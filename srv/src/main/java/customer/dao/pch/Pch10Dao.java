package customer.dao.pch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import cds.gen.pch.Pch_;
import cds.gen.pch.T01PoH;
import cds.gen.pch.T02PoD;
import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;
import cds.gen.sys.T07ComOpH;
import customer.bean.pch.Pch10;
import customer.bean.pch.Pch10DataList;
import customer.bean.pch.Pch10Save;
import customer.bean.pch.Pch10SaveDataList;
import customer.dao.common.Dao;

@Repository
public class Pch10Dao extends Dao {

    public T06QuotationH getByID(String quono) {

        Optional<T06QuotationH> result = db.run(Select.from(Pch_.T06_QUOTATION_H).where(o -> o.QUO_NUMBER().eq(quono)))
                .first(T06QuotationH.class);
        if (result.isPresent()) {

            return result.get();

        }

        return null;

    }

    public void modify(T06QuotationH o) {
        T06QuotationH isExist = getByID(o.getQuoNumber());
        if (isExist == null) {
            // insert(o);
        } else {
            update(o);
        }

    }

    // Insert
    public void insert(T06QuotationH o) {
        o.setCdTime(getNow());
        db.run(Insert.into(Pch_.T06_QUOTATION_H).entry(o));
    }

    // Update
    public void update(T06QuotationH o) {
        o.setCdTime(getNow());
        db.run(Update.entity(Pch_.T06_QUOTATION_H).entry(o));
    }

    // Updatesta
    public void updatesta(T07QuotationD o) {
        o.setCdTime(getNow());
        db.run(Update.entity(Pch_.T07_QUOTATION_D).entry(o));
    }

    public String getStatus(String sal_Num) {
        Optional<T07QuotationD> result = db
                .run(Select.from(Pch_.T07_QUOTATION_D).where(o -> o.SALES_NUMBER().eq(sal_Num)))
                .first(T07QuotationD.class);
        if (result.isPresent()) {

            return result.get().getStatus();

        }
        return null;

    }

    public void Setstatus(T07QuotationD o) {
        updatesta(o);
    }

    public T06QuotationH getById(String sal_Num) {

        Optional<T06QuotationH> result = db
                .run(Select.from(Pch_.T06_QUOTATION_H).where(o -> o.SALES_NUMBER().eq(sal_Num)))
                .first(T06QuotationH.class);
        if (result.isPresent()) {

            return result.get();

        }
        return null;

    }

    public T06QuotationH getById2(String quo_Number) {

        Optional<T06QuotationH> result = db
                .run(Select.from(Pch_.T06_QUOTATION_H).where(o -> o.QUO_NUMBER().eq(
                        quo_Number)))
                .first(T06QuotationH.class);
        if (result.isPresent()) {

            return result.get();

        }
        return null;

    }

    public T06QuotationH getByQuo(String Quo_No) {

        Optional<T06QuotationH> result = db
                .run(Select.from(Pch_.T06_QUOTATION_H).where(o -> o.QUO_NUMBER().eq(
                        Quo_No)))
                .first(T06QuotationH.class);
        if (result.isPresent()) {

            return result.get();

        }
        return null;

    }

    public List<T07QuotationD> getItems(String sal_Num) {

        List<T07QuotationD> result = db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.SALES_NUMBER().eq(sal_Num)))
                .listOf(T07QuotationD.class);

        return result;

    }

    public String getHStatus(String sal_Num) {

        Optional<T06QuotationH> result = db
                .run(Select.from(Pch_.T06_QUOTATION_H).where(o -> o.SALES_NUMBER().eq(sal_Num)))
                .first(T06QuotationH.class);
        if (result.isPresent()) {

            return result.get().getStatus();

        }
        return null;

    }

    public T07QuotationD getT07ByID(String id) {

        Optional<T07QuotationD> result = db.run(Select.from(Pch_.T07_QUOTATION_D)
                .where(o -> o.ID().eq(id)))
                .first(T07QuotationD.class);

        if (result.isPresent()) {

            return result.get();

        }
        return null;

    }

    public void modifyT07(T07QuotationD o) {

        T07QuotationD isExist = getT07ByID(o.getId());

        if (isExist == null) {
            insertT07(o);
        } else {
            updateT07(o);
        }

    }

    private void updateT07(T07QuotationD o) {
        o.setUpTime(getNow());
        db.run(Update.entity(Pch_.T07_QUOTATION_D).entry(o));
    }

    private void insertT07(T07QuotationD o) {
        o.setCdTime(getNow());
        db.run(Insert.into(Pch_.T07_QUOTATION_D).entry(o));
    }

    public Boolean getCopyByID(String quo_NUMBER, Integer quo_ITEM) {

        Optional<T07QuotationD> result = db.run(Select.from(Pch_.T07_QUOTATION_D)
                .where(o -> o.QUO_NUMBER().eq(
                        quo_NUMBER).and(o.QUO_ITEM().eq(quo_ITEM))))
                .first(T07QuotationD.class);

        if (result.isPresent()) {

            return true;

        }
        return false;

    }

    public List<T07QuotationD> getCopyItem(String quo_NUMBER, Integer copyby) {
        List<T07QuotationD> result = db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.QUO_NUMBER().eq(quo_NUMBER)
                                .and(o.QUO_ITEM().eq(copyby))
                                .and(o.DEL_FLAG().eq("N"))))
                .listOf(T07QuotationD.class);
        return result;
    }

    public void UpdateStatus(String quo_NUMBER) {

        List<T07QuotationD> o = getById4(quo_NUMBER);

        for (T07QuotationD item : o) {

            if (item.getStatus().equals("1")) {// 未送信
                item.setStatus("2");
            } else if (item.getStatus().equals("2")) {
                item.setStatus("2");
            } else if (item.getStatus().equals("3")) {
                item.setStatus("4");
            } else if (item.getStatus().equals("4")) {
                item.setStatus("4");
            } else {

            }

            updateT07Status(item);

        }

        List<T07QuotationD> o2 = getById4(quo_NUMBER);

        boolean hasUnsent = false;
        boolean hasPendingResponse = false;
        boolean hasResponded = false;
        boolean allComplete = true;

        for (T07QuotationD item : o2) {

            // 检查状态
            if (item.getStatus().equals("1")) { // 未送信
                hasUnsent = true;
                allComplete = false;
            } else if (item.getStatus().equals("2")) { // 送信済（未回答）
                hasPendingResponse = true;
                allComplete = false;
            } else if (item.getStatus().equals("3")) { // 回答済
                hasResponded = true;
                allComplete = false;
            } else if (item.getStatus().equals("4")) { // 完了
                allComplete = false;
            }
        }

        T06QuotationH o3 = getById2(quo_NUMBER);
        // 根据检查的状态更新头部状态
        if (hasUnsent) {
            o3.setStatus("1"); // 未送信
        } else if (hasPendingResponse) {
            o3.setStatus("2"); // 送信済（未回答）
        } else if (hasResponded) {
            o3.setStatus("3"); // 回答済
        } else if (allComplete) {
            o3.setStatus("5"); // 完了
        } else {
            o3.setStatus("4"); // 再送信（依頼中）
        }

        updateT06(o3);

    }

    public void updateT06(T06QuotationH o) {

        // 获取 key 的值
        String quonumber = o.getQuoNumber();

        // 构建更新条件
        Map<String, Object> keys = new HashMap<>();
        keys.put("QUO_NUMBER", quonumber);

        // 创建需要更新的字段
        Map<String, Object> updatedFields = new HashMap<>();

        updatedFields.put("STATUS", o.getStatus());

        // 执行更新
        db.run(Update.entity(Pch_.T06_QUOTATION_H, b -> b.matching(keys)).data(updatedFields));

    }

    public void UpdateStatus2(String quo_NUMBER, Integer quo_ITEM) {

        List<T07QuotationD> o = getById5(quo_NUMBER, quo_ITEM);

        for (T07QuotationD item : o) {

            if (item.getStatus().equals("1")) {// 未送信
                item.setStatus("2");
            } else if (item.getStatus().equals("2")) {
                item.setStatus("2");
            } else if (item.getStatus().equals("3")) {
                item.setStatus("4");
            } else if (item.getStatus().equals("4")) {
                item.setStatus("4");
            } else {

            }

            updateT07Status(item);

        }

    }

    private List<T07QuotationD> getById5(String quo_NUMBER, Integer quo_ITEM) {

        List<T07QuotationD> result = db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.QUO_NUMBER().eq(quo_NUMBER)
                                .and(o.QUO_ITEM().eq(quo_ITEM))
                                .and(o.DEL_FLAG().eq("N"))))
                .listOf(T07QuotationD.class);
        return result;

    }

    private void updateT07Status(T07QuotationD o) {
        // 获取 key 的值
        String quonumber = o.getQuoNumber();
        Integer quoitem = o.getQuoItem();

        // 构建更新条件
        Map<String, Object> keys = new HashMap<>();
        keys.put("QUO_NUMBER", quonumber);
        keys.put("QUO_ITEM", quoitem);

        // 创建需要更新的字段
        Map<String, Object> updatedFields = new HashMap<>();

        updatedFields.put("STATUS", o.getStatus());

        // 执行更新
        db.run(Update.entity(Pch_.T07_QUOTATION_D, b -> b.matching(keys)).data(updatedFields));
    }

    private T07QuotationD getById3(String quo_NUMBER, Integer quo_ITEM) {

        Optional<T07QuotationD> result = db
                .run(Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.QUO_NUMBER().eq(quo_NUMBER)
                                .and(o.QUO_ITEM().eq(quo_ITEM)).and(o.DEL_FLAG().eq(
                                        "N"))))

                .first(T07QuotationD.class);
        if (result.isPresent()) {

            return result.get();

        }
        return null;

    }

    private List<T07QuotationD> getById4(String quo_NUMBER) {

        List<T07QuotationD> result = db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.QUO_NUMBER().eq(quo_NUMBER)
                                .and(o.DEL_FLAG().eq("N"))))
                .listOf(T07QuotationD.class);
        return result;

    }

    public void SetHStatus(HashSet<String> qUO_NUMBERset) {

        for (String quono : qUO_NUMBERset) {

            List<T07QuotationD> o2 = getById4(quono);

            boolean hasUnsent = false;
            boolean hasPendingResponse = false;
            boolean hasResponded = false;
            boolean allComplete = true;

            for (T07QuotationD item : o2) {

                // 检查状态
                if (item.getStatus().equals("1")) { // 未送信
                    hasUnsent = true;
                    allComplete = false;
                } else if (item.getStatus().equals("2")) { // 送信済（未回答）
                    hasPendingResponse = true;
                    allComplete = false;
                } else if (item.getStatus().equals("3")) { // 回答済
                    hasResponded = true;
                    allComplete = false;
                } else if (item.getStatus().equals("4")) { // 完了
                    allComplete = false;
                }
            }

            T06QuotationH o3 = getById2(quono);
            // 根据检查的状态更新头部状态
            if (hasUnsent) {
                o3.setStatus("1"); // 未送信
            } else if (hasPendingResponse) {
                o3.setStatus("2"); // 送信済（未回答）
            } else if (hasResponded) {
                o3.setStatus("3"); // 回答済
            } else if (allComplete) {
                o3.setStatus("5"); // 完了
            } else {
                o3.setStatus("4"); // 再送信（依頼中）
            }
            updateT06(o3);
        }

    }
}
