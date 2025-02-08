package customer.service.comm;

import com.alibaba.excel.util.StringUtils;
import com.sap.cds.services.messages.Messages;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.TransactionStatus;

import cds.gen.sys.T08ComOpD;
import cds.gen.sys.T11IfManager;
import customer.bean.ifm.IFLog;
import customer.comm.constant.ConfigConstants;
import customer.comm.tool.MessageTools;
import customer.dao.sys.IFLogDao;
// import customer.dao.sys.IFMd5ValueDao;
import customer.dao.sys.IFSManageDao;
import customer.dao.sys.SysD008Dao;
import customer.tool.UWebConstants;

//接口共同service
public class IfmService extends TranscationService {

    @Autowired
    public IFSManageDao ifDao;

    @Autowired
    public IFLogDao ifLogDao;

    @Autowired
    public SysD008Dao sysD08Dao;

    @Autowired
    public ResourceBundleMessageSource rbms;

    public T08ComOpD t08ComOpD = T08ComOpD.create();

    //考虑到每次数据都会调用数据库，所以把数据缓存起来
    /**
     * 输入code提前得到要判断的数据
     */
    public IfmService(String code) {
         t08ComOpD = sysD08Dao.getT08ByHcode(code).get(0);
    }
    public IfmService() {
        
    }

    

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

    /**
     * 检查工厂是不是当前系统对象内
     * @param v 工厂
     * @param o 购买组织
     * @return
     */
    public boolean checkPlant(String v) {
        
        if(t08ComOpD.getValue01().equals(v)){
            return true;
        }
        return false;  
        
    }
        /**
     * 检查购买组织是不是当前系统对象内
     * @param v 工厂
     * @param o 购买组织
     * @return
     */
    public boolean checkOrg(String org) {
        

        String[] split = t08ComOpD.getValue02().split(",");
        List<String> asList = Arrays.asList(split);
        if(asList.contains(org)){
            return true;
        }
        return false;  
    }

}
