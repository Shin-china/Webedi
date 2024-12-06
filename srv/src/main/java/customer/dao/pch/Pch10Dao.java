package customer.dao.pch;

import java.util.List;
import java.util.Optional;

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

}
