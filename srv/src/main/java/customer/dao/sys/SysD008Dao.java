package customer.dao.sys;

import java.util.HashMap;
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
import cds.gen.sys.T07ComOpH;
import cds.gen.sys.T08ComOpD;
import customer.bean.com.UmcConstants;
import customer.dao.common.Dao;
import customer.tool.DateTools;

@Repository
public class SysD008Dao extends Dao {
    // LOG
    private static final Logger logger = LoggerFactory.getLogger(SysD008Dao.class);

    // 根据 区分头code 查区分信息
    public T07ComOpH getByCode(String hCode) {
        java.util.Optional<T07ComOpH> result = db.run(Select.from(Sys_.T07_COM_OP_H).where(o -> o.H_CODE().eq(hCode)))
                .first(T07ComOpH.class);
        if (result.isPresent()) {
            return result.get();
        }

        return null;

    }

    // 根据 区分头code 查所有明细信息
    public List<T08ComOpD> getT08ByHcode(String code) {
        return db.run(Select.from(Sys_.T08_COM_OP_D).where(o -> o.H_CODE().eq(code))).listOf(T08ComOpD.class);
    }

    // 根据 区分头code 查所有明细信息
    public HashMap<String, T08ComOpD> getT08ByHcodeToMap(String code) {
        HashMap<String, T08ComOpD> map = new HashMap<String, T08ComOpD>();
        List<T08ComOpD> list = getT08ByHcode(code);
        for (T08ComOpD d : list) {
            map.put(d.getDCode(), d);
        }
        return map;
    }

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

    public List<T08ComOpD> getmailaddByHcodeV1(String h_code, String supplier) {

        List<T08ComOpD> listOf = db.run(
                Select.from(Sys_.T08_COM_OP_D)
                        .where(o -> o.H_CODE().eq(h_code).and(o.VALUE01().eq(supplier)).and(o.DEL_FLAG().eq("N"))))

                .listOf(T08ComOpD.class);

        if (listOf.size() > 0) {
            return listOf;
        }
        return null;
    }

    public String getDnameByHcodeDcode(String h_code, String d_code) {

        Optional<T08ComOpD> listOf = db.run(
                Select.from(Sys_.T08_COM_OP_D)
                        .where(o -> o.H_CODE().eq(h_code).and(o.D_CODE().eq(d_code))))

                .first(T08ComOpD.class);

        if (listOf.isPresent()) {

            return listOf.get().getDName();

        }

        return d_code;

    }

    /**
     * 通过供应商取得相应邮箱数据
     * 
     */
    public String getEmailAddress(String supplier) {

        List<T08ComOpD> listOf = db.run(
                Select.from(Sys_.T08_COM_OP_D)
                        .where(o -> o.H_CODE().eq(UmcConstants.T08_EMAIL_ADDRESS).and(o.VALUE01().eq(supplier))))

                .listOf(T08ComOpD.class);

        if (listOf.size() > 0) {
            // 获取listOf中所有的.getValue02()去重数据，并且拼接成,分割的字符串
            return listOf.stream().map(T08ComOpD::getValue02).distinct().collect(Collectors.joining(","));

        }

        return "";

    }

}
