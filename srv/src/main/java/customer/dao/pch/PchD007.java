package customer.dao.pch;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Select;

import cds.gen.pch.Pch_;

import cds.gen.pch.T07QuotationD;
import customer.dao.common.Dao;

@Repository
public class PchD007 extends Dao {

    public List<T07QuotationD> getList(String quoNum, String matId) {
        return db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.QUO_NUMBER().eq(quoNum).and(o.Material().eq(matId))))
                .listOf(T07QuotationD.class);
    }

}
