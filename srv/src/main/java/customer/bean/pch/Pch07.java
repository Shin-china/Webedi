package customer.bean.pch;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Pch07 {

    @JSONField(name = "SUCCESS")
    private Boolean SUCCESS = true;
    @JSONField(name = "I_CON")
    private String I_CON;
    
    @JSONField(name = "STATUS")
    private String STATUS;

    @JSONField(name = "STATUS1")
    private String STATUS1;

    @JSONField(name = "RESULT")
    private String RESULT;

    @JSONField(name = "MESSAGE")
    private String MESSAGE;

    @JSONField(name = "QUO_NUMBER")
    private String QUO_NUMBER; // 購買見積番号

    @JSONField(name = "QUO_ITEM")
    private String QUO_ITEM; // 管理No

    @JSONField(name = "NO")
    private String NO; // No.

    @JSONField(name = "REFRENCE_NO")
    private String REFRENCE_NO; // 併記有無リファレンスNo

    @JSONField(name = "MATERIAL_NUMBER")
    private String MATERIAL_NUMBER; // SAP品番（任意）

    @JSONField(name = "CUST_MATERIAL")
    private String CUST_MATERIAL; // 顧客品番

    @JSONField(name = "MANUFACT_MATERIAL")
    private String MANUFACT_MATERIAL; // メーカー品番

    @JSONField(name = "Attachment")
    private String Attachment; // カスタム品図面 仕様添付

    @JSONField(name = "Material")
    private String Material; // 品名

    @JSONField(name = "MAKER")
    private String MAKER; // メーカ

    @JSONField(name = "UWEB_USER")
    private String UWEB_USER; // 仕入先連絡先（WEB EDIの担当）（必須）

    @JSONField(name = "BP_NUMBER")
    private String BP_NUMBER; // SAP BP（任意）

    @JSONField(name = "INITIAL_OBJ")
    private String INITIAL_OBJ; // イ二シ儿費用対象

    @JSONField(name = "QTY")
    private Double QTY; // 数量

    @JSONField(name = "PLANT_ID")
    private String PLANT_ID; // 管理No

    @JSONField(name = "MANUFACT_CODE")
    private String MANUFACT_CODE; // Manfact. Code name

    @JSONField(name = "CURRENCY")
    private String CURRENCY; // 通貨

    @JSONField(name = "PRICE")
    private Double PRICE; // 単価

    @JSONField(name = "PRICE_CONTROL")
    private String PRICE_CONTROL; // Date of pricing control

    @JSONField(name = "LEAD_TIME")
    private String LEAD_TIME; // LT（日数）

    @JSONField(name = "MOQ")
    private String MOQ; // MOQ

    @JSONField(name = "UNIT")
    private String UNIT; // Base Unit of Measure

    @JSONField(name = "ORIGINAL_COU")
    private String ORIGINAL_COU; // 原産国

    @JSONField(name = "Incoterms")
    private String Incoterms; // Incoterms 1

    @JSONField(name = "Incoterms_Text")
    private String Incoterms_Text; // Incoterms 1（納入場所）

    @JSONField(name = "VALIDATE_START")
    private String VALIDATE_START;

    @JSONField(name = "VALIDATE_END")
    private String VALIDATE_END;

    @JSONField(name = "UMC_COMMENT_1")
    private String UMC_COMMENT_1;

    @JSONField(name = "UMC_COMMENT_2")
    private String UMC_COMMENT_2;

    // public Boolean getSUCCESS() {
    //     return SUCCESS;
    // }

    // public void setSUCCESS(Boolean sUCCESS) {
    //     SUCCESS = sUCCESS;
    // }

    // public String getI_CON() {
    //     return I_CON;
    // }

    // public void setI_CON(String i_CON) {
    //     I_CON = i_CON;
    // }

    // public String getSTATUS() {
    //     return STATUS;
    // }

    // public void setSTATUS(String sTATUS) {
    //     STATUS = sTATUS;
    // }

    // public String getSTATUS1() {
    //     return STATUS1;
    // }

    // public void setSTATUS1(String sTATUS1) {
    //     STATUS1 = sTATUS1;
    // }

    // public String getRESULT() {
    //     return RESULT;
    // }

    // public void setRESULT(String rESULT) {
    //     RESULT = rESULT;
    // }

    // public String getMESSAGE() {
    //     return MESSAGE;
    // }

    // public void setMESSAGE(String mESSAGE) {
    //     MESSAGE = mESSAGE;
    // }

    // public String getQUO_NUMBER() {
    //     return QUO_NUMBER;
    // }

    // public void setQUO_NUMBER(String qUO_NUMBER) {
    //     QUO_NUMBER = qUO_NUMBER;
    // }

    // public String getQUO_ITEM() {
    //     return QUO_ITEM;
    // }

    // public void setQUO_ITEM(String qUO_ITEM) {
    //     QUO_ITEM = qUO_ITEM;
    // }

    // public String getNO() {
    //     return NO;
    // }

    // public void setNO(String nO) {
    //     NO = nO;
    // }

    // public String getREFRENCE_NO() {
    //     return REFRENCE_NO;
    // }

    // public void setREFRENCE_NO(String rEFRENCE_NO) {
    //     REFRENCE_NO = rEFRENCE_NO;
    // }

    // public String getMATERIAL_NUMBER() {
    //     return MATERIAL_NUMBER;
    // }

    // public void setMATERIAL_NUMBER(String mATERIAL_NUMBER) {
    //     MATERIAL_NUMBER = mATERIAL_NUMBER;
    // }

    // public String getCUST_MATERIAL() {
    //     return CUST_MATERIAL;
    // }

    // public void setCUST_MATERIAL(String cUST_MATERIAL) {
    //     CUST_MATERIAL = cUST_MATERIAL;
    // }

    // public String getMANUFACT_MATERIAL() {
    //     return MANUFACT_MATERIAL;
    // }

    // public void setMANUFACT_MATERIAL(String mANUFACT_MATERIAL) {
    //     MANUFACT_MATERIAL = mANUFACT_MATERIAL;
    // }

    // public String getAttachment() {
    //     return Attachment;
    // }

    // public void setAttachment(String attachment) {
    //     Attachment = attachment;
    // }

    // public String getMaterial() {
    //     return Material;
    // }

    // public void setMaterial(String material) {
    //     Material = material;
    // }

    // public String getMAKER() {
    //     return MAKER;
    // }

    // public void setMAKER(String mAKER) {
    //     MAKER = mAKER;
    // }

    // public String getUWEB_USER() {
    //     return UWEB_USER;
    // }

    // public void setUWEB_USER(String uWEB_USER) {
    //     UWEB_USER = uWEB_USER;
    // }

    // public String getBP_NUMBER() {
    //     return BP_NUMBER;
    // }

    // public void setBP_NUMBER(String bP_NUMBER) {
    //     BP_NUMBER = bP_NUMBER;
    // }

    // public String getINITIAL_OBJ() {
    //     return INITIAL_OBJ;
    // }

    // public void setINITIAL_OBJ(String iNITIAL_OBJ) {
    //     INITIAL_OBJ = iNITIAL_OBJ;
    // }

    // public Double getQTY() {
    //     return QTY;
    // }

    // public void setQTY(Double qTY) {
    //     QTY = qTY;
    // }

    // public String getPLANT_ID() {
    //     return PLANT_ID;
    // }

    // public void setPLANT_ID(String pLANT_ID) {
    //     PLANT_ID = pLANT_ID;
    // }

    // public String getMANUFACT_CODE() {
    //     return MANUFACT_CODE;
    // }

    // public void setMANUFACT_CODE(String mANUFACT_CODE) {
    //     MANUFACT_CODE = mANUFACT_CODE;
    // }

    // public String getCURRENCY() {
    //     return CURRENCY;
    // }

    // public void setCURRENCY(String cURRENCY) {
    //     CURRENCY = cURRENCY;
    // }

    // public Double getPRICE() {
    //     return PRICE;
    // }

    // public void setPRICE(Double pRICE) {
    //     PRICE = pRICE;
    // }

    // public String getPRICE_CONTROL() {
    //     return PRICE_CONTROL;
    // }

    // public void setPRICE_CONTROL(String pRICE_CONTROL) {
    //     PRICE_CONTROL = pRICE_CONTROL;
    // }

    // public String getLEAD_TIME() {
    //     return LEAD_TIME;
    // }

    // public void setLEAD_TIME(String lEAD_TIME) {
    //     LEAD_TIME = lEAD_TIME;
    // }

    // public String getMOQ() {
    //     return MOQ;
    // }

    // public void setMOQ(String mOQ) {
    //     MOQ = mOQ;
    // }

    // public String getUNIT() {
    //     return UNIT;
    // }

    // public void setUNIT(String uNIT) {
    //     UNIT = uNIT;
    // }

    // public String getORIGINAL_COU() {
    //     return ORIGINAL_COU;
    // }

    // public void setORIGINAL_COU(String oRIGINAL_COU) {
    //     ORIGINAL_COU = oRIGINAL_COU;
    // }

    // public String getIncoterms() {
    //     return Incoterms;
    // }

    // public void setIncoterms(String incoterms) {
    //     Incoterms = incoterms;
    // }

    // public String getIncoterms_Text() {
    //     return Incoterms_Text;
    // }

    // public void setIncoterms_Text(String incoterms_Text) {
    //     Incoterms_Text = incoterms_Text;
    // }

    // public String getVALIDATE_START() {
    //     return VALIDATE_START;
    // }

    // public void setVALIDATE_START(String vALIDATE_START) {
    //     VALIDATE_START = vALIDATE_START;
    // }

    // public String getVALIDATE_END() {
    //     return VALIDATE_END;
    // }

    // public void setVALIDATE_END(String vALIDATE_END) {
    //     VALIDATE_END = vALIDATE_END;
    // }

    // public String getUMC_COMMENT_1() {
    //     return UMC_COMMENT_1;
    // }

    // public void setUMC_COMMENT_1(String uMC_COMMENT_1) {
    //     UMC_COMMENT_1 = uMC_COMMENT_1;
    // }

    // public String getUMC_COMMENT_2() {
    //     return UMC_COMMENT_2;
    // }

    // public void setUMC_COMMENT_2(String uMC_COMMENT_2) {
    //     UMC_COMMENT_2 = uMC_COMMENT_2;
    // }

    
}