package customer.dao.sys;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;

import cds.gen.pch.Pch_;
import cds.gen.pch.T03PoC;
import cds.gen.pch.T07QuotationD;
import cds.gen.pch.T08Upload;
import cds.gen.sys.Sys_;
import cds.gen.sys.T08ComOpD;
import customer.dao.common.Dao;
import customer.tool.DateTools;

@Repository
public class SysD008Dao extends Dao {
    // LOG
    private static final Logger logger = LoggerFactory.getLogger(SysD008Dao.class);

    /**
     * 根据id查找
     * 
     * @param h_id
     */
    public List<T08ComOpD> getByID(String h_code) {
        List<T08ComOpD> listOf = db.run(
                Select.from(Sys_.T08_COM_OP_D)
                        .where(o -> o.H_CODE().eq(h_code)))
                .listOf(T08ComOpD.class);

        return listOf;
    }

    public List<T08ComOpD> getmailaddByHcodeV1(String h_code, String value1) {

        List<T08ComOpD> listOf = db.run(
                Select.from(Sys_.T08_COM_OP_D)
                        .where(o -> o.H_CODE().eq(h_code).and(o.VALUE01().eq(value1)).and(o.DEL_FLAG().eq("N"))))

                .listOf(T08ComOpD.class);

        if (listOf.size() > 0) {
            return listOf;
        }
        return null;
    }

}
