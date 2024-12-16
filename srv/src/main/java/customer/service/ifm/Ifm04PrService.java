package customer.service.ifm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.alibaba.fastjson.JSON;
import java.time.LocalDate;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import cds.gen.mst.T01SapMat;
import cds.gen.pch.T09Forcast;
import cds.gen.sys.T11IfManager;
import customer.bean.ifm.IFLog;
import customer.bean.mst.SapMstRoot;
import customer.bean.mst.Value;
import customer.bean.pch.Items;
import customer.bean.pch.SapPchRoot;
import customer.bean.pch.SapPrRoot;
import customer.comm.tool.MessageTools;
import customer.dao.pch.PurchaseDataDao;
import customer.dao.sys.IFSManageDao;
import customer.odata.S4OdataTools;
import customer.service.comm.IfmService;
import customer.tool.StringTool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class Ifm04PrService extends IfmService {

    private static final Logger logger = LoggerFactory.getLogger(Ifm04PrService.class);

    @Autowired
    private IFSManageDao ifsManageDao;

    @Autowired
    private PurchaseDataDao PchDao;

    public SapPrRoot get(IFLog log) throws Exception {

        T11IfManager info = this.getIfMnager(log);

        // 调用 Web Service 的 get 方法
        String a = S4OdataTools.get(info, 0, null, null);

        return JSON.parseObject(a, SapPrRoot.class);
        
    }

    public void process(IFLog log) {

        log.setTd(super.transactionInit()); // 事务初始换

        try {
            this.insertLog(log);// 插入日志

            SapPrRoot data = get(log);

            // log.setTotalNum(data.get__count());// 得到记录总数
            // int pageCount = log.getPageCount(); // 得到页数

            onePage(log, data.getItems()); // 处理第0页的数据

            log.setSuccessMsg(MessageTools.getMsgText(rbms, "IFM03_01", log.getSuccessNum(), log.getErrorNum(),
            log.getConsumTimeS()));

        } catch (Exception e) {
            e.printStackTrace();
            log.setFairMsg(e.getMessage());
        } finally {
            log.setFinish(); // 设定接口完了时间
            this.updateLogAndIf(log, log.getIfConfig());

        }

    }
    
    public IFLog onePage(IFLog log, List<Items> list) {
        
        if (list != null && list.size() > 0) {

            for (Items v : list) { // 单个品目

                TransactionStatus s = null;

                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    s = this.begin(log.getTd()); // 开启新事务

                    // 处理单个 PR 数据
                    T09Forcast o = T09Forcast.create();
                    o.setPrNumber(v.getPrNumber());
                    o.setDNo(Integer.parseInt(v.getDNo())); // 假设 v.getDNo() 返回的是 String 类型
                    o.setPurGroup(v.getPurGroup());
                    o.setSupplier(v.getSupplier().replaceFirst("^0+(?!$)", ""));

                    o.setMaterial(v.getMaterial());
                    o.setMaterialText(v.getMaterialText());
                    o.setDelivaryDays(Integer.parseInt(v.getDelivaryDays())); // 假设 v.getDelivaryDays() 返回的是
                                                                                 // String 类型
                    o.setArrangeStartDate(LocalDate.parse(v.getArrangeStartDate(), formatter)); // 假设
                                                                                                   // v.getArrangeStartDate()
                                                                                                   // // 为字符串
                    o.setArrangeEndDate(LocalDate.parse(v.getArrangeEndDate(), formatter)); // 假设
                                                                                               // v.getArrangeEndDate()
                                                                                               // 为字符串
                    o.setPlant(v.getPlant());
                    o.setArrangeQty(new BigDecimal(v.getArrangeQty())); // 假设 v.getArrangeQty() 返回的是 String 类型
                    o.setName1(v.getName1());
                    o.setMinDeliveryQty(new BigDecimal(v.getMinDeliveryQty())); // 假设 v.getMinDeliveryQty() 返回
                                                                                   // String 类型
                    o.setManufCode(v.getManufCode());
                    o.setPurGroupName(v.getPurGroupName());

                    o.setSupplierTel(v.getSupplierphonenumber());
                    o.setSupplierMaterial(v.getSuppliermaterialnumber());

                    PchDao.modify3(o);

                    this.commit(s); // 提交事务
                    log.addSuccessNum();

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
