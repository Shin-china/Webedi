package customer.handlers.pch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Select;
import com.sap.cds.reflect.CdsService;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.tableservice.PCH03GETTYPEContext;
import cds.gen.tableservice.PCH03LOGDATAContext;
import cds.gen.tableservice.PCH03PRINTHXContext;
import cds.gen.tableservice.PCH03QUERENContext;
import cds.gen.tableservice.PCH03SENDEMAILContext;
import cds.gen.tableservice.PCH04SENDEMAILContext;
import cds.gen.tableservice.PchT03PoItem;
import cds.gen.tableservice.PchT03PoItemPrint;
import cds.gen.tableservice.PchT03PoItemPrint_;
import cds.gen.tableservice.PchT03PoItem_;
import cds.gen.tableservice.TableService_;
import cds.gen.MailBody;
import cds.gen.MailJson;
import cds.gen.mst.T03SapBp;
import cds.gen.pch.T10EmailSendLog;
import cds.gen.sys.T08ComOpD;
import customer.bean.com.UmcConstants;
import customer.comm.tool.StringTool;
import customer.dao.mst.MstD003;
import customer.dao.pch.PchD008Dao;
import customer.dao.pch.PchD010Dao;
import customer.service.pch.PchService;
import customer.service.sys.EmailServiceFun;
import customer.tool.DateTools;
import customer.tool.NumberTool;
import customer.tool.UWebConstants;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.alibaba.excel.util.StringUtils;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Pch03Handler implements EventHandler {

    @Autowired
    private EmailServiceFun emailServiceFun;
    @Autowired
    PchService pchService;

    @Autowired
    PchD010Dao pchD010;
    @Autowired
    PchD008Dao pchD008Dao;
    @Autowired
    MstD003 mstD003;

    /**
     *
     * @param context
     */
    @On(event = "PCH03_QUEREN")
    public void queRn(PCH03QUERENContext context) {
        // 直接从上下文中获取参数
        JSONArray jsonArray = JSONArray.parseArray(context.getParms());
        // 根据传入的po和po明细修改po明细状态
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String po = jsonObject.getString("po");
            String dNo = jsonObject.getString("dNo");
            pchService.setPoStu(po, dNo);
            // 确认后写log表
            pchService.setT09LogData(jsonObject);

        }

        context.setResult("success");
    }

    /**
     * 判断po明细是否和履历表中的一致
     * 
     * @param context
     */
    @On(event = "PCH03_LOGDATA")
    public void logData(PCH03LOGDATAContext context) {
        // 直接从上下文中获取参数
        JSONArray jsonArray = JSONArray.parseArray(context.getParms());
        ArrayList<String> list = new ArrayList<>();
        // 根据传入的po和po明细修改po明细状态
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String po = jsonObject.getString("po");
            String dNo = jsonObject.getString("dNo");
            // 判断po明细是否和履历表中的一致
            Boolean boo = pchService.getT09LogData(po, dNo);
            if (boo) {
                list.add(po);
            }

        }

        context.setResult(JSON.toJSONString(list));
    }

    /**
     *
     * 打印前数据处理
     *
     */
    @After(entity = PchT03PoItemPrint_.CDS_NAME, event = "READ")
    public void beforeReadD03PDF(CdsReadEventContext context,
            Stream<PchT03PoItemPrint> pchd03List) {

        Boolean[] isPrint = new Boolean[1];
        isPrint[0] = false;
        pchd03List.forEach(pchd03 -> {
            // 获取po
            String po = pchd03.getPoNo();
            // 获取明细编号
            String dNo = pchd03.getDNo() + "";
            // 设置前置零
            pchd03.setPodno(po + StringTool.leftPadWithZeros(dNo, 5));
            // 单价
            BigDecimal prc = BigDecimal.ZERO;
            // 设置税的四个
            BigDecimal delPrice = pchd03.getDelPrice();
            BigDecimal poPurQty = pchd03.getPoPurQty();
            // 税额
            BigDecimal taxAmount = pchd03.getTaxAmount();
            // 货币除了日元5其余2
            String currency = pchd03.getCurrency();
            pchd03.setOrderUnitPrice(numFromt(pchd03.getDelPrice()));
            if (delPrice != null && poPurQty != null && taxAmount != null) {
                //// 税抜額
                // 根据货币进行四舍五入
                prc = NumberTool.toScale(delPrice.multiply(poPurQty), currency);

                // 税抜額
                BigDecimal exclusive_tax_amount = prc;

                // 获取税率
                // BigDecimal sl = pchService.getSysD006("税率");
                // BigDecimal tax_amount = exclusive_tax_amount.multiply(sl);

                // 税込額
                BigDecimal inclusive_tax_amount = taxAmount.add(exclusive_tax_amount);

                pchd03.setExclusiveTaxAmount(numFromt(exclusive_tax_amount));
                pchd03.setTaxAmountView(numFromt(taxAmount));
                pchd03.setInclusiveTaxAmount(numFromt(inclusive_tax_amount));
            }

            // 设置検査合区分
            if (!StringTool.isEmpty(pchd03.getImpComp())) {
                pchd03.setCheckOk("受入検査あり");
            }
            pchd03.setQrCode(pchService.getQrCode(pchd03));

            pchd03.setPoPurQty(poPurQty);
            // 納期格式化
            pchd03.setPoDDate2(DateTools.getCurrentDateString(pchd03.getPoDDate(),
                    "yyyy-MM-dd"));
            // 数字格式化
            pchd03.setPoPurQty2(numFromt(pchd03.getPoPurQty()));

            // 得意先コード
            String matId = pchd03.getMatId();
            // matId不能位空，且不能小于2位
            if (matId != null && matId.length() >= 2) {
                String matIdLastTwo = matId.substring(matId.length() - 2);
                System.out.println("=======" + matIdLastTwo);
                T03SapBp bySearch = mstD003.getBySearch(matIdLastTwo);
                if (bySearch != null)
                    pchd03.setBpId(bySearch.getBpId());
            }
            String poSendPDFZWSType = pchService.getPoSendPDFZWSType(pchd03.getPoNo());
            pchd03.setType(poSendPDFZWSType);
            // 复制用作显示
            pchd03.setCop1(pchd03.getPodno());
            pchd03.setCop2(pchd03.getPoPurQty2() == null ? "" : pchd03.getPoPurQty2().toString());
            pchd03.setCop3(pchd03.getPodno());
            // 納入日：
            pchd03.setCop4(DateTools.getCurrentDateString(pchd03.getPoDDate(), "yyyy-MM-dd"));
            pchd03.setCop5(pchd03.getMatId());
            pchd03.setCop6(pchd03.getPoDTxz01());
            pchd03.setCop20(pchd03.getSupplierMat());
            pchd03.setCop7(pchd03.getStorage());
            pchd03.setCop8(pchd03.getCheckOk());
            pchd03.setCop9(pchd03.getBpName1());// name1
            pchd03.setCop10(pchd03.getStorage());
            pchd03.setCop11(pchd03.getPodno());
            pchd03.setCop12(pchd03.getSupplierMat());
            pchd03.setCop13(pchd03.getMatId());

            pchd03.setCop14(pchd03.getCustMaterial());
            pchd03.setCop15(pchd03.getBpId());
            pchd03.setCop16(pchd03.getCheckOk());
            pchd03.setCop17(pchd03.getPrBy());
            pchd03.setCop18(pchd03.getPoPurUnit());

            pchd03.setCop21(pchd03.getPodno());// 海外
            pchd03.setCop24(pchd03.getPodno());// 海外
            pchd03.setCop25(pchd03.getIntNumber());// 海外
            pchd03.setCop27(pchd03.getIntNumber());// 海外
            pchd03.setCop28(pchd03.getIntNumber());// 海外
            pchd03.setCop22(pchd03.getCop27());//
            pchd03.setCop19(pchd03.getCop27());
            // 公司固定值取出

            pchd03.setZws1(
                    pchd03.getPodno() + "\n" + (StringUtils.isBlank(pchd03.getSapCdBy()) ? "" : pchd03.getSapCdBy()));
            pchd03.setZws2(pchd03.getSupplierMat() + "\n" + pchd03.getMatId());
            pchd03.setZws3(pchd03.getManuMaterial());
            pchd03.setZws4(pchd03.getCop2() + "\n" + pchd03.getStorage());
            pchd03.setZws5(pchd03.getPoPurUnit() + "\n" + pchd03.getMemo());
            if (pchd03.getDelPrice() != null && pchd03.getPoPurQty() != null) {
                pchd03.setZws7(numFromt(
                        NumberTool.toScale(pchd03.getDelPrice().multiply(pchd03.getPoPurQty()),
                                currency)));
                pchd03.setZws6(numFromt(pchd03.getDelPrice()));
            }
            // 如果potype为删除是则单价和价格0
            if ("D".equals(pchd03.getPoType())) {
                pchd03.setZws7("0");
                pchd03.setZws6("0");
            }

            pchd03.setZws8(pchd03.getCurrency());
            pchd03.setZws9(DateTools.getCurrentDateString(pchd03.getPoDDate()));

            pchd03.setDate1(DateTools.getCurrentDateString());
            pchd03.setDate2(DateTools.getCurrentDateString());
            pchd03.setDate3(DateTools.getCurrentDateString());
            pchd03.setDate4(DateTools.getCurrentDateString());
            List<T08ComOpD> byList = pchD008Dao.getByList(UmcConstants.C_INFO);
            for (T08ComOpD t08ComOpD : byList) {
                if (UmcConstants.C_INFO_NAME.equals(t08ComOpD.getDName())) {
                    pchd03.setBpName12(t08ComOpD.getValue01());
                }
                if (UmcConstants.C_INFO_POSTCODE.equals(t08ComOpD.getDName())) {
                    pchd03.setPostcode2(t08ComOpD.getValue01());
                }
                if (UmcConstants.C_INFO_REGIONS.equals(t08ComOpD.getDName())) {
                    pchd03.setRegions2(t08ComOpD.getValue01());
                }
                if (UmcConstants.C_INFO_FAX.equals(t08ComOpD.getDName())) {
                    pchd03.setTel2(t08ComOpD.getValue01());
                }
                if (UmcConstants.C_INFO_TEL.equals(t08ComOpD.getDName())) {
                    pchd03.setFax2(t08ComOpD.getValue01());
                }

            }
            StringBuilder qrcode = getQrcode(pchd03);
            pchd03.setQrCode(qrcode.toString());
            pchd03.setCop26(qrcode.toString());
            // jcs2设置
            this.setJcs(pchd03);
        });
    }

    private void setJcs(PchT03PoItemPrint pchd03) {
        pchd03.setJcs1(pchd03.getPodno());
        pchd03.setJcs2(pchd03.getCop22());
        pchd03.setJcs3(pchd03.getStorage());
        pchd03.setJcs4(pchd03.getSapCdBy());
        pchd03.setJcs5(pchd03.getCop1());
        pchd03.setJcs6(pchd03.getMatId());
        pchd03.setJcs7(pchd03.getPoDTxz01());
        pchd03.setJcs8(pchd03.getSupplierMat());
        pchd03.setJcs9(pchd03.getPoDDate2());
        pchd03.setJcs10(pchd03.getPoPurQty2());
        pchd03.setJcs11(pchd03.getCop2());
        pchd03.setJcs12(pchd03.getPoPurUnit());
        pchd03.setJcs13(pchd03.getOrderUnitPrice());
        pchd03.setJcs14(pchd03.getTaxAmountView());
        pchd03.setJcs15(pchd03.getExclusiveTaxAmount());
        pchd03.setJcs16(pchd03.getInclusiveTaxAmount());
        pchd03.setJcs17("");
        pchd03.setJcs18(pchd03.getCheckOk());
        pchd03.setJcs19(pchd03.getBpName1());
        pchd03.setJcs20(pchd03.getCop3());
        pchd03.setJcs21(pchd03.getCop24());

    }

    private StringBuilder getQrcode(PchT03PoItemPrint pchd03) {
        // 获取qrcode
        StringBuilder qrcode = new StringBuilder();
        // 納入日：
        qrcode.append(customer.tool.StringTool.padOrTruncate(pchd03.getPoDDate2(), 10));
        // 品目コード：
        qrcode.append(customer.tool.StringTool.padOrTruncate(pchd03.getCop5(), 40));
        // 品名：
        qrcode.append(customer.tool.StringTool.padOrTruncate(pchd03.getCop6(), 40));
        // P/N：
        qrcode.append(customer.tool.StringTool.padOrTruncate(pchd03.getCustMaterial(),
                40));
        // 納品場所：
        qrcode.append(customer.tool.StringTool.padOrTruncate(pchd03.getStorage(),
                40));
        // 得意先：
        qrcode.append(customer.tool.StringTool.padOrTruncate(pchd03.getBpId(), 10));
        // 検査区分：
        qrcode.append(customer.tool.StringTool.padOrTruncate(pchd03.getCheckOk(),
                12));
        // 依頼者：
        qrcode.append(customer.tool.StringTool.padOrTruncate(pchd03.getPrBy(), 50));
        // 海外発注番号：
        qrcode.append(customer.tool.StringTool.padOrTruncate(pchd03.getIntNumber(),
                18));
        return qrcode;
    }

    private String numFromt(BigDecimal poPurQty) {
        // 获取小数部分
        BigDecimal fractionalPart = poPurQty.subtract(poPurQty.setScale(0,
                RoundingMode.DOWN));

        // 如果小数部分为0或不存在，则返回整数部分
        if (fractionalPart.compareTo(BigDecimal.ZERO) == 0) {
            return poPurQty.setScale(0, RoundingMode.DOWN).toString();
        }
        return poPurQty.toString();
    }

    /**
     *
     * 打印前数据处理
     * PCH_T03_PO_ITEM
     */
    @After(entity = PchT03PoItem_.CDS_NAME, event = "READ")
    public void beforeReadD03(CdsReadEventContext context, Stream<PchT03PoItem> pchd03List) {

        System.out.println("进入后台+++++++++++++++++++++++++++++++==");
        Boolean[] isPrint = new Boolean[1];
        isPrint[0] = false;
        pchd03List.forEach(pchd03 -> {
            // type
            String poSendPDFZWSType = pchService.getPoSendPDFZWSType(pchd03.getPoNo());
            String type = getTypeIss(poSendPDFZWSType);
            pchd03.setEmailAddress(pchService.getEmailAddress(pchd03.getSupplier()));
            // 如果poSendPDFZWSType是REIUSSE
            pchd03.setType(type);
            // 货币除了日元5其余2
            String currency = pchd03.getCurrency();
            // 発注金額 = 価格単位*発注数量 三位小数
            // 根据货币进行四舍五入
            if (pchd03.getDelPrice() != null) {
                pchd03.setIssuedamount(NumberTool.toScale(pchd03.getDelPrice().multiply(pchd03.getPoPurQty()),
                        currency));
            }
            // 如果potype为删除是则单价和价格0
            if ("D".equals(pchd03.getPoType())) {
                pchd03.setIssuedamount(BigDecimal.ZERO);
                pchd03.setDelPrice2(BigDecimal.ZERO);
                pchd03.setPoPurQty2(BigDecimal.ZERO);

            }
            // 设置検査合区分
            if (!StringTool.isEmpty(pchd03.getImpComp())) {
                // pchd03.setImpComp("受入検査あり");
                pchd03.setCheckOk("受入検査あり");
            }
            // 得意先コード
            String matId = pchd03.getMatId();
            // matId不能位空，且不能小于2位
            if (matId != null && matId.length() >= 2) {
                String matIdLastTwo = matId.substring(matId.length() - 2);
                T03SapBp bySearch = mstD003.getBySearch(matIdLastTwo);
                if (bySearch != null)
                    pchd03.setBpId(bySearch.getBpId());
            }

            // 先获取品目最后两位

            // 発注担当者
            String pocdby = pchd03.getPocdby();
            // 如果発注担当者为空或者全为数字
            if (pocdby == null || pocdby.matches("\\d+")) {
                pocdby = pchd03.getSapCdBy();
            }
            pchd03.setByname(pocdby);

            // 获取po
            String po = pchd03.getPoNo();
            // 获取明细编号
            String dNo = pchd03.getDNo() + "";
            // 设置前置零
            pchd03.setId(po + StringTool.leftPadWithZeros(dNo, 5));

        });
    }

    /**
     * 获取类型
     * 
     * @param poSendPDFZWSType
     * @return
     */
    private String getTypeIss(String poSendPDFZWSType) {
        String type = "";
        if (UmcConstants.ZWS_TYPE_3.equals(poSendPDFZWSType)) {
            type = UmcConstants.ZWS_TYPE_3_NAME;
        }
        if (UmcConstants.ZWS_TYPE_1.equals(poSendPDFZWSType)) {
            type = UmcConstants.ZWS_TYPE_1_NAME;
        }
        if (UmcConstants.ZWS_TYPE_2.equals(poSendPDFZWSType)) {
            type = UmcConstants.ZWS_TYPE_2_NAME;
        }
        return type;
    }

    @On(event = PCH03GETTYPEContext.CDS_NAME)
    public void getType(PCH03GETTYPEContext context) {
        // 获取po号
        String po = (String) context.get("parms"); // 根据上下文对象获取数据
        // 根据po号获取对应的邮件发送类型
        String type = pchService.getPoSendPDFZWSType(po);
        context.setResult(type);
    }

    @On(event = "PCH03_PRINTHX")
    public void setPrintHx(PCH03PRINTHXContext context) {
        // 获取po号
        JSONArray jsonArray = JSONArray.parseArray(context.getParms());

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String string = jsonObject.getString("po");
            String string2 = jsonObject.getString("dNo");
            pchService.setT02PrintHx(string, string2);
        }

        context.setResult("success");
    }
}