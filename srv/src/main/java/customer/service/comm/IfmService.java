package customer.service.comm;

import com.sap.cds.services.messages.Messages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.TransactionStatus;

import cds.gen.sys.T11IfManager;
import customer.bean.ifm.IFLog;
import customer.comm.tool.MessageTools;
import customer.dao.sys.IFLogDao;
// import customer.dao.sys.IFMd5ValueDao;
import customer.dao.sys.IFSManageDao;

//接口共同service
public class IfmService extends TranscationService {

    @Autowired
    public IFSManageDao ifDao;

    @Autowired
    public IFLogDao ifLogDao;

    @Autowired
    public ResourceBundleMessageSource rbms;

    //暂时不需要md5
    // @Autowired
    // public IFMd5ValueDao md5Dao;

    public int printLogRows = 100;

    public T11IfManager getIfMnager(IFLog log) {

        String ifCode = log.gett15log().getIfCode();

        T11IfManager ifs = ifDao.getByCode(ifCode);
        if (ifs == null) {
            throw new RuntimeException(MessageTools.getMsgText(rbms, "info", ifCode));
        }
        else {
            log.setIfConfig(ifs);
        }

        return ifs;
    }

    public void insertLog(IFLog log) {
        TransactionStatus s = this.begin(log.getTd());
        ifLogDao.insert(log.gett15log());
        this.commit(s);
    }

    public void updateLog(IFLog log) {
        TransactionStatus s = this.begin(log.getTd());
        ifLogDao.update(log.gett15log());
        this.commit(s);
    }

    public void updateLogAndIf(IFLog log, T11IfManager ifm) {
        TransactionStatus s = this.begin(log.getTd());
        ifLogDao.update(log.gett15log());
        ifDao.updateIfManager(ifm);
        this.commit(s);
    }

    

}
