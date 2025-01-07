package customer.service.ifm;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.alibaba.fastjson.JSON;

import cds.gen.mst.T01SapMat;
import cds.gen.mst.T06MatPlant;
import cds.gen.sys.T06DocNo;
import cds.gen.sys.T11IfManager;
import customer.bean.mst.Value;
import customer.comm.tool.MessageTools;
import customer.bean.ifm.IFLog;
import customer.bean.mst.SapMstRoot;
import customer.dao.mst.MaterialDataDao;
import customer.dao.sys.IFSManageDao;
import customer.dao.sys.SysD008Dao;
import customer.odata.S4OdataTools;
import customer.service.comm.IfmService;
import customer.tool.StringTool;
import customer.odata.S4OdataTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class Ifm02MstService extends IfmService {
    private static final Logger logger = LoggerFactory.getLogger(Ifm02MstService.class);

    @Autowired
    private IFSManageDao ifsManageDao1;

    @Autowired
    private MaterialDataDao MSTDao;

    @Autowired
    private SysD008Dao sysD008Dao;

    public String tt;

    // 得到SAP的物料信息
    private SapMstRoot getS4(IFLog log, Integer currentPage, String matId) throws Exception {
        
        T11IfManager info = this.getIfMnager(log);
        HashMap<String, String> addParaMap = S4OdataTools.setCountPara(true, null);
        String para = "";
        if (!StringTool.isEmpty(matId)) {
            para = "Product  eq '" + matId + "' "; // 设定最大时间查询条件
            addParaMap.put("$filter", StringTool.encodeURIComponent(para)); // Greater than or equal
        } else {
            if (!StringTool.isEmpty(info.getNextPara())) {
                para = "LastChangeDateTime  ge " + info.getNextPara(); // 设定最大时间查询条件
                addParaMap.put("$filter", StringTool.encodeURIComponent(para)); // Greater than or equal
            }
        }

        log.gett15log().setIfPara(para);

        String a = S4OdataTools.get(info, currentPage, addParaMap, null);

        SapMstRoot ps = JSON.parseObject(a, SapMstRoot.class);

        if (currentPage == 0) { // 第一次执行的时候，指定取的总记录数
            ps.set__count(S4OdataTools.getCount(a));
        }

        return ps;
    }

    public IFLog process(IFLog log, String matId) {
        log.setTd(super.transactionInit()); // 事务初始换

        try {
            this.insertLog(log);// 插入日志
            SapMstRoot data = getS4(log, 0, matId);
            log.setTotalNum(data.get__count());// 得到记录总数
            int pageCount = log.getPageCount(); // 得到页数
            onePage(log, data.getValue()); // 处理第0页的数据

            if (pageCount > 1) {
                for (int i = 1; i < pageCount; i++) {
                    data = getS4(log, i, matId);
                    onePage(log, data.getValue());
                }

            }

            if (StringTool.isEmpty(matId)) {
                log.setNextPara();// 设定最大变更时间，作为下次的参数
            }

            log.setSuccessMsg(MessageTools.getMsgText(rbms, "IFM02_01", log.getSuccessNum(), log.getErrorNum(),
                    log.getConsumTimeS()));

        } catch (Exception e) {
            e.printStackTrace();
            log.setFairMsg(e.getMessage());
        } finally {
            log.setFinish(); // 设定接口完了时间
            this.updateLogAndIf(log, log.getIfConfig());

        }

        return log;

    }

    public IFLog onePage(IFLog log, List<Value> list) {
        String matId = null;
        if (list != null && list.size() > 0) {

            for (Value v : list) { // 单个品目

                TransactionStatus s = null;

                try {
                    s = this.begin(log.getTd()); // 开启新事务
                    matId = v.getProduct();
                    
                    T01SapMat ifMatInfo = MSTDao.S4Mat_2_locl_Mat(v, T01SapMat.create(matId)); // 得到接口获取的品目信息

                    MSTDao.modify(ifMatInfo);

                    log.addSuccessNum();

                    log.setMaxInstant(ifMatInfo.getSapUpTime()); // 设定当前履历的最大修改时间

                    this.commit(s); // 提交事务

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    this.rollback(s); // 回滚事务
                }

            }

        }
        return log;
    }

}
