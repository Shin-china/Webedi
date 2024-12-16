package customer.service.ifm;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import com.alibaba.fastjson.JSON;

import cds.gen.pch.T01PoH;
import cds.gen.pch.T02PoD;
import cds.gen.pch.T03PoC;
import cds.gen.sys.T11IfManager;
import customer.bean.ifm.IFLog;
import customer.bean.pch.Confirmation;
import customer.bean.pch.Item;
import customer.bean.pch.Pch01Sap;
import customer.bean.pch.SapPchRoot;
import customer.dao.pch.PchD001;
import customer.dao.pch.PurchaseDataDao;
import customer.dao.sys.IFSManageDao;
import customer.odata.S4OdataTools;
import customer.service.comm.IfmService;
import customer.service.comm.TranscationService;

@Component
public class Ifm03PoService extends IfmService {


    @Autowired
    private PurchaseDataDao PchDao;

    @Autowired
    PchD001 PchD001dao;

    @Autowired
    private IFSManageDao ifsManageDao;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public SapPchRoot get() throws UnsupportedOperationException, IOException {
        T11IfManager webServiceConfig = ifsManageDao.getByCode("IFM41");
        // 调用 Web Service 的 get 方法
        String response = S4OdataTools.get(webServiceConfig, 0, null, null);
        return JSON.parseObject(response, SapPchRoot.class);

    }

    public void process(IFLog log) throws UnsupportedOperationException, IOException {

        Pch01Sap sap = new Pch01Sap();
        // 获取 Web Service 配置信息

        SapPchRoot sapPchRoot = get();

        HashSet<String> PoSet = getSet(sapPchRoot);

        TransactionDefinition td = transactionInit(); // 初始化事务
        log.setTd(super.transactionInit()); // 事务初始换

        for (String po : PoSet) { // 循环一个PO
            this.insertLog(log);
            this.processOne(td, sap, po, sapPchRoot, log);
        }

        for (String poDel : sap.getPoDelSet()) {
            String supplier = PchDao.getSupplierByPO(poDel);
            String PoComp = PchDao.getPoCompByPO(poDel);

            Boolean isalldele = PchDao.getdelbyPO(poDel);
            if (isalldele) {
                // 全部明细都有删除标记
                sap.getSupplierDeleteMap().put(PoComp + supplier, "来自287行，全部明细都有删除标记，肯定是删除");

            } else {
                // 部分明细有删除标记 ，但是删除标记更新了，所以也是更新。
                sap.getSupplierUpdateMap().put(PoComp + supplier, "来自290行，部分明细有删除标记，肯定是更新");
            }
        }

        PchD001dao.sendMailToCreatePo(sap.getSupplierCreatMap());
        PchD001dao.sendMailToUpdatePo(sap.getSupplierUpdateMap());
        PchD001dao.sendMailToDeletePo(sap.getSupplierDeleteMap());

    }

    private Pch01Sap processOne(TransactionDefinition td, Pch01Sap sap, String poNo, SapPchRoot sapPchRoot, IFLog log) {
        TransactionStatus s = null;

        try {
            s = super.begin(td);

            for (Item Items : sapPchRoot.getItems()) {

                if (!poNo.equals(Items.getPurchaseorder()))
                    continue;

                Boolean dele = false;

                int dn = Integer.parseInt(Items.getPurchaseorderitem());

                if ("L".equals(Items.getPurchasingdocumentdeletioncode())
                        && PchDao.getByID2(Items.getPurchaseorder(), dn) == null) {

                    // 当前02表中没有本条，传进来的是删除标记还是x ，则不进行插入操作

                } else {
                    T01PoH o = T01PoH.create();

                    T01PoH T01poExist = PchDao.getByID(Items.getPurchaseorder());
                    String supplier = Items.getSupplier().replaceFirst("^0+(?!$)", "");
                    String PoComp = Items.getCompanycode();
                    // 头找到了
                    if (T01poExist != null) {

                        // 但是明细没找到，也是创建
                        Boolean T02podnExist = (PchDao.getByID2(Items.getPurchaseorder(), dn)) != null;

                        if (!T02podnExist) {

                            sap.getSupplierCreatMap().put(PoComp + supplier, "来自99行，头找到了，但是明细没找到，创建发信对象"
                                    + Items.getPurchaseorder() + Items.getPurchaseorderitem());

                        }

                    } else { // 头没找到，创建

                        sap.getSupplierCreatMap().put(PoComp + supplier,
                                "来自105行，头没找到，创建发信对象" + Items.getPurchaseorder() + Items.getPurchaseorderitem());

                    }

                    // 记录供应商信息，供后续邮件发送使用
                    // 判断是否需要更新或插入对象 (購買伝票 + 明細番号 + 発注数量 + 納入日付 + 発注単価 + 削除フラグ+MRP減少数量 有变化时才是变更对象
                    // podn 找不到则也是变更对象)
                    Boolean isUpdateObj = PchDao.getByPoDnUpdateObj(Items);

                    if (isUpdateObj) {

                        if (!sap.getSupplierUpdateMap().containsKey(PoComp + supplier)) {

                            sap.getSupplierUpdateMap().put(PoComp + supplier,
                                    "来自118行，四个字段其中有修改，肯定是更新" + Items.getPurchaseorder() + Items.getPurchaseorderitem());// delflag
                                                                                                                        // 单独考虑。

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

                    LocalDate cddate = LocalDate.parse(Items.getCreationdate(), formatter);
                    LocalDate poDate = LocalDate.parse(Items.getPurchaseorderdate(), formatter); // 转换字符串为 //

                    o.setCdDate(cddate);
                    o.setPoDate(poDate);

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

                    o2.setTaxCode(Items.getTaxcode());// 1125新需求 追加

                    LocalDate deliveryDate = LocalDate.parse(Items.getSchedulelinedeliverydate(), formatter);
                    o2.setPoDDate(deliveryDate);

                    // 将字符串转换为 BigDecimal
                    BigDecimal netpriceAmount = new BigDecimal(Items.getNetpriceamount());
                    BigDecimal netPriceQuantity = new BigDecimal(Items.getNetpricequantity());
                    BigDecimal taxAmount = new BigDecimal(Items.getTaxamount());// 1125新需求 追加

                    o2.setTaxAmount(taxAmount);

                    // 检查 netPriceQuantity 是否为 0，以避免除以 0 的情况
                    if (netPriceQuantity.compareTo(BigDecimal.ZERO) != 0) {
                        // 计算并设置价格，使用指定为具有两位小数的舍入模式
                        BigDecimal delPrice = netpriceAmount.divide(netPriceQuantity, 2, RoundingMode.HALF_UP);
                        o2.setDelPrice(delPrice);
                    } else {
                        // 处理除以 0 的情况，例如设置为 0 或抛出异常
                        o2.setDelPrice(BigDecimal.ZERO); // 或其他逻辑
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

                            deliveryDate = LocalDate.parse(conf.getDeliverydate(), formatter);

                            o3.setDeliveryDate(deliveryDate);

                            o3.setQuantity(new BigDecimal(conf.getConfirmedquantity()));

                            o3.setRelevantQuantity(new BigDecimal(conf.getMrprelevantquantity()));

                            o3.setExtNumber(conf.getSupplierconfirmationextnumber());

                            PchDao.modifyT03(o3);
                        }

                    }

                    // ------------------------------------------------------------------------------以上是对t02的修改

                    // 首先是，dele被修改的情况下再继续。
                    if (isDelflaghavechange) {

                        sap.getPoDelSet().add(Items.getPurchaseorder());

                    }
                }

            }
            log.addSuccessNum();
            this.commit(s);

        } catch (Exception e) {
        } finally {

            this.rollback(s);
        }

        return sap;
    }

    private HashSet<String> getSet(SapPchRoot sapPchRoot) {
        HashSet<String> PoSet = new HashSet<>();
        for (Item Items : sapPchRoot.getItems()) {
            String poNo = Items.getPurchaseorder();
            if (PoSet.contains(poNo)) {

            } else {
                PoSet.add(poNo);
            }
        }
        return PoSet;
    }

}
