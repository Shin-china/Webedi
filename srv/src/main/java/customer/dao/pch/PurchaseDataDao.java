package customer.dao.pch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.units.qual.t;
import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import cds.gen.mst.Mst_;
import cds.gen.mst.T05SapBpPurchase;
import cds.gen.pch.Pch_;
import cds.gen.pch.T01PoH;
import cds.gen.pch.T02PoD;
import cds.gen.pch.T03PoC;
import cds.gen.pch.T04PaymentH;
import cds.gen.pch.T05PaymentD;
import cds.gen.pch.T09Forcast;
import customer.bean.pch.Item;
import customer.bean.pch.Items;
import customer.dao.common.Dao;

@Repository
public class PurchaseDataDao extends Dao {

    public T01PoH getByID(String id) {
        Optional<T01PoH> result = db.run(Select.from(Pch_.T01_PO_H).where(o -> o.PO_NO().eq(id)))
                .first(T01PoH.class);
        if (result.isPresent()) {

            return result.get();

        }
        return null;

    }

    public void modify(T01PoH o) {
        T01PoH isExist = getByID(o.getPoNo());
        if (isExist == null) {
            insert(o);
        } else {
            update(o);
        }
    }

    // Insert
    public void insert(T01PoH o) {
        o.setCdTime(getNow());
        db.run(Insert.into(Pch_.T01_PO_H).entry(o));
    }

    // Update
    public void update(T01PoH o) {
        o.setUpTime(getNow());
        db.run(Update.entity(Pch_.T01_PO_H).entry(o));
    }

    public T02PoD getByID2(String id, Integer dn) {
        Optional<T02PoD> result = db.run(Select.from(Pch_.T02_PO_D)
                .where(o -> o.PO_NO().eq(id)
                        .and(o.D_NO().eq(dn))))
                .first(T02PoD.class);

        if (result.isPresent()) {

            return result.get();

        }
        return null;

    }

    public void modify2(T02PoD o2, Boolean d, String supplier) {
        T02PoD isExist = getByID2(o2.getPoNo(), o2.getDNo());
        if (isExist == null) {
            insert2(o2, d, supplier);
        } else {
            update2(o2, d, supplier);
        }
    }

    // Insert2
    public void insert2(T02PoD o2, Boolean d, String supplier) {
        o2.setPoType("C");

        // 如果是删除标记被打上的话 应该是改成D而不是c创建
        if (d) {

            o2.setPoType("D");
            o2.setDelFlag("Y");

        }
        // 如果 supplier 是 W 则 status 应该是2 不等于W则全都是1。

        if (checkSupplierisW(supplier)) {
            o2.setStatus("2");
        } else {
            o2.setStatus("1");
        }

        o2.setCdTime(getNow());
        db.run(Insert.into(Pch_.T02_PO_D).entry(o2));
    }

    public boolean checkSupplierisW(String supplier) {
        Optional<T05SapBpPurchase> result = db.run(Select.from(Mst_.T05_SAP_BP_PURCHASE)
                .where(o -> o.SUPPLIER().eq(supplier)
                        .and(o.ZABC().eq("W"))))
                .first(T05SapBpPurchase.class);

        if (result.isPresent()) {

            return true;

        }
        return false;

    }

    // Update2
    public void update2(T02PoD o2, Boolean d, String supplier) {
        // 判断是否要更新 potype
        Boolean update = getByPoDnUpdateObj2(o2);

        if (d) {

            o2.setPoType("D");
            o2.setDelFlag("Y");

        } else {

            if (update) {

                o2.setPoType("U");
                o2.setUpTime(getNow());
                if (!checkSupplierisW(supplier)) {
                    o2.setStatus("1");
                }

            }

            o2.setDelFlag("N");

        }
        if (checkSupplierisW(supplier)) {
            o2.setStatus("2");
        } 
        // o2.setCdTime(getNow());

        db.run(Update.entity(Pch_.T02_PO_D).entry(o2));
    }

    public T09Forcast getByID3(String id, Integer dn) {
        Optional<T09Forcast> result = db
                .run(Select.from(Pch_.T09_FORCAST).where(o -> o.PR_NUMBER().eq(id).and(o.D_NO().eq(dn))))
                .first(T09Forcast.class);
        if (result.isPresent()) {

            return result.get();

        }
        return null;

    }

    public void modify3(T09Forcast o) {
        T09Forcast isExist = getByID3(o.getPrNumber(), o.getDNo());
        if (isExist == null) {
            insert3(o);
        } else {
            update3(o);
        }
    }

    // Insert
    public void insert3(T09Forcast o) {
        o.setCdTime(getNow());
        db.run(Insert.into(Pch_.T09_FORCAST).entry(o));
    }

    // Update
    public void update3(T09Forcast o) {
        o.setUpTime(getNow());
        db.run(Update.entity(Pch_.T09_FORCAST).entry(o));
    }

    public Boolean getByPoDnUpdateObj(Item items) {
        Integer dn = Integer.parseInt(items.getPurchaseorderitem());

        T02PoD isExist = getByID2(items.getPurchaseorder(), dn);

        if (isExist == null) {

            return false;

        } else {

            Boolean update = updateobj(isExist, items);

            return update;

        }

    }

    public Boolean getByPoDnUpdateObj2(T02PoD o2) {

        T02PoD isExist = getByID2(o2.getPoNo(), o2.getDNo());

        Boolean update = updateobj2(isExist, o2);

        return update;
    }

    private Boolean updateobj2(T02PoD isExist, T02PoD o2) {

        if (!isExist.getPoPurQty().equals(o2.getPoPurQty())
                || !isExist.getPoDDate().isEqual(o2.getPoDDate())
                || isExist.getDelAmount().compareTo(o2.getDelAmount()) != 0) {

            return true; // 只要有一个值不一样，返回 true
        }

        return false;

    }

    private Boolean updateobj(T02PoD isExist, Item items) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate deliveryDate = LocalDate.parse(items.getSchedulelinedeliverydate(), formatter);
        Integer dn = Integer.parseInt(items.getPurchaseorderitem());
        BigDecimal PoPurQty = new BigDecimal(items.getOrderquantity());
        BigDecimal amount = new BigDecimal(items.getNetamount());

        // isExist.getPoNo() != items.getPurchaseorder()
        // || !isExist.getDNo().equals(dn)
        // 删除flag，单独考虑
        // || isExist.getDelFlag() != items.getPurchasingdocumentdeletioncode()
        if (!isExist.getPoPurQty().equals(PoPurQty)
                || !isExist.getPoDDate().isEqual(deliveryDate)
                || isExist.getDelAmount().compareTo(amount) != 0) {

            return true; // 只要有一个值不一样，返回 true
        }

        return false;

    }

    public Boolean getDelflaghavechange(Item items) {
        Integer dn = Integer.parseInt(items.getPurchaseorderitem());
        String delFlag = "N";

        if (items.getPurchasingdocumentdeletioncode().equals("L")) {
            delFlag = "Y";
        }

        T02PoD isExist = getByID2(items.getPurchaseorder(), dn);
        if (isExist == null) {

        } else {

            if (!isExist.getDelFlag().equals(delFlag)) {
                return true;
            }

        }

        return false;

    }

    public Boolean getdelbyPO(String purchaseorder) {

        List<T02PoD> result = getByPo(purchaseorder);

        for (T02PoD t02PoD : result) {
            // 如果当前po下，任意一个明细的delflag为N，则返回false
            if ("N".equals(t02PoD.getDelFlag())) {
                return false; // 遇到"N"即返回false
            }
        }

        // 如果所有的delflag都为Y，则返回true
        return true; // 只有当没有"N"时，返回true
    }

    public List<T02PoD> getByPo(String po) {

        List<T02PoD> listOf = db.run(
                Select.from(Pch_.T02_PO_D)
                        .where(o -> o.PO_NO().eq(po)))
                .listOf(T02PoD.class);

        return listOf;

    }

    public void modifyT03(T03PoC o3) {

        T03PoC isExist = getByIDT03(o3.getPoNo(), o3.getDNo(), o3.getSeq());
        if (isExist == null) {
            insertT03(o3);
        } else {
            updateT03(o3);
        }

    }

    private void updateT03(T03PoC o3) {

        o3.setUpTime(getNow());
        db.run(Update.entity(Pch_.T03_PO_C).entry(o3));

    }

    private void insertT03(T03PoC o3) {
        o3.setCdTime(getNow());

        db.run(Insert.into(Pch_.T03_PO_C).entry(o3));

    }

    private T03PoC getByIDT03(String poNo, Integer dNo, Integer seq) {

        Optional<T03PoC> result = db.run(Select.from(Pch_.T03_PO_C)
                .where(o -> o.PO_NO().eq(poNo)
                        .and(o.D_NO().eq(dNo))
                        .and(o.SEQ().eq(seq))))
                .first(T03PoC.class);

        if (result.isPresent()) {

            return result.get();

        }

        return null;

    }

    public String getSupplierByPO(String poDel) {

        Optional<T01PoH> result = db.run(Select.from(Pch_.T01_PO_H).where(o -> o.PO_NO().eq(poDel)))
                .first(T01PoH.class);
        if (result.isPresent()) {

            return result.get().getSupplier();

        }

        return null;

    }

    public void modifyT04(T04PaymentH o) {
        T04PaymentH isExist = getT04ByID(o.getInvNo(), o.getGlYear());
        if (isExist == null) {
            insertT04(o);
        } else {
            updateT04(o);
        }

    }

    private void updateT04(T04PaymentH o) {
        o.setUpTime(getNow());
        db.run(Update.entity(Pch_.T04_PAYMENT_H).entry(o));

    }

    private void insertT04(T04PaymentH o) {
        o.setCdTime(getNow());
        db.run(Insert.into(Pch_.T04_PAYMENT_H).entry(o));

    }

    private T04PaymentH getT04ByID(String invNo, Integer glYear) {

        Optional<T04PaymentH> result = db
                .run(Select.from(Pch_.T04_PAYMENT_H)
                        .where(o -> o.INV_NO().eq(invNo).and(o.GL_YEAR().eq(glYear))))
                .first(T04PaymentH.class);
        if (result.isPresent()) {

            return result.get();

        }
        return null;
    }

    public void modifyT05(T05PaymentD p) {
        T05PaymentD isExist = getT05ByID(p.getInvNo(), p.getGlYear(), p.getItemNo());
        if (isExist == null) {
            insertT05(p);
        } else {
            updateT05(p);
        }
    }

    private void updateT05(T05PaymentD p) {
        p.setUpTime(getNow());
        db.run(Update.entity(Pch_.T05_PAYMENT_D).entry(p));
    }

    private void insertT05(T05PaymentD p) {
        p.setCdTime(getNow());
        db.run(Insert.into(Pch_.T05_PAYMENT_D).entry(p));
    }

    private T05PaymentD getT05ByID(String invNo, Integer glYear, Integer itemNo) {
        Optional<T05PaymentD> result = db
                .run(Select.from(Pch_.T05_PAYMENT_D)
                        .where(o -> o.INV_NO().eq(invNo).and(o.GL_YEAR().eq(glYear)).and(o.ITEM_NO().eq(itemNo))))
                .first(T05PaymentD.class);
        if (result.isPresent()) {

            return result.get();

        }
        return null;

    }

    public String getPoCompByPO(String poDel) {
        Optional<T01PoH> result = db.run(Select.from(Pch_.T01_PO_H).where(o -> o.PO_NO().eq(poDel)))
                .first(T01PoH.class);

        if (result.isPresent()) {

            return result.get().getPoBukrs();

        }
        ;
        return null;
    }

}
