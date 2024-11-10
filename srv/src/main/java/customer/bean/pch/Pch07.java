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
    private String CUST_MATERIAL; // 図面品番

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

    
    
}