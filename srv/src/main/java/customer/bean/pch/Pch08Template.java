package customer.bean.pch;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.alibaba.fastjson.annotation.JSONField;

public class Pch08Template {
    @JSONField(name = "QUO_NUMBER")
    private String QUO_NUMBER;

    @JSONField(name = "QUO_ITEM")
    private Integer QUO_ITEM;

    @JSONField(name = "SALES_NUMBER")
    private String SALES_NUMBER;

    @JSONField(name = "SALES_D_NO")
    private String SALES_D_NO;

    @JSONField(name = "NO")
    private Integer NO;

    @JSONField(name = "REFRENCE_NO")
    private String REFRENCE_NO;

    @JSONField(name = "QUANTITY")
    private String QUANTITY;

    @JSONField(name = "CUSTOMER")
    private String CUSTOMER;

    @JSONField(name = "MACHINE_TYPE")
    private String MACHINE_TYPE;

    @JSONField(name = "VALIDATE_START")
    private LocalDate VALIDATE_START;

    @JSONField(name = "VALIDATE_END")
    private LocalDate VALIDATE_END;

    @JSONField(name = "TIME")
    private String TIME;

    @JSONField(name = "ITEM")
    private String ITEM;

    @JSONField(name = "LOCATION")
    private String LOCATION;

    @JSONField(name = "MATERIAL_NUMBER")
    private String MATERIAL_NUMBER;

    @JSONField(name = "CUST_MATERIAL")
    private String CUST_MATERIAL;

    @JSONField(name = "MANUFACT_MATERIAL")
    private String MANUFACT_MATERIAL;

    @JSONField(name = "Attachment")
    private String Attachment;

    @JSONField(name = "Material")
    private String Material;

    @JSONField(name = "MAKER")
    private String MAKER;

    @JSONField(name = "UWEB_USER")
    private String UWEB_USER;

    @JSONField(name = "BP_NUMBER")
    private String BP_NUMBER;

    @JSONField(name = "PERSON_NO1")
    private BigDecimal PERSON_NO1;

    @JSONField(name = "YLP")
    private String YLP;

    @JSONField(name = "MANUL")
    private String MANUL;

    @JSONField(name = "MANUFACT_CODE")
    private String MANUFACT_CODE;

    @JSONField(name = "CUSTOMER_MMODEL")
    private String CUSTOMER_MMODEL;

    @JSONField(name = "MID_QF")
    private String MID_QF;

    @JSONField(name = "SMALL_QF")
    private String SMALL_QF;

    @JSONField(name = "OTHER_QF")
    private String OTHER_QF;

    @JSONField(name = "CURRENCY")
    private String CURRENCY;

    @JSONField(name = "QTY")
    private BigDecimal QTY;

    @JSONField(name = "PRICE")
    private BigDecimal PRICE;

    @JSONField(name = "PRICE_CONTROL")
    private String PRICE_CONTROL;

    @JSONField(name = "LEAD_TIME")
    private String LEAD_TIME;

    @JSONField(name = "MOQ")
    private String MOQ;

    @JSONField(name = "UNIT")
    private String UNIT;

    @JSONField(name = "SPQ")
    private String SPQ;

    @JSONField(name = "KBXT")
    private String KBXT;

    @JSONField(name = "PRODUCT_WEIGHT")
    private String PRODUCT_WEIGHT;

    @JSONField(name = "ORIGINAL_COU")
    private String ORIGINAL_COU;

    @JSONField(name = "EOL")
    private String EOL;

    @JSONField(name = "ISBOI")
    private Boolean ISBOI;

    @JSONField(name = "Incoterms")
    private String Incoterms;

    @JSONField(name = "Incoterms_Text")
    private String Incoterms_Text;

    @JSONField(name = "MEMO1")
    private String MEMO1;

    @JSONField(name = "MEMO2")
    private String MEMO2;

    @JSONField(name = "MEMO3")
    private String MEMO3;

    @JSONField(name = "SL")
    private String SL;

    @JSONField(name = "TZ")
    private String TZ;

    @JSONField(name = "RMATERIAL")
    private String RMATERIAL;

    @JSONField(name = "RMATERIAL_CURRENCY")
    private String RMATERIAL_CURRENCY;

    @JSONField(name = "RMATERIAL_PRICE")
    private BigDecimal RMATERIAL_PRICE;

    @JSONField(name = "RMATERIAL_LT")
    private String RMATERIAL_LT;

    @JSONField(name = "RMATERIAL_MOQ")
    private String RMATERIAL_MOQ;

    @JSONField(name = "RMATERIAL_KBXT")
    private String RMATERIAL_KBXT;

    @JSONField(name = "UMC_COMMENT_1")
    private String UMC_COMMENT_1;

    @JSONField(name = "UMC_COMMENT_2")
    private String UMC_COMMENT_2;

    public String getQUO_NUMBER() {
        return QUO_NUMBER;
    }

    public void setQUO_NUMBER(String qUO_NUMBER) {
        QUO_NUMBER = qUO_NUMBER;
    }

    public Integer getQUO_ITEM() {
        return QUO_ITEM;
    }

    public void setQUO_ITEM(Integer qUO_ITEM) {
        QUO_ITEM = qUO_ITEM;
    }

    public String getSALES_NUMBER() {
        return SALES_NUMBER;
    }

    public void setSALES_NUMBER(String sALES_NUMBER) {
        SALES_NUMBER = sALES_NUMBER;
    }

    public String getSALES_D_NO() {
        return SALES_D_NO;
    }

    public void setSALES_D_NO(String sALES_D_NO) {
        SALES_D_NO = sALES_D_NO;
    }

    public Integer getNO() {
        return NO;
    }

    public void setNO(Integer nO) {
        NO = nO;
    }

    public String getREFRENCE_NO() {
        return REFRENCE_NO;
    }

    public void setREFRENCE_NO(String rEFRENCE_NO) {
        REFRENCE_NO = rEFRENCE_NO;
    }

    public String getQUANTITY() {
        return QUANTITY;
    }

    public void setQUANTITY(String qUANTITY) {
        QUANTITY = qUANTITY;
    }

    public String getCUSTOMER() {
        return CUSTOMER;
    }

    public void setCUSTOMER(String cUSTOMER) {
        CUSTOMER = cUSTOMER;
    }

    public String getMACHINE_TYPE() {
        return MACHINE_TYPE;
    }

    public void setMACHINE_TYPE(String mACHINE_TYPE) {
        MACHINE_TYPE = mACHINE_TYPE;
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

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String tIME) {
        TIME = tIME;
    }

    public String getITEM() {
        return ITEM;
    }

    public void setITEM(String iTEM) {
        ITEM = iTEM;
    }

    public String getLOCATION() {
        return LOCATION;
    }

    public void setLOCATION(String lOCATION) {
        LOCATION = lOCATION;
    }

    public String getMATERIAL_NUMBER() {
        return MATERIAL_NUMBER;
    }

    public void setMATERIAL_NUMBER(String mATERIAL_NUMBER) {
        MATERIAL_NUMBER = mATERIAL_NUMBER;
    }

    public String getCUST_MATERIAL() {
        return CUST_MATERIAL;
    }

    public void setCUST_MATERIAL(String cUST_MATERIAL) {
        CUST_MATERIAL = cUST_MATERIAL;
    }

    public String getMANUFACT_MATERIAL() {
        return MANUFACT_MATERIAL;
    }

    public void setMANUFACT_MATERIAL(String mANUFACT_MATERIAL) {
        MANUFACT_MATERIAL = mANUFACT_MATERIAL;
    }

    public String getAttachment() {
        return Attachment;
    }

    public void setAttachment(String attachment) {
        Attachment = attachment;
    }

    public String getMaterial() {
        return Material;
    }

    public void setMaterial(String material) {
        Material = material;
    }

    public String getMAKER() {
        return MAKER;
    }

    public void setMAKER(String mAKER) {
        MAKER = mAKER;
    }

    public String getUWEB_USER() {
        return UWEB_USER;
    }

    public void setUWEB_USER(String uWEB_USER) {
        UWEB_USER = uWEB_USER;
    }

  

    public String getYLP() {
        return YLP;
    }

    public void setYLP(String yLP) {
        YLP = yLP;
    }

    public String getMANUL() {
        return MANUL;
    }

    public void setMANUL(String mANUL) {
        MANUL = mANUL;
    }

    public String getMANUFACT_CODE() {
        return MANUFACT_CODE;
    }

    public void setMANUFACT_CODE(String mANUFACT_CODE) {
        MANUFACT_CODE = mANUFACT_CODE;
    }

    public String getCUSTOMER_MMODEL() {
        return CUSTOMER_MMODEL;
    }

    public void setCUSTOMER_MMODEL(String cUSTOMER_MMODEL) {
        CUSTOMER_MMODEL = cUSTOMER_MMODEL;
    }

    public String getMID_QF() {
        return MID_QF;
    }

    public void setMID_QF(String mID_QF) {
        MID_QF = mID_QF;
    }

    public String getSMALL_QF() {
        return SMALL_QF;
    }

    public void setSMALL_QF(String sMALL_QF) {
        SMALL_QF = sMALL_QF;
    }

    public String getOTHER_QF() {
        return OTHER_QF;
    }

    public void setOTHER_QF(String oTHER_QF) {
        OTHER_QF = oTHER_QF;
    }

    public String getCURRENCY() {
        return CURRENCY;
    }

    public void setCURRENCY(String cURRENCY) {
        CURRENCY = cURRENCY;
    }

    public BigDecimal getQTY() {
        return QTY;
    }

    public void setQTY(BigDecimal qTY) {
        QTY = qTY;
    }

    public BigDecimal getPRICE() {
        return PRICE;
    }

    public void setPRICE(BigDecimal pRICE) {
        PRICE = pRICE;
    }

    public String getPRICE_CONTROL() {
        return PRICE_CONTROL;
    }

    public void setPRICE_CONTROL(String pRICE_CONTROL) {
        PRICE_CONTROL = pRICE_CONTROL;
    }

    public String getLEAD_TIME() {
        return LEAD_TIME;
    }

    public void setLEAD_TIME(String lEAD_TIME) {
        LEAD_TIME = lEAD_TIME;
    }

    public String getMOQ() {
        return MOQ;
    }

    public void setMOQ(String mOQ) {
        MOQ = mOQ;
    }

    public String getUNIT() {
        return UNIT;
    }

    public void setUNIT(String uNIT) {
        UNIT = uNIT;
    }

    public String getSPQ() {
        return SPQ;
    }

    public void setSPQ(String sPQ) {
        SPQ = sPQ;
    }

    public String getKBXT() {
        return KBXT;
    }

    public void setKBXT(String kBXT) {
        KBXT = kBXT;
    }

    public String getPRODUCT_WEIGHT() {
        return PRODUCT_WEIGHT;
    }

    public void setPRODUCT_WEIGHT(String pRODUCT_WEIGHT) {
        PRODUCT_WEIGHT = pRODUCT_WEIGHT;
    }

    public String getORIGINAL_COU() {
        return ORIGINAL_COU;
    }

    public void setORIGINAL_COU(String oRIGINAL_COU) {
        ORIGINAL_COU = oRIGINAL_COU;
    }

    public String getEOL() {
        return EOL;
    }

    public void setEOL(String eOL) {
        EOL = eOL;
    }

    public Boolean getISBOI() {
        return ISBOI;
    }

    public void setISBOI(Boolean iSBOI) {
        ISBOI = iSBOI;
    }

    public String getIncoterms() {
        return Incoterms;
    }

    public void setIncoterms(String incoterms) {
        Incoterms = incoterms;
    }

    public String getIncoterms_Text() {
        return Incoterms_Text;
    }

    public void setIncoterms_Text(String incoterms_Text) {
        Incoterms_Text = incoterms_Text;
    }

    public String getMEMO1() {
        return MEMO1;
    }

    public void setMEMO1(String mEMO1) {
        MEMO1 = mEMO1;
    }

    public String getMEMO2() {
        return MEMO2;
    }

    public void setMEMO2(String mEMO2) {
        MEMO2 = mEMO2;
    }

    public String getMEMO3() {
        return MEMO3;
    }

    public void setMEMO3(String mEMO3) {
        MEMO3 = mEMO3;
    }

    public String getSL() {
        return SL;
    }

    public void setSL(String sL) {
        SL = sL;
    }

    public String getTZ() {
        return TZ;
    }

    public void setTZ(String tZ) {
        TZ = tZ;
    }

    public String getRMATERIAL() {
        return RMATERIAL;
    }

    public void setRMATERIAL(String rMATERIAL) {
        RMATERIAL = rMATERIAL;
    }

    public String getRMATERIAL_CURRENCY() {
        return RMATERIAL_CURRENCY;
    }

    public void setRMATERIAL_CURRENCY(String rMATERIAL_CURRENCY) {
        RMATERIAL_CURRENCY = rMATERIAL_CURRENCY;
    }

    public BigDecimal getRMATERIAL_PRICE() {
        return RMATERIAL_PRICE;
    }

    public void setRMATERIAL_PRICE(BigDecimal rMATERIAL_PRICE) {
        RMATERIAL_PRICE = rMATERIAL_PRICE;
    }

    public String getRMATERIAL_LT() {
        return RMATERIAL_LT;
    }

    public void setRMATERIAL_LT(String rMATERIAL_LT) {
        RMATERIAL_LT = rMATERIAL_LT;
    }

    public String getRMATERIAL_MOQ() {
        return RMATERIAL_MOQ;
    }

    public void setRMATERIAL_MOQ(String rMATERIAL_MOQ) {
        RMATERIAL_MOQ = rMATERIAL_MOQ;
    }

    public String getRMATERIAL_KBXT() {
        return RMATERIAL_KBXT;
    }

    public void setRMATERIAL_KBXT(String rMATERIAL_KBXT) {
        RMATERIAL_KBXT = rMATERIAL_KBXT;
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

    public BigDecimal getPERSON_NO1() {
        return PERSON_NO1;
    }

    public void setPERSON_NO1(BigDecimal pERSON_NO1) {
        PERSON_NO1 = pERSON_NO1;
    }

    public String getBP_NUMBER() {
        return BP_NUMBER;
    }

    public void setBP_NUMBER(String BP_NUMBER) {
        this.BP_NUMBER = BP_NUMBER;
    }



}
