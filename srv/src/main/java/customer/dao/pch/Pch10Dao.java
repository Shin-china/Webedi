package customer.dao.pch;

import java.util.HashMap;
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

        T06QuotationH o = getById2(quo_NUMBER);

        if (o != null) {
            o.setStatus("2");

            updateT06(o);

        }

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

        T07QuotationD o = getById3(quo_NUMBER, quo_ITEM);

        if (o != null) {
            o.setStatus("4");

            updateT07Status(o);

        }

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

}
