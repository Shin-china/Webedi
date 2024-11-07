package customer.dao.pch;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import cds.gen.pch.Pch_;
import cds.gen.pch.T01PoH;
import cds.gen.pch.T06QuotationH;
import customer.bean.pch.Pch10;
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
    public void updatesta(T06QuotationH o) {
        o.setCdTime(getNow());
        db.run(Update.entity(Pch_.T06_QUOTATION_H).entry(o));
    }

    public String getStatus(String quono) {
        Optional<T06QuotationH> result = db.run(Select.from(Pch_.T06_QUOTATION_H).where(o -> o.QUO_NUMBER().eq(quono)))
                .first(T06QuotationH.class);
        if (result.isPresent()) {

            return result.get().getStatus();

        }
        return null;

    }

    public void Setstatus(T06QuotationH o) {
        updatesta(o);
    }

    public T06QuotationH getById(String quono) {

        Optional<T06QuotationH> result = db.run(Select.from(Pch_.T06_QUOTATION_H).where(o -> o.QUO_NUMBER().eq(quono)))
                .first(T06QuotationH.class);
        if (result.isPresent()) {

            return result.get();

        }
        return null;

    }

}
