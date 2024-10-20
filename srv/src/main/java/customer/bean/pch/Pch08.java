package customer.bean.pch;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Pch08 {
    @JSONField(name = "QUO_NUMBER")
    private String QUO_NUMBER;

    @JSONField(name = "QUO_ITEM")
    private Integer QUO_ITEM;

    @JSONField(name = "NO")
    private Integer NO;

    @JSONField(name = "REFRENCE_NO")
    private String REFRENCE_NO;

    @JSONField(name = "CUSTOMER")
    private String CUSTOMER;

    @JSONField(name = "MACHINE_TYPE")
    private String  MACHINE_TYPE;

    @JSONField(name = "QUANTITY")
    private Double QUANTITY;

    @JSONField(name = "VALIDATE_START")
    private LocalDate VALIDATE_START;

    @JSONField(name = "VALIDATE_END")
    private LocalDate VALIDATE_END;

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
    private Integer PERSON_NO1;

    @JSONField(name = "PERSON_NO2")
    private Integer PERSON_NO2;

    @JSONField(name = "PERSON_NO3")
    private Integer  PERSON_NO3;

    @JSONField(name = "PERSON_NO4")
    private Integer PERSON_NO4;

    @JSONField(name = "PERSON_NO5")
    private Integer   PERSON_NO5;

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
    private String  SMALL_QF;

    @JSONField(name = "OTHER_QF")
    private String OTHER_QF;

    @JSONField(name = "CURRENCY")
    private String CURRENCY;

    @JSONField(name = "PRICE")
    private Double PRICE;

    @JSONField(name = "PRICE_CONTROL")
    private String PRICE_CONTROL;

    @JSONField(name = "LEAD_TIME")
    private LocalDate LEAD_TIME;

    @JSONField(name = "MOQ")
    private String  MOQ;

    @JSONField(name = "UNIT")
    private String UNIT;

    @JSONField(name = "SPQ")
    private String  SPQ;

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
    private String  MEMO3;

    @JSONField(name = "SL")
    private String SL;

    @JSONField(name = "TZ")
    private String TZ;

    @JSONField(name = "RMATERIAL")
    private String RMATERIAL;

    @JSONField(name = "RMATERIAL_CURRENCY")
    private String RMATERIAL_CURRENCY;

    @JSONField(name = "RMATERIAL_PRICE")
    private Double RMATERIAL_PRICE;

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

    @JSONField(name = "STATUS")
    private String STATUS;

}
