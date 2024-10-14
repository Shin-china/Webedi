package customer.bean.pch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import org.apache.catalina.util.StringUtil;

import com.alibaba.fastjson.annotation.JSONField;

public class Pch06 {

    @JSONField(name = "PO_NO")
    private String PO_NO;
    @JSONField(name = "D_NO")
    private String D_NO;
    @JSONField(name = "SEQ")
    private String SEQ;
    @JSONField(name = "MAT_ID")
    private String MAT_ID;
    @JSONField(name = "SHELF")
    private String SHELF;
    @JSONField(name = "DELIVERY_DATE")
    private String DELIVERY_DATE;
    @JSONField(name = "QUANTITY")
    private BigDecimal QUANTITY;
    @JSONField(name = "STATUS")
    private String STATUS;
    @JSONField(name = "PO_PUR_QTY")
    private String PO_PUR_QTY;
    @JSONField(name = "PO_PUR_UNIT")
    private String PO_PUR_UNIT;
    @JSONField(name = "SUPPLIER")
    private String SUPPLIER;
    @JSONField(name = "BP_NAME1")
    private String BP_NAME1;
    @JSONField(name = "LOCNAME")
    private String LOCNAME;
    @JSONField(name = "CURRENCY")
    private String CURRENCY;
    @JSONField(name = "PRICE")
    private String PRICE;
    @JSONField(name = "DEL_AMOUNT")
    private String DEL_AMOUNT;
    @JSONField(name = "PO_DATE")
    private String PO_DATE;
    @JSONField(name = "CUSTOMER_MAT")
    private String CUSTOMER_MAT;
    @JSONField(name = "SUPPLIER_MAT")
    private String SUPPLIER_MAT;
    @JSONField(name = "PO_D_DATE")
    private String PO_D_DATE;
    @JSONField(name = "MEMO")
    private String MEMO;
    @JSONField(name = "ExtNumber")
    private String ExtNumber;

    public String getDELIVERY_DATE() {
        return DELIVERY_DATE;
    }

    public void setDELIVERY_DATE(String dELIVERY_DATE) {
        DELIVERY_DATE = dELIVERY_DATE;
    }

    public String getPO_DATE() {
        return PO_DATE;
    }

    public void setPO_DATE(String pO_DATE) {
        PO_DATE = pO_DATE;
    }

    public String getPO_D_DATE() {
        return PO_D_DATE;
    }

    public void setPO_D_DATE(String pO_D_DATE) {
        PO_D_DATE = pO_D_DATE;
    }

    public String getPO_NO() {
        return PO_NO;
    }

    public void setPO_NO(String pO_NO) {
        PO_NO = pO_NO;
    }

    public String getD_NO() {
        return D_NO;
    }

    public void setD_NO(String d_NO) {
        D_NO = d_NO;
    }

    public String getSEQ() {
        return SEQ;
    }

    public void setSEQ(String sEQ) {
        SEQ = sEQ;
    }

    public String getMAT_ID() {
        return MAT_ID;
    }

    public void setMAT_ID(String mAT_ID) {
        MAT_ID = mAT_ID;
    }

    public String getSHELF() {
        return SHELF;
    }

    public void setSHELF(String sHELF) {
        SHELF = sHELF;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String sTATUS) {
        STATUS = sTATUS;
    }

    public String getPO_PUR_QTY() {
        return PO_PUR_QTY;
    }

    public void setPO_PUR_QTY(String pO_PUR_QTY) {
        PO_PUR_QTY = pO_PUR_QTY;
    }

    public String getPO_PUR_UNIT() {
        return PO_PUR_UNIT;
    }

    public void setPO_PUR_UNIT(String pO_PUR_UNIT) {
        PO_PUR_UNIT = pO_PUR_UNIT;
    }

    public String getSUPPLIER() {
        return SUPPLIER;
    }

    public void setSUPPLIER(String sUPPLIER) {
        SUPPLIER = sUPPLIER;
    }

    public String getBP_NAME1() {
        return BP_NAME1;
    }

    public void setBP_NAME1(String bP_NAME1) {
        BP_NAME1 = bP_NAME1;
    }

    public String getLOCNAME() {
        return LOCNAME;
    }

    public void setLOCNAME(String lOCNAME) {
        LOCNAME = lOCNAME;
    }

    public String getCURRENCY() {
        return CURRENCY;
    }

    public void setCURRENCY(String cURRENCY) {
        CURRENCY = cURRENCY;
    }

    public String getPRICE() {
        return PRICE;
    }

    public void setPRICE(String pRICE) {
        PRICE = pRICE;
    }

    public String getDEL_AMOUNT() {
        return DEL_AMOUNT;
    }

    public void setDEL_AMOUNT(String dEL_AMOUNT) {
        DEL_AMOUNT = dEL_AMOUNT;
    }

    public String getCUSTOMER_MAT() {
        return CUSTOMER_MAT;
    }

    public void setCUSTOMER_MAT(String cUSTOMER_MAT) {
        CUSTOMER_MAT = cUSTOMER_MAT;
    }

    public String getSUPPLIER_MAT() {
        return SUPPLIER_MAT;
    }

    public void setSUPPLIER_MAT(String sUPPLIER_MAT) {
        SUPPLIER_MAT = sUPPLIER_MAT;
    }

    public String getMEMO() {
        return MEMO;
    }

    public void setMEMO(String mEMO) {
        MEMO = mEMO;
    }

    public String getExtNumber() {
        return ExtNumber;
    }

    public void setExtNumber(String extNumber) {
        ExtNumber = extNumber;
    }

    public BigDecimal getQUANTITY() {
        return QUANTITY;
    }

    public void setQUANTITY(BigDecimal qUANTITY) {
        QUANTITY = qUANTITY;
    }

}
