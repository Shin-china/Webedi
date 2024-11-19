package customer.service.pch;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

// import org.apache.tomcat.util.openssl.pem_password_cb;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import org.checkerframework.checker.units.qual.t;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cds.services.messages.Messages;

import cds.gen.mst.T01SapMat;
import cds.gen.mst.T03SapBp;
import cds.gen.pch.T03PoC;
import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;
import cds.gen.sys.T11IfManager;
import cds.gen.tableservice.PoTypePop;
import cds.gen.tableservice.PCHT07QuoItemMax1;
import customer.dao.mst.MstD001;
import customer.dao.mst.MstD003;
import customer.bean.pch.Pch07;
import customer.service.Service;
import customer.tool.UniqueIDTool;
import customer.dao.pch.PchD006;
import customer.dao.pch.PchD007;
import customer.bean.pch.Pch01;
import customer.bean.pch.Pch07DataList;
import customer.comm.tool.MessageTools;
import customer.dao.sys.DocNoDao;
// import customer.usapbe.comm.tool.UniqueIDTool;
// import customer.usapbe.service.Service;
import customer.dao.sys.IFSManageDao;
import customer.odata.S4OdataTools;

@Component
public class Pch07Service {

    private boolean isValidDateFormat(String date) {
        // 使用正则表达式检查日期格式 YYYY/MM/DD
        String regex = "^\\d{4}/\\d{2}/\\d{2}$";
        return date != null && date.matches(regex);
    }

    @Autowired
    private MstD001 mstD001;
    @Autowired
    private MstD003 mstD003;
    @Autowired
    private PchD006 pchD006;
    @Autowired
    private PchD007 pchD007;
    @Autowired
    private IFSManageDao ifsManageDao;
    @Autowired
    private DocNoDao docDao;

    public void detailsCheck(Pch07DataList list) {
        // 遍历上传的每条记录,
        for (Pch07 item : list.getList()) {
            // 1. 检查 SAP 品目代码 (MATERIAL_NUMBER -> MAT_ID)

            String matno = item.getMATERIAL_NUMBER();
            String plant = item.getPLANT_ID();
            String bpno = item.getBP_NUMBER();
            String valstart = item.getVALIDATE_START();
            String valend = item.getVALIDATE_END();
            String custMaterial = item.getCUST_MATERIAL();
            String manu = item.getMANUFACT_MATERIAL();

            // 先检查 MATERIAL_NUMBER 和 CUST_MATERIAL 字段是否至少有一个有值
            if ((matno == null || matno.isEmpty()) && (custMaterial == null || custMaterial.isEmpty())
                    && (manu == null || manu.isEmpty())) {
                // 如果三个字段都为空，报错
                item.setSUCCESS(false);
                item.setMESSAGE("SAP 品目コード、顧客品番、メーカー品番は少なくとも一つを入力してください。");
                item.setRESULT("失敗");
                item.setI_CON("sap-icon://error");
                item.setSTATUS("Error");
            } else {
                item.setSUCCESS(true);
                item.setRESULT("成功");
                item.setI_CON("sap-icon://sys-enter-2");
                item.setSTATUS("Success");
            }

            if (!item.getSUCCESS()) {
                // 如果前面已经失败，直接跳过后续检查
                continue;
            }

            // 如果 MATERIAL_NUMBER 有值，检查 SAP 品目代码
            if (matno != null && !matno.isEmpty()) {
                // 1. 检查 SAP 品目代码 (MATERIAL_NUMBER -> MAT_ID)
                T01SapMat matid = mstD001.getByID(matno);
                if (matid == null || "Y".equals(matid.getDelFlag())) {
                    item.setSUCCESS(false);
                    item.setMESSAGE("SAP品目コード" + matno + "が登録されていません。チェックしてください。");
                    item.setRESULT("失敗");
                    item.setI_CON("sap-icon://error");
                    item.setSTATUS("Error");
                } else {
                    item.setSUCCESS(true);
                    item.setRESULT("成功");
                    item.setI_CON("sap-icon://sys-enter-2");
                    item.setSTATUS("Success");
                }
            }

            if (!item.getSUCCESS()) {
                // 如果前面已经失败，直接跳过后续检查
                continue;
            }

            // // 2. 检查供应商 ID (BP_NUMBER -> BP_ID)
            // T03SapBp bpid = mstD003.getByID(bpno);
            // if (bpid == null || "Y".equals(bpid.getDelFlag())) {
            // item.setSUCCESS(false);
            // item.setMESSAGE("仕入先" + bpno + "が登録されていません。チェックしてください。");
            // item.setRESULT("失敗");
            // item.setI_CON("sap-icon://error");
            // item.setSTATUS("Error");
            // }else {
            // item.setSUCCESS(true);
            // item.setRESULT("成功");
            // item.setI_CON("sap-icon://sys-enter-2");
            // item.setSTATUS("Success");
            // }

            // if (!item.getSUCCESS()) {
            // // 如果前面已经失败，直接跳过后续检查
            // continue;
            // }

            // 3. 检查日期格式 (VALIDATE_START, VALIDATE_END)
            if (!isValidDateFormat(valstart) || !isValidDateFormat(valend)) {
                item.setSUCCESS(false);
                item.setMESSAGE("日付形式はYYYY/MM/DDではないので、調整してください。");
                item.setRESULT("失敗");
                item.setI_CON("sap-icon://error");
                item.setSTATUS("Error");
            } else {
                item.setSUCCESS(true);
                item.setRESULT("成功");
                item.setI_CON("sap-icon://sys-enter-2");
                item.setSTATUS("Success");
            }
        }
    }

    public void detailsSave(Pch07DataList list) throws Exception {
        // 获取map储存key值与最大明细值，与番号
        // 番号
        HashMap<String, String> noMap = new HashMap<>();
        // 最大明细值
        HashMap<String, String> dNoMap = new HashMap<>();

        for (Pch07 data : list.getList()) {

            String plant = data.getPLANT_ID() == null ? "" : data.getPLANT_ID();
            String matno = data.getMATERIAL_NUMBER() == null ? "" : data.getMATERIAL_NUMBER();
            String cust = data.getCUST_MATERIAL() == null ? "" : data.getCUST_MATERIAL();
            String manu = data.getMANUFACT_MATERIAL() == null ? "" : data.getMANUFACT_MATERIAL();

            // 首先找有没有之前这个noMap的值
            String key = plant + matno + cust + manu;

            if (noMap.containsKey(key)) {
                // 如果存在则取出最大明细值进行+1

                String dNo = dNoMap.get(key);
                int dNoInt = Integer.parseInt(dNo);
                dNoInt++;
                dNoMap.put(key, String.valueOf(dNoInt));
            }
            // 如果不存在则创建noMap和从1开始的最大明细值
            else {
                // 番号
                String no = docDao.getPJNo(plant, 1);
                noMap.put(key, no);
                // 最大明细值
                dNoMap.put(key, "1");
            }

            data.setQUO_NUMBER(noMap.get(key));
            // 增加明细番号记录
            data.setQUO_ITEM(dNoMap.get(key));
            // 需要采番的情况创建T06
            this.createT06(data);

            // 创建T07
            this.createT07(data);
            data.setMESSAGE("購買見積は成功にアップロードしました");
            data.setQUO_NUMBER(data.getQUO_NUMBER());
            data.setQUO_ITEM(data.getQUO_ITEM());
        }
    }

    private void createT07(Pch07 data) throws IOException {

        String response = getResponse(data);
        System.out.println(response);
        T07QuotationD t07QuotationD2 = T07QuotationD.create();

        JSONObject jb = (JSONObject) JSON.parse(response);
        T01SapMat number = mstD001.getByID(data.getMATERIAL_NUMBER());
        T03SapBp bpid = mstD003.getByID(data.getBP_NUMBER());

        // 解析 JSON 字符串
        JSONObject jsonObject = (JSONObject) JSON.parse(response);

        // 获取 Items 数组
        JSONArray itemsArray = jsonObject.getJSONArray("Items");

        // 确保 Items 数组不为空
        if (itemsArray != null && itemsArray.size() > 0) {
            // 取第一个 Item
            JSONObject firstItem = itemsArray.getJSONObject(0);

            // // 检查字符串是否有效
            // if (leadTimeStr != null && !leadTimeStr.isEmpty() && !leadTimeStr.equals("0")) {
            //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            //     LocalDate leadTime = LocalDate.parse(leadTimeStr, formatter);
            //     // t07QuotationD2.setLeadTime(leadTime);
            // } else {
            //     // 处理无效值的情况，例如设置为 null 或者提供默认值
            //     t07QuotationD2.setLeadTime(null); // 或者根据需要设置默认值
            // }

            // 从 JSON 获取 DEC3 类型的值为 BigDecimal
            BigDecimal leadTimeValue = firstItem.getBigDecimal("Materialplanneddeliverydurn");

            // 检查值是否有效
            if (leadTimeValue != null) {
                // 将 BigDecimal 转换为 int 并设置
                t07QuotationD2.setLeadTime(leadTimeValue.intValue());
            } else {
                // 如果值为 null，设置为默认值或 null
                t07QuotationD2.setLeadTime(null); // 或者设置为 0
            }

            // 设置 Qty
            Double qtyDouble = data.getQTY();
            BigDecimal qtyBigDecimal = qtyDouble != null ? BigDecimal.valueOf(qtyDouble) : null;
            t07QuotationD2.setQty(qtyBigDecimal);

            // 设置 Price
            String priceStr = firstItem.getString("Netpriceamount");
            BigDecimal priceBigDecimal = (priceStr != null && !priceStr.isEmpty()) ? new BigDecimal(priceStr) : null;
            t07QuotationD2.setPrice(priceBigDecimal);

            // 获取 Baseunit 并设置到表字段中
         
            t07QuotationD2.setUnit(firstItem.getString("Baseunit"));
            t07QuotationD2.setOriginalCou(firstItem.getString("Suppliercertorigincountry"));
            t07QuotationD2.setPriceControl(firstItem.getString("Pricingdatecontrol"));
            t07QuotationD2.setMoq(firstItem.getString("Minimumpurchaseorderquantity"));
            t07QuotationD2.setIncoterms(firstItem.getString("Incotermsclassification"));
            t07QuotationD2.setIncotermsText(firstItem.getString("Incotermslocation1"));
            t07QuotationD2.setCurrency(firstItem.getString("Currency"));
            t07QuotationD2.setSupplierMat(firstItem.getString("SupplierMaterialNumber"));

        }

        t07QuotationD2.setQuoNumber(data.getQUO_NUMBER());
        t07QuotationD2.setQuoItem(Integer.parseInt(data.getQUO_ITEM()));
        t07QuotationD2.setMaterialNumber(data.getMATERIAL_NUMBER());
        t07QuotationD2.setInitialObj(data.getINITIAL_OBJ());
        t07QuotationD2.setStatus("1");
        // t07QuotationD2.setMaterial(number.getMatName());
        // t07QuotationD2.setMaker(number.getManuCode());

            // 处理与 number 相关的逻辑
            if (number != null && number.getMatName() != null && !number.getMatName().isEmpty()) {
                t07QuotationD2.setMaterial(number.getMatName());
            } else {
                t07QuotationD2.setMaterial(null); // 如果 MatName 为 null 或空，设置为 null
            }

            if (number != null && number.getManuCode() != null && !number.getManuCode().isEmpty()) {
                t07QuotationD2.setMaker(number.getManuCode());
            } else {
                t07QuotationD2.setMaker(null); // 如果 ManuCode 为 null 或空，设置为 null
            }

        // t07QuotationD2.setManufactMaterial(number.getManuMaterial());
        if (bpid != null && bpid.getBpName1() != null && !bpid.getBpName1().isEmpty()) {
            t07QuotationD2.setUwebUser(bpid.getBpName1());
        } else {
            t07QuotationD2.setUwebUser(null);
        }        
        // t07QuotationD2.setUwebUser(bpid.getBpName1());
        t07QuotationD2.setCustMaterial(data.getCUST_MATERIAL());
        t07QuotationD2.setManufactMaterial(data.getMANUFACT_MATERIAL());
        t07QuotationD2.setSalesNumber(data.getSALES_NUMBER());

        pchD007.insert(t07QuotationD2);
    }

    private void createT06(Pch07 data) {
        T06QuotationH t06QuotationH = T06QuotationH.create(UniqueIDTool.getUUID());

        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        // 将字符串转换为 LocalDate
        LocalDate validateStart = LocalDate.parse(data.getVALIDATE_START(), formatter);
        LocalDate validateEnd = LocalDate.parse(data.getVALIDATE_END(), formatter);

        // 设置转换后的日期
        t06QuotationH.setValidateStart(validateStart);
        t06QuotationH.setValidateEnd(validateEnd);

        t06QuotationH.setQuoNumber(data.getQUO_NUMBER());
        t06QuotationH.setPlantId(data.getPLANT_ID());

        pchD006.insert(t06QuotationH);
    }

    private String getResponse(Pch07 data) throws IOException {
        ArrayList ar = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        map.put("material_number", data.getMATERIAL_NUMBER());
        map.put("bp_number", data.getBP_NUMBER());
        map.put("plant_id", data.getPLANT_ID());
        ar.add(map);

        // 获取 Web Service 配置信息
        T11IfManager webServiceConfig = ifsManageDao.getByCode("MM036");
        // 调用 Web Service 的 get 方法
        String response = S4OdataTools.post(webServiceConfig, JSON.toJSONString(ar), null);
        return response;

    }
}