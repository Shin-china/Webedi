using {TableService as view} from './table';
using {PCH} from '../db/model-pch';

//MM-012 FCレポート

extend service TableService {


    entity PCH10_Header       as
        select from PCH.T06_QUOTATION_H as T01
        left join PCH.T07_QUOTATION_D as T02
            on(
                T01.QUO_NUMBER = T02.QUO_NUMBER
            )

        distinct {

            key T01.ID,
                T01.QUO_NUMBER,
                T01.QUO_VERSION,
                T01.SALES_NUMBER,
                T01.CUSTOMER,
                T01.MACHINE_TYPE,
                T01.Item,
                T01.QUANTITY,
                T01.TIME,
                T01.LOCATION,
                T01.VALIDATE_START,
                T01.VALIDATE_END,
                T01.STATUS,
                // T01.TOTAL_JPY,
                // T01.TOTAL_USD,
                // T01.TOTAL_CNY,
                // T01.TOTAL_HKD,
                // T01.TOTAL_THB,
                T01.CD_DATE,
                T01.CD_BY,
                T02.PLANT_ID,
                T02.SUPPLIER_MAT,
                T02.INITIAL_OBJ,
                T01.CD_DATE_TIME,

        }


    entity PCH10_HISTORY_LIST as
        select from PCH.T07_QUOTATION_D as T01
        left join PCH.T06_QUOTATION_H as T02
            on(
                T01.QUO_NUMBER = T02.QUO_NUMBER
            )

        distinct {
            key T01.ID,
                T01.QUO_NUMBER,
                T01.QUO_ITEM,
                T01.SALES_NUMBER,
                T01.QUO_VERSION,
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
                T01.PLANT_ID,
                T01.SUPPLIER_MAT,
                T01.CD_BY,
                T01.CD_DATE_TIME,
                T01.CD_DATE,
        }

    entity PCH10_LIST         as
        select from PCH.T06_QUOTATION_H as T01
        left join PCH.T07_QUOTATION_D as T02
            on(
                T01.QUO_NUMBER = T02.QUO_NUMBER

            )

        inner join (
            select
                max(ID) as ID,
                QUO_NUMBER,
                QUO_ITEM
            from PCH.T07_QUOTATION_D as T03
            where
                DEL_FLAG = 'N'
            group by
                T03.QUO_NUMBER,
                QUO_ITEM
        ) as T04
            on T02.ID = T04.ID


        {
            key T02.QUO_NUMBER,
                T02.QUO_ITEM,
                T02.ID as T02_ID,
                T02.SALES_NUMBER,
                T02.SALES_D_NO,
                T02.QUO_VERSION,
                T02.NO,
                T02.REFRENCE_NO,
                T02.MATERIAL_NUMBER,
                T02.CUST_MATERIAL,
                T02.MANUFACT_MATERIAL,
                T02.Attachment,
                T02.Material,
                T02.MAKER,
                T02.UWEB_USER,
                T02.BP_NUMBER,
                T02.PERSON_NO1,
                //                    T02.PERSON_NO2,
                //                    T02.PERSON_NO3,
                //                    T02.PERSON_NO4,
                //                    T02.PERSON_NO5,
                T02.YLP,
                T02.MANUL,
                T02.MANUFACT_CODE,
                T02.CUSTOMER_MMODEL,
                T02.MID_QF,
                T02.SMALL_QF,
                T02.OTHER_QF,
                T02.QTY,
                T02.CURRENCY,
                T02.PRICE,
                T02.PRICE_CONTROL,
                T02.LEAD_TIME,
                T02.MOQ,
                T02.UNIT,
                T02.SPQ,
                T02.KBXT,
                T02.PRODUCT_WEIGHT,
                T02.ORIGINAL_COU,
                T02.EOL,
                T02.ISBOI,
                T02.Incoterms,
                T02.Incoterms_Text,
                T02.MEMO1,
                T02.MEMO2,
                T02.MEMO3,
                T02.SL,
                T02.TZ,
                T02.RMATERIAL,
                T02.RMATERIAL_CURRENCY,
                T02.RMATERIAL_PRICE,
                T02.RMATERIAL_LT,
                T02.RMATERIAL_MOQ,
                T02.RMATERIAL_KBXT,
                T02.UMC_SELECTION,
                T02.UMC_COMMENT_1,
                T02.UMC_COMMENT_2,
                T02.STATUS,
                T02.INITIAL_OBJ,
                T02.PLANT_ID,
                T02.SUPPLIER_MAT,
                T02.CD_BY
        }
        where
                T01.DEL_FLAG <> 'Y'
            and T01.DEL_FLAG <> 'y';

    //                 action PCH07_CHECK_DATA(shelfJson : String) returns String;
    //                 action PCH07_SAVE_DATA(shelfJson : String) returns String;
    action PCH10_GR_SEND(params : String)   returns String;
    action pch06BatchSending(json : String) returns String; //IFM055 購買見積結果送信
    action PCH10_SAVE_DATA(json : String)   returns String; //IFM055 購買見積保存
    action PCH10_L_SAVE_DATA(str : String)  returns String; // list 画面保存按钮
    action PCH10_GMTQ(str : String)         returns String; //购买同期
    action PCH10_BPTQ(str : String)         returns String; //BP同期
    action PCH10_L_SENDSTATUS(str : String) returns String; //list 发送状态变更
    action PCH10_D_SENDSTATUS(str : String) returns String; //list 发送按钮


}

annotate TableService.PCH10_Header with {

    STATUS @(Common: {ValueList: {entity: 'PCH10_STATUS_POP', }});

};
