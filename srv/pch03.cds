using {TableService as view} from './table';

extend service TableService {
    entity PCH_T03_PO_ITEM_PRINT as
        select from view.T01_PO_H as T01
         join view.T02_PO_D as T02
            on(
                T01.PO_NO = T02.PO_NO
            )

        left join view.MST_T05_SAP_BP_PURCHASE T04
            on T01.SUPPLIER = T04.SUPPLIER
            and T01.PO_ORG = T04.PURCHASE_ORG
        left join view.MST_T03_SAP_BP T05
            on T05.BP_ID = T01.SUPPLIER
        left join view.MST_T06_MAT_PLANT T06
            on T06.PLANT_ID = T02.PLANT_ID
            and T06.MAT_ID = T02.MAT_ID
        left join view.MST_T01_SAP_MAT T07
            on T07.MAT_ID = T02.MAT_ID
  
        distinct {
            key COALESCE(T01.PO_NO,'') || RIGHT('00000' || T02.D_NO, 5)  as ID      : String(100),
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
                T02.TAX_AMOUNT,//税額


                T02.PO_PUR_UNIT, // 単位
                T02.CURRENCY, // 通貨
                T02.DEL_PRICE, // 発注単価（値）
                T02.UNIT_PRICE, // 価格単位
                T02.DEL_AMOUNT, // 発注金額（値）
                T02.CUSTOMER_MAT, // 顾客品番
                T02.MEMO, // 備考
                T02.MEMO as MEMO1, // 備考
                T02.MEMO as MEMO2, // 備考
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
                T02.SAP_CD_BY, // SAP担当者
                T02.TO_MAT.TO_SAP_BP.BP_NAME1 as MANU_MATERIAL,
                T01.POCDBY,//自设参照者
                T01.SAP_CD_BY_TEXT , // 発注担当者
                T01.approvedate, //承認日
                T01.CD_DATE, //承認日

                '' as SAP_CD_BY2 : String, // SAP担当者2
                '' as T03_ADDR : String, // SAP担当者2
                '' as checkOk : String, // 検査合区分
                '' as order_unit_price : String, // 発注単価
                '' as exclusive_tax_amount : String, // 税抜額
                '' as tax_amount_view : String, // 税額
                '' as inclusive_tax_amount : String, // 税込額
                '' as QR_CODE : String,

                '' as jcs1 : String,
                '' as jcs2 : String,
                '' as jcs3 : String,
                '' as jcs4 : String,
                '' as jcs5 : String,
                '' as jcs6 : String,
                '' as jcs7 : String,
                '' as jcs8 : String,
                '' as jcs9 : String,
                '' as jcs10 : String,
                '' as jcs11 : String,
                '' as jcs12 : String,
                '' as jcs13 : String,
                '' as jcs14 : String,
                '' as jcs15 : String,
                '' as jcs16 : String,
                '' as jcs17 : String,
                '' as jcs18 : String,
                '' as jcs19 : String,
                '' as jcs20 : String,
                '' as jcs21 : String,

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
                '' as cop22 : String,// 海外発注番号：
                '' as cop23 : String,// 納入日：
                '' as cop24 : String,// podno
                '' as cop25 : String,// 海外発注番号：
                '' as cop26 : String,// qrcode
                '' as cop27 : String,// 海外発注番号：
                '' as cop28 : String,// 海外発注番号：




                '' as ZWS_1_1 : String,
                '' as ZWS_1_2 : String,
                '' as ZWS_2_1 : String,
                '' as ZWS_2_2 : String,
                '' as ZWS_3_1 : String,
                '' as ZWS_3_2 : String,
                '' as ZWS_4_1 : String,
                '' as ZWS_4_2 : String,
                '' as ZWS_5_1 : String,
                '' as ZWS_5_2 : String,
                '' as ZWS_6 : String,
                '' as ZWS_7 : String,
                '' as ZWS_8 : String,
                '' as ZWS_9 : String,

                '' as DATE1 : String,
                '' as DATE2 : String,
                '' as DATE3 : String,
                '' as DATE4 : String,

                '' as BP_NAME12 : String,
                '' as POSTCODE2 : String,
                '' as REGIONS2 : String,
                '' as TEL2 : String,
                '' as FAX2 : String,
                '' as TYPE : String,
                '' as PO_D_DATE2 : String,
                '' as PO_PUR_QTY2 : String,
                T02.INT_NUMBER,//海外発注番号：
                T02.PR_BY,//依頼者：
                T02.TO_MAT.CUST_MATERIAL,//P/N：
                '' as BP_ID : String(50),//得意先コード
                
        }

    


    entity PCH_T03_PO_ITEM       as
        select from view.T01_PO_H as T01
        join view.T02_PO_D as T02
            on(
                T01.PO_NO = T02.PO_NO
            )
        left join view.MST_T05_SAP_BP_PURCHASE T04
            on T01.SUPPLIER = T04.SUPPLIER
            and T01.PO_ORG = T04.PURCHASE_ORG


            join view.SYS_T01_USER as Tu
                on Tu.USER_ID = COALESCE($user, 'anonymous')
                and  (T01.SUPPLIER in (select BP_ID from view.AUTH_USER_BP   ) and Tu.USER_TYPE = '2') 

        
        left join view.PO_TYPE_POP T06
            on T02.PO_TYPE = T06.VALUE
        left join view.PCH03_STATUS_POP T07
            on T02.STATUS = T07.VALUE
        left join view.MST_BP_ZABC_POP T08
            on T04.ZABC = T08.VALUE
        left join view.MST_T06_MAT_PLANT T09
            on T09.PLANT_ID = T02.PLANT_ID
            and T09.MAT_ID = T02.MAT_ID

          distinct{
            @title: '{i18n>PO_NO_DNO}'
            key T01.PO_NO || RIGHT('00000' || T02.D_NO, 5) as ID   : String(100),
            key T01.PO_NO, // 発注番号
            key T02.D_NO, // 明細番号
                T01.SUPPLIER, // 仕入先コード
                T02.SUPPLIER_MAT, // 仕入先品目コード
                T01.TO_SAP_BP.BP_NAME1 as PCH03_BP_NAME1,
                T02.MAT_ID, // 品目コード
                T02.PO_TYPE, // 発注区分  C：新規 U：変更 D：削除

                T01.CD_DATE as PO_DATE, //発注日
                T02.STATUS, // ステータス  01：送信済　02：照会済
                
                T02.CD_BY, //登録者
                T02.PO_D_DATE, //所要日付
                T01.PO_BUKRS as PLANT_ID,
                
                @title: '{i18n>ZABC1}'
                     case T04.ZABC
                    when 'E' then 'E'
                    when 'F' then 'F'
                    when 'W' then 'W'
                    else 'C' end as ZABC : String(5), //ABC区分 E：Email F：Fax  W：Web edi
                @title: '{i18n>PO_TYPE}'
                T06.NAME  as PO_TYPE_NAME,// 発注区分名称
                @title: '{i18n>STATUS}'
                T07.NAME as STATUS_NAME, // ステータス  01：送信済　02：照会済
                @title: '{i18n>ZABC1}'
                 case T04.ZABC
                    when 'E' then T08.NAME
                    when 'F' then T08.NAME
                    when 'W' then T08.NAME
                    else 'その他' end as ZABC1_NAME : String(10), //ABC区分 E：Email F：Fax  W：Web edi
                T02.PO_D_TXZ01, // 品目テキスト
                T02.PO_PUR_QTY, // 発注数量
                T02.PO_PUR_UNIT, // 単位
                T02.CURRENCY, // 通貨
                T02.DEL_PRICE, // 発注単価（値）
                T02.UNIT_PRICE, // 価格単位
                T02.DEL_AMOUNT, // 発注金額（値）
                T02.MEMO, // 備考
                Tu.USER_TYPE, // 用户type
                T02.STORAGE_LOC,
                T02.STORAGE_LOC || T02.STORAGE_TXT AS STORAGE_TXT: String(100),
                '' as TYPE : String,//csv ステータス
                T02.TO_MAT.TO_SAP_BP.BP_NAME1,
                 case T02.DOWN_FLAG
                    when 'Y' then '確認済'
                    else '未確認' end as DOWN_FLAG : String(10), //確認済みフラグ
                T02.TO_MAT.MANU_MATERIAL,
                0 as ISSUEDAMOUNT  :  Decimal(18, 5),//csv 発注金額
                '' as BP_ID : String(50),//得意先コード
                '' as checkOk : String(50), // 検査合区分
                @title: '{i18n>CD_BY}'
               '' AS BYNAME : String(50), // 発注担当者
                T01.POCDBY , // 発注担当者
                T01.SAP_CD_BY_TEXT , // 発注担当者
                @title: '{i18n>CD_BY}'
                T02.SAP_CD_BY, // SAP担当者
                T02.INT_NUMBER,
                T02.TO_MAT.CUST_MATERIAL,//P/N：
                T02.PR_BY,
                '' as EMAIL_ADDRESS : String(500), // 邮箱地址,
                T09.IMP_COMP,//検査合区分
                T02.PO_PUR_QTY as PO_PUR_QTY2, // 発注数量
                T02.DEL_PRICE as DEL_PRICE2, // 発注単価（値）
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
                and T01.PO_ORG = T04.PURCHASE_ORG

            join view.SYS_T01_USER as Tu
                on Tu.USER_ID = COALESCE($user, 'anonymous')
                and Tu.USER_TYPE = '1'

                
            left join view.PO_TYPE_POP T06
                on T02.PO_TYPE = T06.VALUE
            left join view.PCH03_STATUS_POP T07
                on T02.STATUS = T07.VALUE
            left join view.MST_BP_ZABC_POP T08
                on T04.ZABC = T08.VALUE
        left join view.MST_T06_MAT_PLANT T09
            on T09.PLANT_ID = T02.PLANT_ID
            and T09.MAT_ID = T02.MAT_ID

            distinct {
             @title: '{i18n>PO_NO_DNO}'
            key T01.PO_NO || RIGHT('00000' || T02.D_NO, 5) as ID   : String(100),
            key T01.PO_NO, // 発注番号
            key T02.D_NO, // 明細番号
                T01.SUPPLIER, // 仕入先コード
                T02.SUPPLIER_MAT, // 仕入先品目コード
                T01.TO_SAP_BP.BP_NAME1 as PCH03_BP_NAME1,
                T02.MAT_ID, // 品目コード
                T02.PO_TYPE, // 発注区分  C：新規 U：変更 D：削除

                T01.CD_DATE as PO_DATE, //発注日
                T02.STATUS, // ステータス  01：送信済　02：照会済
                
                T02.CD_BY, //登録者
                T02.PO_D_DATE, //所要日付
                T01.PO_BUKRS as PLANT_ID,
                
                @title: '{i18n>ZABC1}'
                     case T04.ZABC
                    when 'E' then 'E'
                    when 'F' then 'F'
                    when 'W' then 'W'
                    else 'C' end as ZABC : String(5), //ABC区分 E：Email F：Fax  W：Web edi
                @title: '{i18n>PO_TYPE}'
                T06.NAME  as PO_TYPE_NAME,// 発注区分名称
                @title: '{i18n>STATUS}'
                T07.NAME as STATUS_NAME, // ステータス  01：送信済　02：照会済
                @title: '{i18n>ZABC1}'
                 case T04.ZABC
                    when 'E' then T08.NAME
                    when 'F' then T08.NAME
                    when 'W' then T08.NAME
                    else 'その他' end as ZABC1_NAME : String(10), //ABC区分 E：Email F：Fax  W：Web edi
                T02.PO_D_TXZ01, // 品目テキスト
                T02.PO_PUR_QTY, // 発注数量
                T02.PO_PUR_UNIT, // 単位
                T02.CURRENCY, // 通貨
                T02.DEL_PRICE, // 発注単価（値）
                T02.UNIT_PRICE, // 価格単位
                T02.DEL_AMOUNT, // 発注金額（値）
                T02.MEMO, // 備考
                Tu.USER_TYPE, // 用户type
                T02.STORAGE_LOC,
                T02.STORAGE_LOC || T02.STORAGE_TXT AS STORAGE_TXT: String(100),
                '' as TYPE : String,//csv ステータス
                T02.TO_MAT.TO_SAP_BP.BP_NAME1,//メーカー
                 case T02.DOWN_FLAG
                    when 'Y' then '確認済'
                    else '未確認' end as DOWN_FLAG : String(10), //確認済みフラグ
                    T02.TO_MAT.MANU_MATERIAL, //メーカー品番
                0 as ISSUEDAMOUNT  :  Decimal(18, 5),//csv 発注金額
                '' as BP_ID : String(50),//得意先コード
                '' as checkOk : String(50), // 検査合区分
                @title: '{i18n>CD_BY}'
                '' AS BYNAME : String(50), // 発注担当者
                T01.POCDBY , // 発注担当者
                T01.SAP_CD_BY_TEXT , // 発注担当者
                @title: '{i18n>CD_BY}'
                T02.SAP_CD_BY, // SAP担当者
                T02.INT_NUMBER,
                T02.TO_MAT.CUST_MATERIAL,
                T02.PR_BY,
                '' as EMAIL_ADDRESS : String(500), // 邮箱地址,
                T09.IMP_COMP,//検査合区分
                T02.PO_PUR_QTY as PO_PUR_QTY2, // 発注数量
                T02.DEL_PRICE as DEL_PRICE2, // 発注単価（値）

                
            }
            where
                Tu.USER_TYPE = '1';


    action PCH03_SENDEMAIL(parms : String) returns String; //发用邮件方法
    action PCH03_QUEREN(parms : String) returns String; //确认方法
    action PCH03_GETTYPE(parms : String) returns String; //获取邮件类型
    action PCH03_PRINTHX(parms : String) returns String; //打印csv回执方法 
    action PCH03_LOGDATA(parms : String) returns String; //判断po明细是否和履历表中的一致
    action PCH03_UPDATE_EMAILTYPE(parms : String) returns String; //更新fax的邮箱头，以用作邮件发送

}



annotate TableService.PCH_T03_PO_ITEM with {

    PO_TYPE @(Common: {ValueList: {entity: 'PO_TYPE_POP', }});
    STATUS  @(Common: {ValueList: {entity: 'PCH03_STATUS_POP', }});
    ZABC    @(Common: {ValueList: {entity: 'MST_BP_ZABC_POP', }});
};