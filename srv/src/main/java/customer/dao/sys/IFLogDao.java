package customer.dao.sys;

// import customer.bean.sys.Sys015Email;
import customer.comm.constant.ConfigConstants;
import customer.comm.tool.MessageTools;
import customer.dao.common.Dao;
// import customer.ht.business.comm.MsgBoxTool;

import java.time.Duration;
import java.util.Optional;

import com.alibaba.fastjson.JSON;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Repository;

import cds.gen.sys.Sys_;
import cds.gen.sys.T11IfManager;
import cds.gen.sys.T15IfLog;

@Repository
public class IFLogDao extends Dao {
    private static final Logger logger = LoggerFactory.getLogger(IFLogDao.class);

    @Autowired
    IFSManageDao ifsManageDao;

    @Autowired
    ResourceBundleMessageSource rbms;

    public void insert(T15IfLog o) {
        db.run(Insert.into(Sys_.T15_IF_LOG).entry(o));
    }

    public void update(T15IfLog o) {
        if (o.getIfPara() != null && o.getIfPara().length() >= 200) {
            o.setIfPara(o.getIfPara().substring(0, 200));
        }
        if (o.getIfMsg() != null && o.getIfMsg().length() >= 200) {
            o.setIfMsg(o.getIfMsg().substring(0, 200));
        }
        logger.info(JSON.toJSONString(o));
        db.run(Update.entity(Sys_.T15_IF_LOG).entry(o));
    }

    public T15IfLog get(String id) {
        Optional<T15IfLog> op = db.run(Select.from(Sys_.T15_IF_LOG).where(o -> o.ID().eq(id))).first(T15IfLog.class);
        if (op.isPresent()) {
            return op.get();
        }
        return null;
    }

    // public Sys015Email dataCollect(String id) {
    // Sys015Email data = new Sys015Email();
    // T15IfLog o = get(id);
    // T11IfManager p = ifsManageDao.getByCode(o.getIfCode());
    // data.setId(o.getId());
    // data.setIfCode(o.getIfCode());
    // data.setIfName(p.getIfName());
    // data.setIfPara(o.getIfPara());
    // data.setIfMsg("");
    // data.setIfResult(o.getIfResult());
    // data.setTotalNum(o.getTotalNum());
    // data.setSuccessNum(o.getSuccessNum());
    // data.setIgnoreNum(o.getIgnoreNum());
    // data.setErrorNum(o.getErrorNum());
    // data.setStartTime(o.getStartTime());
    // data.setFinishTime(o.getFinishTime());
    // data.setConsuming((o.getStartTime() == null || o.getFinishTime() == null) ?
    // null
    // : Duration.between(o.getStartTime(), o.getFinishTime()).toMillis() / 1000);
    // data.setCdBy(o.getCdBy());

    // String result = "成功";
    // if (o.getErrorNum() > 0) {
    // data.setIfMsg(o.getIfMsg());
    // result = "有异常";
    // }

    // String title = MessageTools.getMsgText(rbms, "MAIL_TITLE",
    // ConfigConstants.BTP_APP_ID, o.getIfCode(),
    // p.getIfName(), result);
    // data.setTitle(title);

    // return data;
    // }

}
