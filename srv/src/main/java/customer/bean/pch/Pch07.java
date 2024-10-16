package customer.bean.pch;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.alibaba.fastjson.annotation.JSONField;

public class Pch07 {
    
    @JSONField(name = "STATUS")
    private String STATUS;
    @JSONField(name = "RESULT")
    private String RESULT;
    @JSONField(name = "MESSAGE")
    private String MESSAGE;
    @JSONField(name = "QUO_NUMBER")
    private String QUO_NUMBER;
    @JSONField(name = "QUO_ITEM")
    private String QUO_ITEM;
    @JSONField(name = "MATERIAL_NUMBER")
    private Integer MATERIAL_NUMBER;
    @JSONField(name = "BP_NUMBER")
    private String BP_NUMBER;
    @JSONField(name = "PLANT_ID")
    private String PLANT_ID;
    @JSONField(name = "QTY")
    private BigDecimal QTY;
    @JSONField(name = "VALIDATE_START")
    private LocalDate VALIDATE_START;
    @JSONField(name = "VALIDATE_END")
    private LocalDate VALIDATE_END;
    @JSONField(name = "UMC_COMMENT_1")
    private String UMC_COMMENT_1;
    @JSONField(name = "UMC_COMMENT_2")
    private String UMC_COMMENT_2;
    @JSONField(name = "INITIAL_OBJ")
    private String INITIAL_OBJ;
    public String getSTATUS() {
        return STATUS;
    }
    public void setSTATUS(String sTATUS) {
        STATUS = sTATUS;
    }
    public String getRESULT() {
        return RESULT;
    }
    public void setRESULT(String rESULT) {
        RESULT = rESULT;
    }
    public String getMESSAGE() {
        return MESSAGE;
    }
    public void setMESSAGE(String mESSAGE) {
        MESSAGE = mESSAGE;
    }
    public String getQUO_NUMBER() {
        return QUO_NUMBER;
    }
    public void setQUO_NUMBER(String qUO_NUMBER) {
        QUO_NUMBER = qUO_NUMBER;
    }
    public String getQUO_ITEM() {
        return QUO_ITEM;
    }
    public void setQUO_ITEM(String qUO_ITEM) {
        QUO_ITEM = qUO_ITEM;
    }
    public Integer getMATERIAL_NUMBER() {
        return MATERIAL_NUMBER;
    }
    public void setMATERIAL_NUMBER(Integer mATERIAL_NUMBER) {
        MATERIAL_NUMBER = mATERIAL_NUMBER;
    }
    public String getBP_NUMBER() {
        return BP_NUMBER;
    }
    public void setBP_NUMBER(String bP_NUMBER) {
        BP_NUMBER = bP_NUMBER;
    }
    public String getPLANT_ID() {
        return PLANT_ID;
    }
    public void setPLANT_ID(String pLANT_ID) {
        PLANT_ID = pLANT_ID;
    }
    public BigDecimal getQTY() {
        return QTY;
    }
    public void setQTY(BigDecimal qTY) {
        QTY = qTY;
    }
    public LocalDate getVALIDATE_START() {
        return VALIDATE_START;
    }
    public void setVALIDATE_START(LocalDate vALIDATE_START) {
        VALIDATE_START = vALIDATE_START;
    }
    public LocalDate getVALIDATE_END() {
        return VALIDATE_END;
    }
    public void setVALIDATE_END(LocalDate vALIDATE_END) {
        VALIDATE_END = vALIDATE_END;
    }
    public String getUMC_COMMENT_1() {
        return UMC_COMMENT_1;
    }
    public void setUMC_COMMENT_1(String uMC_COMMENT_1) {
        UMC_COMMENT_1 = uMC_COMMENT_1;
    }
    public String getUMC_COMMENT_2() {
        return UMC_COMMENT_2;
    }
    public void setUMC_COMMENT_2(String uMC_COMMENT_2) {
        UMC_COMMENT_2 = uMC_COMMENT_2;
    }
    public String getINITIAL_OBJ() {
        return INITIAL_OBJ;
    }
    public void setINITIAL_OBJ(String iNITIAL_OBJ) {
        INITIAL_OBJ = iNITIAL_OBJ;
    }

}