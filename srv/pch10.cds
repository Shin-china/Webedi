using {TableService as view} from './table';
using {PCH} from '../db/model-pch';

//MM-012 FCレポート

extend service TableService {


    entity PCH10_Header       as
        select from PCH.T06_QUOTATION_H as T01 distinct {

            key T01.ID,
                T01.QUO_NUMBER,
                T01.CUSTOMER,
                T01.MACHINE_TYPE,
                T01.Item,
                T01.QUANTITY,
                T01.TIME,
                T01.LOCATION,
                T01.VALIDATE_START,
                T01.VALIDATE_END,
                // T01.PLANT_ID,
                T01.SALES_NUMBER,
                T01.STATUS,
                // T01.TOTAL_JPY,
                // T01.TOTAL_USD,
                // T01.TOTAL_CNY,
                // T01.TOTAL_HKD,
                // T01.TOTAL_THB,
                T01.CD_DATE,
                T01.CD_DATE_TIME

        }


    entity PCH10_HISTORY_LIST as
        select from PCH.T07_QUOTATION_D as T01
        left join PCH.T06_QUOTATION_H as T02
            on(
                T01.QUO_NUMBER = T02.QUO_NUMBER
            )

        distinct {

            key T01.SALES_NUMBER,
            key T01.QUO_VERSION,
                T02.CUSTOMER,
                T02.MACHINE_TYPE,
                T02.Item,
                T02.QUANTITY,
                T02.TIME,
                T02.LOCATION,
                T02.VALIDATE_START,
                T02.VALIDATE_END,
                T01.SALES_D_NO,
                T01.SAP_MAT_ID,
                T01.QUO_NUMBER,
                T01.QUO_ITEM,
                T01.NO,
                T01.REFRENCE_NO,
                T01.MATERIAL_NUMBER,
                T01.CUST_MATERIAL,
                T01.MANUFACT_MATERIAL,
                T01.Attachment,
                T01.Material,
                T01.MAKER,
                T01.UWEB_USER,
                T01.BP_NUMBER,
                T01.PERSON_NO1,
                T01.YLP,
                T01.MANUL,
                T01.MANUFACT_CODE,
                T01.CUSTOMER_MMODEL,
                T01.MID_QF,
                T01.SMALL_QF,
                T01.OTHER_QF,
                T01.QTY,
                T01.CURRENCY,
                T01.PRICE,
                T01.PRICE_CONTROL,
                T01.LEAD_TIME,
                T01.MOQ,
                T01.UNIT,
                T01.SPQ,
                T01.KBXT,
                T01.PRODUCT_WEIGHT,
                T01.ORIGINAL_COU,
                T01.EOL,
                T01.ISBOI,
                T01.Incoterms,
                T01.Incoterms_Text,
                T01.MEMO1,
                T01.MEMO2,
                T01.MEMO3,
                T01.SL,
                T01.TZ,
                T01.RMATERIAL,
                T01.RMATERIAL_CURRENCY,
                T01.RMATERIAL_PRICE,
                T01.RMATERIAL_LT,
                T01.RMATERIAL_MOQ,
                T01.RMATERIAL_KBXT,
                T01.UMC_SELECTION,
                T01.UMC_COMMENT_1,
                T01.UMC_COMMENT_2,
                T01.FINAL_CHOICE,
                T01.STATUS,
                T01.INITIAL_OBJ,
                T01.PLANT_ID
        }

    action PCH10_GR_SEND(params : String) returns String;
    action pch06BatchSending(json : String)               returns String; //IFM055 購買見積結果送信
    
};
