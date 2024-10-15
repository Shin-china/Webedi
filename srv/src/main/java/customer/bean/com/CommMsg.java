package customer.bean.com;

import customer.bean.com.UmcConstants;

public class CommMsg {

    private String msgTxt;
    private String msgType = UmcConstants.IF_STATUS_E;
    private String msgCode;
    private Object[] msgPara;

    private byte[] dataByte;

    /**
     * @return the msgTxt
     */
    public String getMsgTxt() {
        return msgTxt;
    }

    public boolean isSuccess() {
        if (UmcConstants.IF_STATUS_S.equals(msgType))
            return true;
        return false;
    }

    /**
     * @param msgTxt the msgTxt to set
     */
    public void setMsgTxt(String msgTxt) {
        this.msgTxt = msgTxt;
    }

    /**
     * @return the msgCode
     */
    public String getMsgCode() {
        return msgCode;
    }

    /**
     * @param msgCode the msgCode to set
     */
    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    /**
     * @return the msgPara
     */
    public Object[] getMsgPara() {
        return msgPara;
    }

    /**
     * @param msgPara the msgPara to set
     */
    public void setMsgPara(Object[] msgPara) {
        this.msgPara = msgPara;
    }

    /**
     * @return the msgType
     */
    public String getMsgType() {
        return msgType;
    }

    /**
     * @param msgType the msgType to set
     */
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    /**
     * @return the dataByte
     */
    public byte[] getDataByte() {
        return dataByte;
    }

    /**
     * @param dataByte the dataByte to set
     */
    public void setDataByte(byte[] dataByte) {
        this.dataByte = dataByte;
    }

}
