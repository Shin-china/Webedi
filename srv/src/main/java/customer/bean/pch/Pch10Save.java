package customer.bean.pch;

import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONField;

public class Pch10Save {

    @JSONField(name = "ID")
    private String ID;

    @JSONField(name = "COPYBY")
    private Integer COPYBY;

    @JSONField(name = "T02_ID")
    private String T02_ID;
    @JSONField(name = "QUO_NUMBER")
    private String QUO_NUMBER;
    @JSONField(name = "QUO_ITEM")
    private Integer QUO_ITEM;
    @JSONField(name = "SALES_NUMBER")
    private String SALES_NUMBER;
    @JSONField(name = "SALES_D_NO")
    private String SALES_D_NO;
    @JSONField(name = "QUO_VERSION")
    private String QUO_VERSION;
    @JSONField(name = "NO")
    private Integer NO;
    @JSONField(name = "REFRENCE_NO")
    private String REFRENCE_NO;
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
    private Integer BP_NUMBER;
    @JSONField(name = "PERSON_NO1")
    private Integer PERSON_NO1;
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
    @JSONField(name = "QTY")
    private BigDecimal QTY;
    @JSONField(name = "CURRENCY")
    private String CURRENCY;
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
    @JSONField(name = "UMC_SELECTION")
    private String UMC_SELECTION;
    @JSONField(name = "UMC_COMMENT_1")
    private String UMC_COMMENT_1;
    @JSONField(name = "UMC_COMMENT_2")
    private String UMC_COMMENT_2;
    @JSONField(name = "STATUS")
    private String STATUS;
    @JSONField(name = "INITIAL_OBJ")
    private String INITIAL_OBJ;
    @JSONField(name = "PLANT_ID")
    private String PLANT_ID;
    @JSONField(name = "SUPPLIER_MAT")
    private String SUPPLIER_MAT;
    @JSONField(name = "CD_BY")
    private String CD_BY;

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

    public String getQUO_VERSION() {
        return QUO_VERSION;
    }

    public void setQUO_VERSION(String qUO_VERSION) {
        QUO_VERSION = qUO_VERSION;
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

    public Integer getBP_NUMBER() {
        return BP_NUMBER;
    }

    public void setBP_NUMBER(Integer bP_NUMBER) {
        BP_NUMBER = bP_NUMBER;
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

    public BigDecimal getQTY() {
        return QTY;
    }

    public void setQTY(BigDecimal qTY) {
        QTY = qTY;
    }

    public String getCURRENCY() {
        return CURRENCY;
    }

    public void setCURRENCY(String cURRENCY) {
        CURRENCY = cURRENCY;
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

    public String getUMC_SELECTION() {
        return UMC_SELECTION;
    }

    public void setUMC_SELECTION(String uMC_SELECTION) {
        UMC_SELECTION = uMC_SELECTION;
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

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String sTATUS) {
        STATUS = sTATUS;
    }

    public String getINITIAL_OBJ() {
        return INITIAL_OBJ;
    }

    public void setINITIAL_OBJ(String iNITIAL_OBJ) {
        INITIAL_OBJ = iNITIAL_OBJ;
    }

    public String getPLANT_ID() {
        return PLANT_ID;
    }

    public void setPLANT_ID(String pLANT_ID) {
        PLANT_ID = pLANT_ID;
    }

    public String getSUPPLIER_MAT() {
        return SUPPLIER_MAT;
    }

    public void setSUPPLIER_MAT(String sUPPLIER_MAT) {
        SUPPLIER_MAT = sUPPLIER_MAT;
    }

    public String getCD_BY() {
        return CD_BY;
    }

    public void setCD_BY(String cD_BY) {
        CD_BY = cD_BY;
    }

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public String getT02_ID() {
        return T02_ID;
    }

    public void setT02_ID(String t02_ID) {
        T02_ID = t02_ID;
    }

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

    public Integer getPERSON_NO1() {
        return PERSON_NO1;
    }

    public void setPERSON_NO1(Integer pERSON_NO1) {
        PERSON_NO1 = pERSON_NO1;
    }

    public Integer getCOPYBY() {
        return COPYBY;
    }

    public void setCOPYBY(Integer cOPYBY) {
        COPYBY = cOPYBY;
    }
}
