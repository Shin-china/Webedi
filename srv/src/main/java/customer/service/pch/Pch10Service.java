package customer.service.pch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.formula.functions.T;
import org.checkerframework.checker.units.qual.t;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.alibaba.fastjson.annotation.JSONField;

import cds.gen.mst.T01SapMat;
import cds.gen.mst.T03SapBp;
import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;
import cds.gen.sys.T07ComOpH;
import cds.gen.sys.T08ComOpD;
import ch.qos.logback.core.status.Status;
import customer.bean.ifm.IFLog;
import customer.bean.pch.Pch08;
import customer.bean.pch.Pch10;
import customer.bean.pch.Pch10DataList;
import customer.bean.pch.Pch10L;
import customer.bean.pch.Pch10Save;
import customer.bean.pch.Pch10SaveDataList;
import customer.bean.tmpl.pch04excel;
import customer.comm.tool.DateTools;
import customer.dao.mst.MstD001;
import customer.dao.mst.MstD003;
import customer.dao.pch.Pch08Dao;
import customer.dao.pch.Pch10Dao;
import customer.dao.pch.PchD001;
import customer.dao.pch.PchD003;
import customer.dao.pch.PchD007;
import customer.dao.sys.IFSManageDao;
import customer.service.comm.IfmService;

@Component
public class Pch10Service extends IfmService{

    @Autowired
    private Pch10Dao Pch10Dao;

    @Autowired
    private Pch08Dao pch08Dao;

    @Autowired
    private PchD007 pch07Dao;

    @Autowired
    private MstD001 mstD001;

    @Autowired
    private MstD003 mstD003;




    public void setQuostatus(String sal_Num) {

        // 获取明细的status
        String Status = Pch10Dao.getStatus(sal_Num);
        String Hstatus = Pch10Dao.getHStatus(sal_Num);

        List<T07QuotationD> items = Pch10Dao.getItems(sal_Num);

        // for (T07QuotationD item : items) {

        // switch (item.getStatus()) {

        // case "1":
        // item.setStatus("2");
        // Pch10Dao.Setstatus(item);

        // case "2":
        // item.setStatus("2");
        // Pch10Dao.Setstatus(item);

        // case "3":
        // item.setStatus("4");
        // Pch10Dao.Setstatus(item);

        // case "4":
        // item.setStatus("4");
        // Pch10Dao.Setstatus(item);

        // default:

        // }

        // }

        // 获取全部条目
        List<T07QuotationD> items2 = Pch10Dao.getItems(sal_Num);
        String headerStatus = "5"; // 假设全部为"5"
        boolean allStatus2 = true; // 用于记录是否所有项状态都为"2"
        for (T07QuotationD item2 : items2) {

            if (item2.getStatus().equals("1")) {
                headerStatus = "1";
                break; // 优先级最高，直接退出循环
            }

            if (item2.getStatus().equals("2")) {
                // 保持 allStatus2 为 true
            } else {
                allStatus2 = false; // 一旦发现不是 "2"，设为 false
            }

            if (item2.getStatus().equals("3") && headerStatus.equals("5")) {
                headerStatus = "3";
            } else if (item2.getStatus().equals("4") && headerStatus.equals("5")) {
                headerStatus = "4";
            }
        }

        // 如果全部状态都为 "2"，并且当前 headerStatus 为 "1"，将 headerStatus 更新为 "2"
        if (allStatus2 && Hstatus.equals("1")) {
            headerStatus = "2";
        }

        // 输出头部状态
        System.out.println("Header Status: " + headerStatus);

    }

    public Pch10SaveDataList check(Pch10SaveDataList list) {

        return list;

    }

    public void detailsSave(Pch10SaveDataList list) {

        list.getList().forEach(value -> {

            List<T07QuotationD> oldItems = getOldT07Data(value);
            List<T07QuotationD> newItems = extractT07Data(value);

            pch08Dao.updatePch08(oldItems, newItems);

        });
    }

    //
    public List<T07QuotationD> getOldT07Data(Pch10Save pch10) {
        return pch08Dao.getT07ByQuoNumberItem(pch10.getQUO_NUMBER(), pch10.getQUO_ITEM());
    }

    public List<T07QuotationD> extractT07Data(Pch10Save pch08) {
        List<T07QuotationD> items = getOldT07Data(pch08);
        if (items == null) {
            return null;
        }

        List<T07QuotationD> newItems = new ArrayList<>();

        items.forEach(value -> {
            T07QuotationD t07New = T07QuotationD.create();
            BeanUtils.copyProperties(value, t07New);

            t07New.setId(null); // Create New data
            t07New.setStatus("3");
            t07New.setUpTime(DateTools.getInstantNow());
            t07New.setUpBy(pch08Dao.getUserId());

            t07New.setRefrenceNo(pch08.getREFRENCE_NO());
            t07New.setMaterialNumber(pch08.getMATERIAL_NUMBER());
            t07New.setCustMaterial(pch08.getCUST_MATERIAL());
            t07New.setManufactMaterial(pch08.getMANUFACT_MATERIAL());
            t07New.setAttachment(pch08.getAttachment());
            t07New.setMaterial(pch08.getMaterial());
            t07New.setMaker(pch08.getMAKER());
            t07New.setUwebUser(pch08.getUWEB_USER());
            t07New.setBpNumber(pch08.getBP_NUMBER());
            t07New.setPersonNo1(pch08.getPERSON_NO1());
            t07New.setYlp(pch08.getYLP());
            t07New.setManul(pch08.getMANUL());
            t07New.setManufactCode(pch08.getMANUFACT_CODE());
            t07New.setCustomerMmodel(pch08.getCUSTOMER_MMODEL());
            t07New.setMidQf(pch08.getMID_QF());
            t07New.setSmallQf(pch08.getSMALL_QF());
            t07New.setOtherQf(pch08.getOTHER_QF());
            t07New.setQty(pch08.getQTY());
            t07New.setCurrency(pch08.getCURRENCY());
            t07New.setPrice(pch08.getPRICE());
            t07New.setPriceControl(pch08.getPRICE_CONTROL());
            t07New.setLeadTime(pch08.getLEAD_TIME());
            t07New.setMoq(pch08.getMOQ());
            t07New.setUnit(pch08.getUNIT());
            t07New.setSpq(pch08.getSPQ());
            t07New.setKbxt(pch08.getKBXT());
            t07New.setProductWeight(pch08.getPRODUCT_WEIGHT());
            t07New.setOriginalCou(pch08.getORIGINAL_COU());
            t07New.setEol(pch08.getEOL());
            t07New.setIsboi(pch08.getISBOI());
            t07New.setIncoterms(pch08.getIncoterms());
            t07New.setIncotermsText(pch08.getIncoterms_Text());
            t07New.setMemo1(pch08.getMEMO1());
            t07New.setMemo2(pch08.getMEMO2());
            t07New.setMemo3(pch08.getMEMO3());
            t07New.setSl(pch08.getSL());
            t07New.setTz(pch08.getTZ());
            t07New.setRmaterial(pch08.getRMATERIAL());
            t07New.setRmaterialCurrency(pch08.getRMATERIAL_CURRENCY());
            t07New.setRmaterialPrice(pch08.getRMATERIAL_PRICE());
            t07New.setRmaterialLt(pch08.getRMATERIAL_LT());
            t07New.setRmaterialMoq(pch08.getRMATERIAL_MOQ());
            t07New.setRmaterialKbxt(pch08.getRMATERIAL_KBXT());
            t07New.setUmcSelection(pch08.getUMC_SELECTION());
            t07New.setUmcComment1(pch08.getUMC_COMMENT_1());
            t07New.setUmcComment2(pch08.getUMC_COMMENT_2());
            t07New.setStatus(pch08.getSTATUS());
            t07New.setInitialObj(pch08.getINITIAL_OBJ());
            t07New.setPlantId(pch08.getPLANT_ID());
            t07New.setSupplierMat(pch08.getSUPPLIER_MAT());
            t07New.setFinalChoice(pch08.getFINAL_CHOICE());

            newItems.add(t07New);
        });

        return newItems;
    }

    public void checkList(Pch10DataList list) {

        for (Pch10L value : list.getList()) {

        }

        list.setErr(false);

    }

    public void detailsSaveList(Pch10DataList list) {

        T06QuotationH o = Pch10Dao.getByQuo(list.getList().get(0).getQUO_NUMBER());

        if (o != null) {

            o.setMachineType(list.getList().get(0).getMACHINE_TYPE());
            o.setQuantity(list.getList().get(0).getQUANTITY());
            o.setItem(list.getList().get(0).getItem());
            o.setTime(list.getList().get(0).getTIME());
            o.setLocation(list.getList().get(0).getLOCATION());

            Pch10Dao.modify(o);

            list.setErr(false);

            list.setReTxt("Save Success");

        }
    }

    public Boolean checkCopy(Pch10SaveDataList list) {
        Boolean copy = Pch10Dao.getCopyByID(list.getList().get(0).getQUO_NUMBER(), list.getList().get(0).getQUO_ITEM());

        return copy;

    }

    public void copyDataBykey(Pch10SaveDataList list) {
        T07QuotationD o = T07QuotationD.create();

        List<T07QuotationD> CopyList = Pch10Dao.getCopyItem(list.getList().get(0).getQUO_NUMBER(),
                list.getList().get(0).getCOPYBY());

        Pch10Save value = list.getList().get(0);

        for (T07QuotationD item : CopyList) {

            // 复制数据
            o.setId(UUID.randomUUID().toString());
            o.setSalesNumber(item.getSalesNumber());
            o.setSalesDNo(item.getSalesDNo());
            o.setQuoNumber(item.getQuoNumber());
            o.setQuoVersion(item.getQuoVersion());
            o.setNo(item.getNo());
            o.setRefrenceNo(item.getRefrenceNo());
            o.setMaterialNumber(item.getMaterialNumber());
            o.setCustMaterial(item.getCustMaterial());
            o.setManufactMaterial(item.getManufactMaterial());
            o.setAttachment(item.getAttachment());
            o.setMaterial(item.getMaterial());
            o.setMaker(item.getMaker());
            o.setStatus(item.getStatus());
            o.setPlantId(item.getPlantId());
            o.setSupplierMat(item.getSupplierMat());
            o.setSapMatId(item.getSapMatId());
            o.setPersonNo1(item.getPersonNo1());
            o.setQty(item.getQty());

            // 修改数据
            o.setQuoItem(value.getQUO_ITEM());
            o.setUwebUser(value.getUWEB_USER());
            o.setBpNumber(value.getBP_NUMBER());
            o.setYlp(value.getYLP());
            o.setManul(value.getMANUL());
            o.setManufactCode(value.getMANUFACT_CODE());
            o.setCustomerMmodel(value.getCUSTOMER_MMODEL());
            o.setMidQf(value.getMID_QF());
            o.setSmallQf(value.getSMALL_QF());
            o.setOtherQf(value.getOTHER_QF());
            o.setCurrency(value.getCURRENCY());
            o.setPrice(value.getPRICE());
            o.setPriceControl(value.getPRICE_CONTROL());
            o.setLeadTime(value.getLEAD_TIME());
            o.setMoq(value.getMOQ());
            o.setUnit(value.getUNIT());
            o.setSpq(value.getSPQ());
            o.setKbxt(value.getKBXT());
            o.setProductWeight(value.getPRODUCT_WEIGHT());
            o.setOriginalCou(value.getORIGINAL_COU());
            o.setEol(value.getEOL());
            o.setIsboi(value.getISBOI());
            o.setIncoterms(value.getIncoterms());
            o.setIncotermsText(value.getIncoterms_Text());
            o.setMemo1(value.getMEMO1());
            o.setMemo2(value.getMEMO2());
            o.setMemo3(value.getMEMO3());
            o.setSl(value.getSL());
            o.setTz(value.getTZ());
            o.setRmaterial(value.getRMATERIAL());
            o.setRmaterialCurrency(value.getRMATERIAL_CURRENCY());
            o.setRmaterialPrice(value.getRMATERIAL_PRICE());
            o.setRmaterialLt(value.getRMATERIAL_LT());
            o.setRmaterialMoq(value.getRMATERIAL_MOQ());
            o.setRmaterialKbxt(value.getRMATERIAL_KBXT());
            o.setUmcSelection(value.getUMC_SELECTION());
            o.setUmcComment1(value.getUMC_COMMENT_1());
            o.setUmcComment2(value.getUMC_COMMENT_2());

            o.setInitialObj(value.getINITIAL_OBJ());
            o.setFinalChoice(value.getFINAL_CHOICE());

            try {

                Pch10Dao.modifyT07(o);
                list.setErr(false);
                list.setReTxt("第" + list.getList().size() + "行保存成功");

            } catch (Exception e) {

                e.printStackTrace();
                list.setErr(true);
                list.setReTxt(e.getMessage());

            }
        }
    }

    public void ListSendStatus(Pch10DataList list) {

        for (Pch10L value : list.getList()) {

            Pch10Dao.UpdateStatus(value.getQUO_NUMBER());

        }
    }

    public void ListSendStatus2(Pch10DataList list) {
        HashSet<String> QUO_NUMBERset = new HashSet<>();

        for (Pch10L value : list.getList()) {

            Pch10Dao.UpdateStatus2(value.getQUO_NUMBER(), value.getQUO_ITEM());
            QUO_NUMBERset.add(value.getQUO_NUMBER());

        }

        Pch10Dao.SetHStatus(QUO_NUMBERset);
    }

    public void updateT07(String id) {
        IFLog log = new IFLog(IFSManageDao.IF_S4_MST);
        log.setTd(super.transactionInit()); // 事务初始换

        TransactionStatus s = null;

        try {
            s = this.begin(log.getTd()); // 开启新事务
        
        T07QuotationD t07QuotationD = pch07Dao.get(id);

        T01SapMat custMat = mstD001.getCustMat(t07QuotationD.getCustMaterial());
        t07QuotationD.setMaterial(custMat.getMatName());
        t07QuotationD.setMaterialNumber(custMat.getMatId());
        //获取品目和bp
        T03SapBp byID = mstD003.getByID(t07QuotationD.getBpNumber());
        t07QuotationD.setUwebUser(byID.getBpName1());


        //更新t07表数据
        pch07Dao.update(t07QuotationD);


        this.commit(s); // 提交事务

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        this.rollback(s); // 回滚事务
    }
      
    }
}
