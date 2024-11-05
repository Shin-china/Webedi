using {TableService as view} from './table';

extend service TableService {
    entity PCH_T03_PO_ITEM_PRINT as
        select from view.T01_PO_H as T01
        left join view.T02_PO_D as T02
            on(
                T01.PO_NO = T02.PO_NO
            )

        left join view.MST_T05_SAP_BP_PURCHASE T04
            on T01.SUPPLIER = T04.SUPPLIER
        left join view.MST_T03_SAP_BP T05
            on T05.BP_ID = T01.SUPPLIER
        left join view.MST_T06_MAT_PLANT T06
            on T06.PLANT_ID = T02.PLANT_ID
            and T06.MAT_ID = T02.MAT_ID
                left join view.MST_T01_SAP_MAT T07
            on T07.MAT_ID = T02.MAT_ID
  
        distinct {
            key T01.PO_NO || T02.D_NO as ID      : String(100),
            key T01.PO_NO, // 発注番号
            key T02.D_NO, // 明細番号

                T01.PO_NO || T02.D_NO as PODNO   : String(100), //注文番号明細
                left(
                    T02.STORAGE_LOC, 4
                ) || T02.STORAGE_TXT  as STORAGE : String(100), //納品場所4桁Code＋テキスト
                T01.SUPPLIER, // 仕入先コード
                T02.SUPPLIER_MAT, // 仕入先品目コード
                T02.MAT_ID, // 品目コード
                T02.PO_TYPE, // 発注区分  C：新規 U：変更 D：削除
                T01.PO_DATE, //発注日
                T02.STATUS, // ステータス  01：送信済　02：照会済
                T02.CD_BY, //登録者
                T02.PO_D_DATE, //所要日付
                   case T04.ZABC
                    when 'E' then 'E'
                    when 'F' then 'F'
                    when 'W' then 'W'
                    else 'C' end as ZABC : String(5), //ABC区分 E：Email F：Fax  W：Web edi
                T02.PO_D_TXZ01, // 品目テキスト
                T02.PO_PUR_QTY, // 発注数量


                T02.PO_PUR_UNIT, // 単位
                T02.CURRENCY, // 通貨
                T02.DEL_PRICE, // 発注単価（値）
                T02.UNIT_PRICE, // 価格単位
                T02.DEL_AMOUNT, // 発注金額（値）
                T02.CUSTOMER_MAT, // 顾客品番
                T02.MEMO, // 備考
                T05.FAX,
                T05.BP_TYPE,
                T05.BP_NAME1,
                T05.BP_NAME2,
                T05.BP_NAME3,
                T05.BP_NAME4,
                T05.TEL,
                T05.LOG_NO,
                T05.POSTCODE,
                T05.REGIONS,
                T05.PLACE_NAME,
                T06.IMP_COMP,//検査合区分
                T07.MANU_CODE,

                '' as checkOk : String, // 検査合区分
                '' as order_unit_price : String, // 発注単価
                '' as exclusive_tax_amount : String, // 税抜額
                '' as tax_amount : String, // 税額
                '' as inclusive_tax_amount : String, // 税込額
                '' as QR_CODE : String,

                '' as cop1 : String,
                '' as cop2 : String,
                '' as cop3 : String,
                '' as cop4 : String,
                '' as cop5 : String,
                '' as cop6 : String,
                '' as cop7 : String,
                '' as cop8 : String,
                '' as cop9 : String,
                '' as cop10 : String,
                '' as cop11 : String,
                '' as cop12 : String,
                '' as cop13 : String,
                '' as cop14 : String,
                '' as cop15 : String,
                '' as cop16 : String,
                '' as cop17 : String,
                '' as cop18 : String,
                '' as cop19 : String,
                '' as cop20 : String,
                '' as cop21 : String,

                '' as ZWS_1 : String,
                '' as ZWS_2 : String,
                '' as ZWS_3 : String,
                '' as ZWS_4 : String,
                '' as ZWS_5 : String,
                '' as ZWS_6 : String,
                '' as ZWS_7 : String,
                '' as ZWS_8 : String,
                '' as ZWS_9 : String,

                '' as DATE1 : String,
                '' as DATE2 : String,
                '' as DATE3 : String,

                '' as BP_NAME12 : String,
                '' as POSTCODE2 : String,
                '' as REGIONS2 : String,
                '' as TEL2 : String,
                '' as FAX2 : String,
                '' as TYPE : String,

                
                
        }
   entity PCH_T03_PO_ITEM_ZWS_ALL_PRINT as
        select from view.T01_PO_H as T01
        left join view.T02_PO_D as T02
            on(
                T01.PO_NO = T02.PO_NO
            )

        left join view.MST_T05_SAP_BP_PURCHASE T04
            on T01.SUPPLIER = T04.SUPPLIER
        left join view.MST_T03_SAP_BP T05
            on T05.BP_ID = T01.SUPPLIER
        left join view.MST_T06_MAT_PLANT T06
            on T06.PLANT_ID = T02.PLANT_ID
            and T06.MAT_ID = T02.MAT_ID
                left join view.MST_T01_SAP_MAT T07
            on T07.MAT_ID = T02.MAT_ID
  
        distinct {
            key T01.PO_NO || T02.D_NO as ID      : String(100),
            key T01.PO_NO, // 発注番号
            key T02.D_NO, // 明細番号

                T01.PO_NO || T02.D_NO as PODNO   : String(100), //注文番号明細
                left(
                    T02.STORAGE_LOC, 4
                ) || T02.STORAGE_TXT  as STORAGE : String(100), //納品場所4桁Code＋テキスト
                T01.SUPPLIER, // 仕入先コード
                T02.SUPPLIER_MAT, // 仕入先品目コード
                T02.MAT_ID, // 品目コード
                T02.PO_TYPE, // 発注区分  C：新規 U：変更 D：削除
                T01.PO_DATE, //発注日
                T02.STATUS, // ステータス  01：送信済　02：照会済
                T02.CD_BY, //登録者
                T02.PO_D_DATE, //所要日付
                  case T04.ZABC
                    when 'E' then 'E'
                    when 'F' then 'F'
                    when 'W' then 'W'
                    else 'C' end as ZABC : String(5), //ABC区分 E：Email F：Fax  W：Web edi
                T02.PO_D_TXZ01, // 品目テキスト
                T02.PO_PUR_QTY, // 発注数量


                T02.PO_PUR_UNIT, // 単位
                T02.CURRENCY, // 通貨
                T02.DEL_PRICE, // 発注単価（値）
                T02.UNIT_PRICE, // 価格単位
                T02.DEL_AMOUNT, // 発注金額（値）
                T02.CUSTOMER_MAT, // 顾客品番
                T02.MEMO, // 備考
                T05.FAX,
                T05.BP_TYPE,
                T05.BP_NAME1,
                T05.BP_NAME2,
                T05.BP_NAME3,
                T05.BP_NAME4,
                T05.TEL,
                T05.LOG_NO,
                T05.POSTCODE,
                T05.REGIONS,
                T05.PLACE_NAME,
                T06.IMP_COMP,//検査合区分
                T07.MANU_CODE,

                '' as checkOk : String, // 検査合区分
                '' as order_unit_price : String, // 発注単価
                '' as exclusive_tax_amount : String, // 税抜額
                '' as tax_amount : String, // 税額
                '' as inclusive_tax_amount : String, // 税込額
                '' as QR_CODE : String,

                '' as cop1 : String,
                '' as cop2 : String,
                '' as cop3 : String,
                '' as cop4 : String,
                '' as cop5 : String,
                '' as cop6 : String,
                '' as cop7 : String,
                '' as cop8 : String,
                '' as cop9 : String,
                '' as cop10 : String,
                '' as cop11 : String,
                '' as cop12 : String,
                '' as cop13 : String,
                '' as cop14 : String,
                '' as cop15 : String,
                '' as cop16 : String,
                '' as cop17 : String,
                '' as cop18 : String,
                '' as cop19 : String,
                '' as cop20 : String,
                '' as cop21 : String,

                '' as ZWS_1 : String,
                '' as ZWS_2 : String,
                '' as ZWS_3 : String,
                '' as ZWS_4 : String,
                '' as ZWS_5 : String,
                '' as ZWS_6 : String,
                '' as ZWS_7 : String,
                '' as ZWS_8 : String,
                '' as ZWS_9 : String,

                '' as DATE1 : String,
                '' as DATE2 : String,
                '' as DATE3 : String,

                '' as BP_NAME12 : String,
                '' as POSTCODE2 : String,
                '' as REGIONS2 : String,
                '' as TEL2 : String,
                '' as FAX2 : String,
                

                
                
        }

    entity PCH_T03_PO_ITEM       as
        select from view.T01_PO_H as T01
        join view.T02_PO_D as T02
            on(
                T01.PO_NO = T02.PO_NO
            )
        left join view.MST_T05_SAP_BP_PURCHASE T04
            on T01.SUPPLIER = T04.SUPPLIER


         join view.SYS_T01_USER as Tu
            on Tu.USER_ID = (select user from view.USER_CODE   ) 
            and Tu.BP_NUMBER = T01.SUPPLIER

          {
            key T01.PO_NO || T02.D_NO as ID   : String(100),
            key T01.PO_NO, // 発注番号
            key T02.D_NO, // 明細番号
                T01.SUPPLIER, // 仕入先コード
                T02.SUPPLIER_MAT, // 仕入先品目コード
                T02.MAT_ID, // 品目コード
                T02.PO_TYPE, // 発注区分  C：新規 U：変更 D：削除
                T01.PO_DATE, //発注日
                T02.STATUS, // ステータス  01：送信済　02：照会済
                T02.CD_BY, //登録者
                T02.PO_D_DATE, //所要日付
                     case T04.ZABC
                    when 'E' then 'E'
                    when 'F' then 'F'
                    when 'W' then 'W'
                    else 'C' end as ZABC : String(5), //ABC区分 E：Email F：Fax  W：Web edi
                T02.PO_D_TXZ01, // 品目テキスト
                T02.PO_PUR_QTY, // 発注数量
                T02.PO_PUR_UNIT, // 単位
                T02.CURRENCY, // 通貨
                T02.DEL_PRICE, // 発注単価（値）
                T02.UNIT_PRICE, // 価格単位
                T02.DEL_AMOUNT, // 発注金額（値）
                T02.MEMO, // 備考
                Tu.USER_TYPE, // 用户type
        }
    where
                Tu.USER_TYPE = '2'

        union
            select from view.T01_PO_H as T01
            join view.T02_PO_D as T02
                on(
                    T01.PO_NO = T02.PO_NO
                )
            left join view.MST_T05_SAP_BP_PURCHASE T04
                on T01.SUPPLIER = T04.SUPPLIER

            join view.SYS_T01_USER as Tu
                on Tu.USER_ID = (select user from view.USER_CODE   ) 

                
            join view.SYS_T09_USER_2_PLANT t09
                on  t09.PLANT_ID = T02.PLANT_ID
                and t09.USER_ID  = Tu.USER_ID


            distinct {
                key T01.PO_NO || T02.D_NO as ID : String(100),
                key T01.PO_NO, // 発注番号
                key T02.D_NO, // 明細番号
                    T01.SUPPLIER, // 仕入先コード
                    T02.SUPPLIER_MAT, // 仕入先品目コード
                    T02.MAT_ID, // 品目コード
                    T02.PO_TYPE, // 発注区分  C：新規 U：変更 D：削除
                    T01.PO_DATE, //発注日
                    T02.STATUS, // ステータス  01：送信済　02：照会済
                    T02.CD_BY, //登録者
                    T02.PO_D_DATE, //所要日付
                       case T04.ZABC
                    when 'E' then 'E'
                    when 'F' then 'F'
                    when 'W' then 'W'
                    else 'C' end as ZABC : String(5), //ABC区分 E：Email F：Fax  W：Web edi
                    T02.PO_D_TXZ01, // 品目テキスト
                    T02.PO_PUR_QTY, // 発注数量
                    T02.PO_PUR_UNIT, // 単位
                    T02.CURRENCY, // 通貨
                    T02.DEL_PRICE, // 発注単価（値）
                    T02.UNIT_PRICE, // 価格単位
                    T02.DEL_AMOUNT, // 発注金額（値）
                    T02.MEMO, // 備考
                     Tu.USER_TYPE, // 用户type

            }
            where
                Tu.USER_TYPE = '1';


    action PCH03_SENDEMAIL(parms : String) returns String;
    action PCH03_QUEREN(parms : String) returns String;
    

}

annotate TableService.PCH_T03_PO_ITEM with @(Capabilities: {FilterRestrictions: {NonFilterableProperties: [ID]}});

annotate TableService.PCH_T03_PO_ITEM with {

    PO_TYPE @(Common: {ValueList: {entity: 'PO_TYPE_POP', }});
    STATUS  @(Common: {ValueList: {entity: 'PCH03_STATUS_POP', }});
    ZABC    @(Common: {ValueList: {entity: 'MST_BP_ZABC_POP', }});
};