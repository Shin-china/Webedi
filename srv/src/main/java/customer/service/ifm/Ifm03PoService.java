package customer.service.ifm;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cds.gen.MailBody;
import cds.gen.MailJson;
import cds.gen.pch.T01PoH;
import cds.gen.pch.T02PoD;
import cds.gen.pch.T03PoC;
import cds.gen.sys.T08ComOpD;
import cds.gen.sys.T11IfManager;
import customer.bean.pch.Confirmation;
import customer.bean.pch.Item;
import customer.bean.pch.SapPchRoot;
import customer.dao.pch.PurchaseDataDao;
import customer.dao.sys.IFSManageDao;
import customer.dao.sys.SysD008Dao;
import customer.odata.S4OdataTools;
import customer.service.sys.EmailServiceFun;

@Component
public class Ifm03PoService {

    @Autowired
    private PurchaseDataDao PchDao;

    @Autowired
    private IFSManageDao ifsManageDao;

    @Autowired
    private EmailServiceFun emailServiceFun;

    @Autowired
    private SysD008Dao sysd008dao;

    public String syncPo() {

        try {
            // 获取 Web Service 配置信息
            T11IfManager webServiceConfig = ifsManageDao.getByCode("IFM41");

            if (webServiceConfig != null) {
                // 调用 Web Service 的 get 方法
                String response = S4OdataTools.get(webServiceConfig, 1000, null, null);

                System.out.println(response);

                SapPchRoot sapPchRoot = JSON.parseObject(response, SapPchRoot.class);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

                Map<String, String> supplierCreatMap = new HashMap<>();
                Map<String, String> supplierUpdateMap = new HashMap<>();
                Map<String, String> supplierDeleteMap = new HashMap<>();

                Collection<MailJson> mailJsonList = new ArrayList<>();

                String H_CODE = "MM0004";

                for (Item Items : sapPchRoot.getItems()) {
                    Boolean dele = false;

                    int dn = Integer.parseInt(Items.getPurchaseorderitem());

                    if ("L".equals(Items.getPurchasingdocumentdeletioncode())
                            && PchDao.getByID2(Items.getPurchaseorder(), dn) == null) {

                        // 当前02表中没有本条，传进来的是删除标记还是x ，则不进行插入操作

                    } else {
                        T01PoH o = T01PoH.create();

                        T01PoH T01poExist = PchDao.getByID(Items.getPurchaseorder());
                        String supplier = Items.getSupplier().replaceFirst("^0+(?!$)", "");

                        // 头找到了
                        if (T01poExist != null) {

                            // 但是明细没找到，也是创建
                            Boolean T02podnExist = (PchDao.getByID2(Items.getPurchaseorder(), dn)) != null;

                            if (!T02podnExist) {

                                supplierCreatMap.put(supplier, "来自99行，头找到了，但是明细没找到，创建发信对象");

                            }

                        } else { // 头没找到，创建

                            supplierCreatMap.put(supplier, "来自105行，头没找到，创建发信对象");

                        }

                        // 记录供应商信息，供后续邮件发送使用
                        // 判断是否需要更新或插入对象 (購買伝票 + 明細番号 + 発注数量 + 納入日付 + 発注単価 + 削除フラグ+MRP減少数量 有变化时才是变更对象
                        // podn 找不到则也是变更对象)
                        Boolean isUpdateObj = PchDao.getByPoDnUpdateObj(Items);

                        if (isUpdateObj) {

                            if (!supplierUpdateMap.containsKey(supplier)) {

                                supplierUpdateMap.put(supplier, "来自118行，四个字段其中有修改，肯定是更新");// delflag 单独考虑。

                            }
                            ;
                        }

                        // 判断是否需要删除对象 (删除标记为X)
                        // 前提条件：
                        // 1.有对删除标记的修改

                        // 两种情况
                        // 1.当前po下的所有明细都有删除标记
                        // 2.当前po下的所有明细不是都有删除标记
                        Boolean isDelflaghavechange = PchDao.getDelflaghavechange(Items);

                        // 添加创建时间
                        // o.setCreationdate(Items.getCreationdate());

                        // 添加po创建人
                        // o.setSapCdBy(Items.getCreatedbyuser());

                        o.setPoNo(Items.getPurchaseorder());
                        o.setPoBukrs(Items.getCompanycode());

                        try {
                            LocalDate cddate = LocalDate.parse(Items.getCreationdate(), formatter);
                            LocalDate poDate = LocalDate.parse(Items.getPurchaseorderdate(), formatter); // 转换字符串为 //

                            o.setCdDate(cddate);
                            o.setPoDate(poDate);

                        } catch (DateTimeParseException e) {
                            System.out.println("日期格式无效: " + e.getMessage()); // 处理格式错误
                        }
                        // 去除前导 0 后的
                        o.setSupplier(supplier);
                        o.setPocdby(Items.getCorrespncinternalreference());
                        o.setSapCdBy(Items.getCreatedbyuser());

                        PchDao.modify(o);
                        // ------------------------------------------------------------------------------------以上是对t01的修改

                        T02PoD o2 = T02PoD.create();
                        o2.setPoNo(Items.getPurchaseorder());
                        o2.setDNo(dn);
                        o2.setMatId(Items.getMaterial());
                        o2.setPlantId(Items.getPlant());
                        o2.setPoDTxz01(Items.getPurchaseorderitemtext());
                        Items.getOrderquantity();
                        o2.setPoPurQty(new BigDecimal(Items.getOrderquantity()));
                        o2.setPoPurUnit(Items.getPurchaseorderquantityunit());
                        o2.setCurrency(Items.getDocumentcurrency());
                        o2.setIntNumber(Items.getInternationalarticlenumber());
                        o2.setPrBy(Items.getRequisitionername());

                        try {

                            LocalDate deliveryDate = LocalDate.parse(Items.getSchedulelinedeliverydate(), formatter);
                            o2.setPoDDate(deliveryDate);

                        } catch (DateTimeParseException e) {
                            e.printStackTrace();

                        }

                        try {
                            // 将字符串转换为 BigDecimal
                            BigDecimal netpriceAmount = new BigDecimal(Items.getNetpriceamount());
                            BigDecimal netPriceQuantity = new BigDecimal(Items.getNetpricequantity());

                            // 检查 netPriceQuantity 是否为 0，以避免除以 0 的情况
                            if (netPriceQuantity.compareTo(BigDecimal.ZERO) != 0) {
                                // 计算并设置价格，使用指定为具有两位小数的舍入模式
                                BigDecimal delPrice = netpriceAmount.divide(netPriceQuantity, 2, RoundingMode.HALF_UP);
                                o2.setDelPrice(delPrice);
                            } else {
                                // 处理除以 0 的情况，例如设置为 0 或抛出异常
                                o2.setDelPrice(BigDecimal.ZERO); // 或其他逻辑
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace(); // 处理转换错误
                        } catch (Exception e) {
                            e.printStackTrace(); // 处理其他可能的异常
                        }

                        o2.setDelAmount(new BigDecimal(Items.getNetamount()));
                        o2.setUnitPrice(new BigDecimal(Items.getNetpricequantity()));
                        o2.setStorageLoc(Items.getStoragelocation());
                        o2.setStorageTxt(Items.getStoragelocationname());
                        o2.setMemo(Items.getPlainlongtext());

                        o2.setSupplierMat(Items.getSupplierMaterialNumber());

                        if (Items.getPurchasingdocumentdeletioncode().equals("L")) {

                            dele = true;
                        }

                        //
                        PchDao.modify2(o2, dele);

                        for (Confirmation conf : Items.getConfirmation()) {

                            if (conf != null) {

                                T03PoC o3 = T03PoC.create();
                                o3.setPoNo(conf.getPurchaseorder());

                                o3.setDNo(Integer.parseInt(conf.getPurchaseorderitem()));

                                o3.setSeq(Integer.parseInt(conf.getSequentialnmbrofsuplrconf()));

                                try {
                                    LocalDate deliveryDate = LocalDate.parse(conf.getDeliverydate(), formatter);

                                    o3.setDeliveryDate(deliveryDate);

                                } catch (DateTimeParseException e) {

                                    System.out.println("日期格式错误: " + e.getMessage());
                                }

                                o3.setQuantity(new BigDecimal(conf.getConfirmedquantity()));

                                o3.setRelevantQuantity(new BigDecimal(conf.getMrprelevantquantity()));

                                o3.setExtNumber(conf.getSupplierconfirmationextnumber());

                                PchDao.modifyT03(o3);
                            }

                        }

                        // ------------------------------------------------------------------------------以上是对t02的修改

                        // 首先是，dele被修改的情况下再继续。
                        if (isDelflaghavechange) {
                            Boolean isalldele = PchDao.getdelbyPO(Items.getPurchaseorder());

                            if (isalldele) {
                                // 全部明细都有删除标记
                                supplierDeleteMap.put(supplier, "来自159行，全部明细都有删除标记，肯定是删除");

                            } else {
                                // 部分明细有删除标记
                                supplierUpdateMap.put(supplier, "来自263行，部分明细有删除标记，肯定是更新");

                            }
                        }

                        // 如果deleflag 是 删除，也应该判断一下 是不是所有的都是删除
                        if ("L".equals(Items.getPurchasingdocumentdeletioncode())) {
                            Boolean isalldele1 = PchDao.getdelbyPO(Items.getPurchaseorder());

                            if (isalldele1) {
                                // 全部明细都有删除标记
                                supplierDeleteMap.put(supplier, "来自159行，全部明细都有删除标记，肯定是删除");

                            } else {
                                // 部分明细有删除标记
                                supplierUpdateMap.put(supplier, "来自263行，部分明细有删除标记，肯定是更新");

                            }
                        }

                    }
                }
                // 创建发信
                if (supplierCreatMap.size() > 0) {
                    for (Map.Entry<String, String> entry : supplierCreatMap.entrySet()) {

                        MailJson mailJson = MailJson.create();
                        List<T08ComOpD> emailadd = sysd008dao.getmailaddByHcodeV1(H_CODE, entry.getKey());
                        if (emailadd != null) {
                            mailJson.setTemplateId("UWEB_M004_C");
                            mailJson.setMailTo(emailadd.get(0).getValue02());
                            mailJson.setMailBody(createMailBody(emailadd)); // 设置邮件内容（MailBody）
                            // 添加到邮件列表
                            mailJsonList.add(mailJson);
                            // 调用邮件发送服务
                            try {
                                emailServiceFun.sendEmailFun(mailJsonList);
                                // 设置操作结果
                                return ("メール送信に成功しました。");
                            } catch (Exception e) {
                                // 处理发送邮件的异常
                                return e.getMessage();
                            }
                        }
                    }

                }
                // 更新发信
                if (supplierUpdateMap.size() > 0) {
                    for (Map.Entry<String, String> entry : supplierUpdateMap.entrySet()) {
                        MailJson mailJson = MailJson.create();
                        List<T08ComOpD> emailadd = sysd008dao.getmailaddByHcodeV1(H_CODE, entry.getKey());
                        if (emailadd != null) {
                            mailJson.setTemplateId("UWEB_M004_U");
                            mailJson.setMailTo(emailadd.get(0).getValue02());
                            mailJson.setMailBody(createMailBody(emailadd)); // 设置邮件内容（MailBody）
                            // 添加到邮件列表
                            mailJsonList.add(mailJson);
                            // 调用邮件发送服务
                            try {
                                emailServiceFun.sendEmailFun(mailJsonList);
                                // 设置操作结果
                                return ("メール送信に成功しました。");
                            } catch (Exception e) {
                                // 处理发送邮件的异常
                                return e.getMessage();
                            }
                        }
                    }

                }
                // 删除发信
                if (supplierDeleteMap.size() > 0) {
                    for (Map.Entry<String, String> entry : supplierDeleteMap.entrySet()) {
                        MailJson mailJson = MailJson.create();
                        List<T08ComOpD> emailadd = sysd008dao.getmailaddByHcodeV1(H_CODE, entry.getKey());
                        if (emailadd != null) {
                            mailJson.setTemplateId("UWEB_M004_D");
                            mailJson.setMailTo(emailadd.get(0).getValue02());
                            mailJson.setMailBody(createMailBody(emailadd)); // 设置邮件内容（MailBody）
                            // 添加到邮件列表
                            mailJsonList.add(mailJson);
                            // 调用邮件发送服务
                            try {
                                emailServiceFun.sendEmailFun(mailJsonList);
                                // 设置操作结果
                                return ("メール送信に成功しました。");
                            } catch (Exception e) {
                                // 处理发送邮件的异常
                                return e.getMessage();
                            }
                        }
                    }

                }

            } else {

                return "同步失败";

            }

        } catch (Exception e) {

            e.printStackTrace();

            return e.getMessage();

        }
        System.out.println("po接口测试结束");
        return "同步成功";

    }

    // 创建 MailBody 的集合
    private Collection<MailBody> createMailBody(List<T08ComOpD> emailadd) {
        Collection<MailBody> bodies = new ArrayList<>();
        MailBody body = MailBody.create();
        body.setObject("vendor"); // 根据具体需求设置
        body.setValue(emailadd.get(0).getValue03()); // 使用参数内容
        bodies.add(body);
        return bodies;
    }

}
