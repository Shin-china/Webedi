package customer.bean.ifm;

import customer.comm.constant.UmcConstants;

public class ReturnCPILog {

    private String result;

    private String msg;

    private int totalNum;

    private int successNum;

    private int errorNum;

    /**
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return the totalNum
     */
    public int getTotalNum() {
        return totalNum;
    }

    /**
     * @param totalNum the totalNum to set
     */
    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    /**
     * @return the successNum
     */
    public int getSuccessNum() {
        return successNum;
    }

    /**
     * @param successNum the successNum to set
     */
    public void setSuccessNum(int successNum) {
        this.successNum = successNum;
    }

    /**
     * @return the errorNum
     */
    public int getErrorNum() {
        return errorNum;
    }

    /**
     * @param errorNum the errorNum to set
     */
    public void setErrorNum(int errorNum) {
        this.errorNum = errorNum;
    }

}
