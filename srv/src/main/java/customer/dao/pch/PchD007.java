package customer.dao.pch;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import cds.gen.pch.Pch_;

import cds.gen.pch.T07QuotationD;
import customer.dao.common.Dao;

@Repository
public class PchD007 extends Dao {

    public List<T07QuotationD> getList(String quoNum) {
        return db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.QUO_NUMBER().eq(quoNum)))
                .listOf(T07QuotationD.class);
    }

    public T07QuotationD getById(String id) {
        Optional<T07QuotationD> result = db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.ID().eq(id)))
                .first(T07QuotationD.class);

        if (result.isPresent()) {
            return result.get();

        }
        return null;

    }

    public void update(T07QuotationD o) {
        o.setUpBy(getUserId());
        o.setUpTime(getNow());
        db.run(Update.entity(Pch_.T07_QUOTATION_D).data(o));
    }

}
