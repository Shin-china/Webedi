package customer.dao.sys;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import cds.gen.sys.Sys_;
import cds.gen.sys.T01User;
import cds.gen.sys.T11IfManager;
import customer.comm.tool.StringTool;
import customer.dao.common.Dao;

@Repository
public class IFSManageDao extends Dao {

    public static final String IF_S4_BP = "IF040_1";
    public static final String IF_S4_BPPURCHASE = "IF040_2";
    public static final String IF_S4_MST = "IF039";
    public static final String IF_S4_PO = "IF041";
    public static final String IF_S4_PR = "IF065";
    public static final String IF_S4_PAY = "IF042";
    public static final String IF_S4_PO_POST = "IF043";
    public static final String IF_S4_IF055 = "IF055";//購買見積結果送信
    public static final String IF_S4_IF054 = "IF054";//購買見積依頼受信


    public T11IfManager getByCode(String string) {
        Optional<T11IfManager> first = db
                .run(Select.from(Sys_.T11_IF_MANAGER).where(o -> o.IF_CODE().eq(string)))
                .first(T11IfManager.class);
        if (first.isPresent()) {
            return first.get();
        }

        return null;
    }

    // 修改接口配置
    public void updateIfManager(T11IfManager old) {
        T11IfManager o = T11IfManager.create(old.getIfCode());
        if (!StringTool.isEmpty(old.getNextPara())) {
            o.setNextPara(old.getNextPara());
        }
        o.setLastRunTime(getNow());
        db.run(Update.entity(Sys_.T11_IF_MANAGER).entry(o));
    }

}
