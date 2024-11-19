using {TableService as view} from './table';
using {PCH} from '../db/model-pch';

//MM-036 購買見積登録

extend service TableService {

    entity PCH08_LIST as
            select from PCH.T06_QUOTATION_H as T01
            left join PCH.T07_QUOTATION_D as T02
                on(
                     T01.QUO_NUMBER = T02.QUO_NUMBER

                ) 
            inner join (select max(ID) AS ID, QUO_NUMBER, QUO_ITEM from PCH.T07_QUOTATION_D as T03
              where DEL_FLAG = 'N'  group by T03.QUO_NUMBER, QUO_ITEM ) AS T04 on T02.ID = T04.ID

            distinct {
                key T02.QUO_NUMBER,
                key T02.QUO_ITEM,
                    T02.NO,
                    T02.REFRENCE_NO,
                    T01.CUSTOMER,
                    T01.MACHINE_TYPE,
                    T01.QUANTITY,
                    T01.VALIDATE_START,
                    T01.VALIDATE_END,
                    T01.Item,
                    T01.TIME,
                    T01.LOCATION,
                    T02.MATERIAL_NUMBER,
                    T02.CUST_MATERIAL,
                    T02.MANUFACT_MATERIAL,
                    T02.Attachment,
                    T02.Material,
                    T02.MAKER,
                    T02.UWEB_USER,
                    T02.BP_NUMBER,
                    T02.PERSON_NO1,
                    T02.PERSON_NO2,
                    T02.PERSON_NO3,
                    T02.PERSON_NO4,
                    T02.PERSON_NO5,
                    T02.YLP,
                    T02.MANUL,
                    T02.MANUFACT_CODE,
                    T02.CUSTOMER_MMODEL,
                    T02.MID_QF,
                    T02.SMALL_QF,
                    T02.OTHER_QF,
                    T02.CURRENCY,
                    //T02.PRICE,
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
                    T02.UMC_COMMENT_1,
                    T02.UMC_COMMENT_2,
                    T02.STATUS,
                    T02.SUPPLIER_MAT
            }
            where T02.DEL_FLAG <> 'Y' and T02.DEL_FLAG <> 'y';
//                 action PCH07_CHECK_DATA(shelfJson : String) returns String;
//                 action PCH07_SAVE_DATA(shelfJson : String) returns String;

    action PCH08_SAVE_DATA(str : String) returns String;
    action PCH08_SHOW_DETAIL(param : String) returns String;
    action PCH08_EDIT_DETAIL(param : String) returns String;
};