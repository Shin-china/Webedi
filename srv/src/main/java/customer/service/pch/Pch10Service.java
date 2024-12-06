package customer.service.pch;

import java.math.BigDecimal;
import java.util.List;

import org.apache.poi.ss.formula.functions.T;
import org.checkerframework.checker.units.qual.t;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.annotation.JSONField;

import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;
import cds.gen.sys.T07ComOpH;
import cds.gen.sys.T08ComOpD;
import ch.qos.logback.core.status.Status;
import customer.bean.pch.Pch10;
import customer.bean.pch.Pch10DataList;
import customer.bean.pch.Pch10L;
import customer.bean.pch.Pch10Save;
import customer.bean.pch.Pch10SaveDataList;
import customer.dao.pch.Pch10Dao;

@Component
public class Pch10Service {

    @Autowired
    private Pch10Dao Pch10Dao;

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
        Integer count = 0;

        for (Pch10Save value : list.getList()) {
            count++;

            T07QuotationD o = T07QuotationD.create();

            o.setId(value.getT02_ID());
            o.setSalesNumber(value.getSALES_NUMBER());
            o.setSalesDNo(value.getSALES_D_NO());
            o.setQuoNumber(value.getQUO_NUMBER());
            o.setQuoItem(value.getQUO_ITEM());
            o.setQuoVersion(value.getQUO_VERSION());
            o.setNo(value.getNO());
            o.setRefrenceNo(value.getREFRENCE_NO());
            o.setMaterialNumber(value.getMATERIAL_NUMBER());
            o.setCustMaterial(value.getCUST_MATERIAL());
            o.setManufactMaterial(value.getMANUFACT_MATERIAL());
            o.setAttachment(value.getAttachment());
            o.setMaterial(value.getMaterial());
            o.setMaker(value.getMAKER());
            o.setUwebUser(value.getUWEB_USER());
            o.setBpNumber(value.getBP_NUMBER());
            o.setPersonNo1(value.getPERSON_NO1());
            o.setYlp(value.getYLP());
            o.setManul(value.getMANUL());
            o.setManufactCode(value.getMANUFACT_CODE());
            o.setCustomerMmodel(value.getCUSTOMER_MMODEL());
            o.setMidQf(value.getMID_QF());
            o.setSmallQf(value.getSMALL_QF());
            o.setOtherQf(value.getOTHER_QF());
            o.setQty(value.getQTY());
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
            o.setStatus(value.getSTATUS());
            o.setInitialObj(value.getINITIAL_OBJ());
            o.setPlantId(value.getPLANT_ID());
            o.setSupplierMat(value.getSUPPLIER_MAT());

            try {

                Pch10Dao.modifyT07(o);
                list.setErr(false);
                list.setReTxt("第" + count + "行保存成功");

            } catch (Exception e) {

                e.printStackTrace();
                list.setErr(true);
                list.setReTxt(e.getMessage());

            }

        }
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
}
