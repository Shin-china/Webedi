package customer.bean.ifm;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.transaction.TransactionDefinition;

import cds.gen.sys.T11IfManager;
import cds.gen.sys.T15IfLog;
import customer.comm.constant.UmcConstants;
import customer.comm.odata.OdateValueTool;
import customer.comm.tool.DateTools;
import customer.comm.tool.UniqueIDTool;

public class IFLog {

    private T15IfLog t15log; // 准备插入DB的履历。

    private T11IfManager ifConfig; // 接口配置

    private Instant maxInstant;

    TransactionDefinition td;

    public IFLog(String _ifCode) {
        t15log = T15IfLog.create(UniqueIDTool.getUUID());
        t15log.setIfCode(_ifCode);
        t15log.setErrorNum(0);
        t15log.setSuccessNum(0);
        t15log.setIgnoreNum(0);
        t15log.setTotalNum(0);
        t15log.setStartTime(DateTools.getInstantNow());
    }

    public Integer getSuccessNum() {
        return t15log.getSuccessNum() + t15log.getIgnoreNum();
    }

    public Integer getErrorNum() {
        return t15log.getErrorNum();
    }

    public long getConsumTimeS() { // 得到秒级别的耗时
        if (t15log.getFinishTime() == null)
            t15log.setFinishTime(DateTools.getInstantNow());
        return DateTools.cy(t15log.getStartTime(), t15log.getFinishTime(), ChronoUnit.SECONDS);
    }

    // 返回给CPI的消息
    public ReturnCPILog getCpiReturn() {
        ReturnCPILog cpi = new ReturnCPILog();
        cpi.setMsg(t15log.getIfMsg());
        cpi.setErrorNum(t15log.getErrorNum());
        cpi.setSuccessNum(t15log.getSuccessNum() + t15log.getIgnoreNum());
        cpi.setResult(t15log.getIfResult());
        return cpi;
    }

    // 设定接口成功
    public void setSuccessMsg(String message) {
        t15log.setIfResult(UmcConstants.IF_STATUS_S);
        t15log.setIfMsg(message);

    }

    // 设定接口失败
    public void setFairMsg(String message) {
        t15log.setIfResult(UmcConstants.IF_STATUS_E);
        t15log.setIfMsg(message);
        t15log.setFinishTime(DateTools.getInstantNow());
    }

    /**
     * @return the t15log
     */
    public T15IfLog gett15log() {
        return t15log;
    }

    /**
     * @param t15log the t15log to set
     */
    public void sett15log(T15IfLog t15log) {
        this.t15log = t15log;
    }

    /**
     * @return the ifConfig
     */
    public T11IfManager getIfConfig() {
        return ifConfig;
    }

    /**
     * @param ifConfig the ifConfig to set
     */
    public void setIfConfig(T11IfManager ifConfig) {
        this.ifConfig = ifConfig;
    }

    // 得到取数的页数
    public int getPageCount() {
        if (ifConfig != null && ifConfig.getPageRecord() != null && t15log.getTotalNum() != null) {
            if (ifConfig.getPageRecord() > 0 && t15log.getTotalNum() > 0) {
                int a = t15log.getTotalNum();
                int b = ifConfig.getPageRecord();
                if ((a % b) == 0) {
                    return a / b;
                } else {
                    return (a / b) + 1;
                }
            }

        }

        return 1;
    }

    public void addSuccessNum() {
        t15log.setSuccessNum(t15log.getSuccessNum() + 1);
        t15log.setErrorNum(t15log.getErrorNum() - 1);
    }

    public void setTotalNum(Integer num) {
        t15log.setTotalNum(num);
        t15log.setErrorNum(num);
    }
    public void addSuccessCount() {
        t15log.setSuccessNum(t15log.getSuccessNum() + 1);
        t15log.setTotalNum(t15log.getTotalNum()+1);
        
    }
    public void addErrorCount() {
        t15log.setErrorNum(t15log.getErrorNum() + 1);
        t15log.setTotalNum(t15log.getTotalNum()+1);
        
    }
    public void addIgnoreNum() {
        t15log.setIgnoreNum(t15log.getIgnoreNum() + 1);
        t15log.setErrorNum(t15log.getErrorNum() - 1);
    }

    public void setFinish() {
        if (t15log.getFinishTime() == null)
            t15log.setFinishTime(DateTools.getInstantNow());
    }

    public Instant getMaxInstant() {
        return maxInstant;
    }

    public void setMaxInstant(Instant newInstant) {
        this.maxInstant = OdateValueTool.getMaxInstant(maxInstant, newInstant);

    }

    public void setNextPara() {
        if (this.getMaxInstant() != null && this.getErrorNum() == 0) { // 没有错误记录的时候
            this.getIfConfig().setNextPara(this.getMaxInstant().toString()); // 设定最大变更时间，作为下次的参数
        }
    }

    /**
     * @return the td
     */
    public TransactionDefinition getTd() {
        return td;
    }

    /**
     * @param td the td to set
     */
    public void setTd(TransactionDefinition td) {
        this.td = td;
    }

}
