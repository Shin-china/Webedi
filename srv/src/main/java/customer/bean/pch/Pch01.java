package customer.bean.pch;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Pch01 {

    @JSONField(name = "SUCCESS")
    private Boolean SUCCESS = true;
    // @JSONField(name = "D_NO")
    // private String D_NO;
    @JSONField(name = "I_CON")
    private String I_CON;

    @JSONField(name = "TYPE")
    private String TYPE;

    @JSONField(name = "PO_TYPE")
    private String PO_TYPE;

    @JSONField(name = "PO_NO")
    private String PO_NO;

    @JSONField(name = "D_NO")
    private Integer D_NO;

    @JSONField(name = "SEQ")
    private Integer SEQ;

    @JSONField(name = "MAT_ID")
    private String MAT_ID;

    @JSONField(name = "PO_D_TXZ01")
    private String PO_D_TXZ01;

    @JSONField(name = "PO_PUR_QTY")
    private BigDecimal PO_PUR_QTY;

    @JSONField(name = "PO_PUR_UNIT")
    private String PO_PUR_UNIT;

    @JSONField(name = "DELIVERY_DATE")
    private LocalDate DELIVERY_DATE;

    @JSONField(name = "QUANTITY")
    private BigDecimal QUANTITY;

    @JSONField(name = "SUPPLIER_MAT")
    private String SUPPLIER_MAT;

    @JSONField(name = "STATUS")
    private String STATUS;

    @JSONField(name = "MSG_TEXT")
    private String MSG_TEXT;

    @JSONField(name = "DELETE")
    private String DELETE;

    @JSONField(name = "ExtNumber")
    private String ExtNumber;

    public Integer getSEQ() {
        return SEQ;
    }

    public void setSEQ(Integer sEQ) {
        SEQ = sEQ;
    }

    public String getExtNumber() {
        return ExtNumber;
    }

    public void setExtNumber(String extNumber) {
        ExtNumber = extNumber;
    }

    /**
     * @return the sUCCESS
     */
    public Boolean getSUCCESS() {
        return SUCCESS;
    }

    /**
     * @param sUCCESS the sUCCESS to set
     */
    public void setSUCCESS(Boolean sUCCESS) {
        SUCCESS = sUCCESS;
    }

    public String getI_CON() {
        return I_CON;
    }

    public void setI_CON(String i_CON) {
        I_CON = i_CON;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String tYPE) {
        TYPE = tYPE;
    }

    public String getPO_TYPE() {
        return PO_TYPE;
    }

    public void setPO_TYPE(String pO_TYPE) {
        PO_TYPE = pO_TYPE;
    }

    public String getPO_NO() {
        return PO_NO;
    }

    public void setPO_NO(String pO_NO) {
        PO_NO = pO_NO;
    }

    public Integer getD_NO() {
        return D_NO;
    }

    public void setD_NO(Integer d_NO) {
        D_NO = d_NO;
    }

    public String getPO_D_TXZ01() {
        return PO_D_TXZ01;
    }

    public void setPO_D_TXZ01(String pO_D_TXZ01) {
        PO_D_TXZ01 = pO_D_TXZ01;
    }

    public BigDecimal getPO_PUR_QTY() {
        return PO_PUR_QTY;
    }

    public void setPO_PUR_QTY(BigDecimal pO_PUR_QTY) {
        PO_PUR_QTY = pO_PUR_QTY;
    }

    public String getPO_PUR_UNIT() {
        return PO_PUR_UNIT;
    }

    public void setPO_PUR_UNIT(String pO_PUR_UNIT) {
        PO_PUR_UNIT = pO_PUR_UNIT;
    }

    public LocalDate getDELIVERY_DATE() {
        return DELIVERY_DATE;
    }

    public void setDELIVERY_DATE(LocalDate dELIVERY_DATE) {
        DELIVERY_DATE = dELIVERY_DATE;
    }

    public BigDecimal getQUANTITY() {
        return QUANTITY;
    }

    public void setQUANTITY(BigDecimal qUANTITY) {
        QUANTITY = qUANTITY;
    }

    public String getSUPPLIER_MAT() {
        return SUPPLIER_MAT;
    }

    public void setSUPPLIER_MAT(String sUPPLIER_MAT) {
        SUPPLIER_MAT = sUPPLIER_MAT;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String sTATUS) {
        STATUS = sTATUS;
    }

    public String getMSG_TEXT() {
        return MSG_TEXT;
    }

    public String setMSG_TEXT(String mSG_TEXT) {
        return MSG_TEXT = mSG_TEXT;
    }

    public String getMAT_ID() {
        return MAT_ID;
    }

    public void setMAT_ID(String mAT_ID) {
        MAT_ID = mAT_ID;
    }

    public Object getDELETE() {
        return DELETE;
    }

}
