package customer.dao.sys;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import com.sap.cds.Result;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.draft.DraftService;

import cds.gen.sys.Sys_;
import cds.gen.sys.T07ComOpH;
import cds.gen.sys.T08ComOpD;
import cds.gen.tableservice.TableService_;
import customer.dao.common.Dao;

@Repository
public class DictDao extends Dao {

    private static final Logger logger = LoggerFactory.getLogger(DictDao.class);

    // 根据  区分头code  查区分信息
    public T07ComOpH getByCode(String hCode) {
        java.util.Optional<T07ComOpH> result = db.run(Select.from(Sys_.T07_COM_OP_H).where(o -> o.H_CODE().eq(hCode)))
                .first(T07ComOpH.class);
        if (result.isPresent()) {
            return result.get();
        }

        return null;

    }

    // 根据  区分头code  查所有明细信息
    public List<T08ComOpD> getT08ByHcode(String code) {
        return db.run(Select.from(Sys_.T08_COM_OP_D).where(o -> o.H_CODE().eq(code))).listOf(T08ComOpD.class);
    }

    // 根据  区分头code  查所有明细信息
    public HashMap<String, T08ComOpD> getT08ByHcodeToMap(String code) {
        HashMap<String, T08ComOpD> map = new HashMap<String, T08ComOpD>();
        List<T08ComOpD> list = getT08ByHcode(code);
        for (T08ComOpD d : list) {
            map.put(d.getDCode(), d);
        }
        return map;
    }

    /**
     * 更新草稿明细
     * 
     * @param data
     * @param keys
     * @return
     */
    public long updateDarft(Map<String, Object> data, Map<String, Object> keys, DraftService adminService) {

        CqnUpdate update = Update.entity(TableService_.SYS_T08_COM_OP_D, b -> b.matching(keys)).data(data);

        Result run = adminService.patchDraft(update, adminService);

        return run.rowCount();

    }

     /**
     * 删除数据-更新草稿明细
     * @param data
     * @param keys
     * @param adminService
     * @return
     */
    public long updateDarftByDelete(String id, DraftService adminService) {

        CqnDelete delete = Delete.from(TableService_.SYS_T08_COM_OP_D).where(o -> o.ID().eq(id));

        Result run = adminService.cancelDraft(delete);

        return run.rowCount();

    }


    public T08ComOpD get(String headCode, String dCode) {
        java.util.Optional<T08ComOpD> result = db
                .run(Select.from(Sys_.T08_COM_OP_D).where(o -> o.H_CODE().eq(headCode).and(o.D_CODE().eq(dCode))))
                .first(T08ComOpD.class);

        if (result.isPresent()) {
            return result.get();
        }

        return null;
    }

    public List<T08ComOpD> get(String headCode, String v1, String v2) {
        return db
                .run(Select.from(Sys_.T08_COM_OP_D)
                        .where(o -> o.H_CODE().eq(headCode).and(o.VALUE01().eq(v1).and(o.VALUE02().eq(v2)))))
                .listOf(T08ComOpD.class);

    }

    public List<T08ComOpD> getByValue01(String headCode, String v1) {
        return db.run(Select.from(Sys_.T08_COM_OP_D).where(o -> o.H_CODE().eq(headCode).and(o.VALUE01().eq(v1))))
                .listOf(T08ComOpD.class);

    }

    public void deleteByHcode(String hcode) {
         db.run(Delete.from(Sys_.T07_COM_OP_H).where(o -> o.H_CODE().eq(hcode)));
         db.run(Delete.from(Sys_.T08_COM_OP_D).where(o -> o.H_CODE().eq(hcode)));
    }

    public void insertT07(T07ComOpH o) {
        db.run(Insert.into(Sys_.T07_COM_OP_H).entry(o));
    }

    public void insertT08(T08ComOpD o) {
        o.setCdTime(getNow());
        o.setCdBy(getUserId());
        db.run(Insert.into(Sys_.T08_COM_OP_D).entry(o));
    }

}