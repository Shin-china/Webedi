using {TableService as view} from '../srv/table';
using {MST} from '../db/model-mst';
using {SYS} from '../db/model-sys';


extend service TableService with {
  entity COM_Y_N_DROPDOWN         as
    select from SYS.T08_COM_OP_D {
      key D_CODE as OP_VALUE,
          D_NAME as OP_NAME
    }
    where
      H_CODE = 'Y_OR_N';
  entity PCH01_STATUS_POP_1 as
    select from SYS.T07_COM_OP_H T01
    inner join SYS.T08_COM_OP_D T02
      on T01.H_CODE = T02.H_CODE

    {
          D_NAME  as NAME,
      key VALUE01 as VALUE
    }
    where
      T01.H_CODE = 'PCH01_STATUS';

  entity PCH02_STATUS_POP   as
    select from SYS.T07_COM_OP_H T03
    inner join SYS.T08_COM_OP_D T04
      on T03.H_CODE = T04.H_CODE

    {
      key VALUE01 as VALUE,
          D_NAME  as NAME

    }
    where
      T03.H_CODE = 'PCH02_STATUS';

  entity MST_BP_ZABC_POP    as
    select from SYS.T07_COM_OP_H T03
    inner join SYS.T08_COM_OP_D T04
      on T03.H_CODE = T04.H_CODE

    {
          D_NAME  as NAME,
      key VALUE01 as VALUE
    }
    where
      T03.H_CODE = 'BP_ZABC';

  entity PCH03_STATUS_POP   as
    select from SYS.T07_COM_OP_H T03
    inner join SYS.T08_COM_OP_D T04
      on T03.H_CODE = T04.H_CODE

    {
          D_NAME  as NAME,
      key VALUE01 as VALUE
    }
    where
      T03.H_CODE = 'PCH03_STATUS';

  entity PO_TYPE_POP        as
    select from SYS.T07_COM_OP_H T03
    inner join SYS.T08_COM_OP_D T04
      on T03.H_CODE = T04.H_CODE

    {
      key VALUE01 as VALUE,
          D_NAME  as NAME,
    }
    where
      T03.H_CODE = 'PO_TYPE';

  entity PCH04_STATUS_POP1  as
    select from SYS.T07_COM_OP_H T03
    inner join SYS.T08_COM_OP_D T04
      on T03.H_CODE = T04.H_CODE

    {
      key VALUE01 as VALUE,
          D_NAME  as NAME

    }
    where
      T03.H_CODE = 'PCH04_STATUS';

  entity PCH10_INITIAL_OBJ_POP as
    select from SYS.T07_COM_OP_H T01
    inner join SYS.T08_COM_OP_D T02
      on T01.H_CODE = T02.H_CODE

    {
          D_NAME  as NAME,
      key VALUE01 as VALUE
    }
    where
      T01.H_CODE = 'PCH10_INITIAL_OBJ';
  entity PCH10_STATUS_POP   as
    select from SYS.T07_COM_OP_H T03
    inner join SYS.T08_COM_OP_D T04
      on T03.H_CODE = T04.H_CODE

    {
      key VALUE01 as VALUE,
          D_NAME  as NAME

    }
    where
      T03.H_CODE = 'PCH10_STATUS';
  entity MST_T03_SAP_BP_POP   as
    select from view.MST_T03_SAP_BP T03

    {
      key BP_ID ,
          BP_NAME1
        
    }

    entity PCH_T03_PO_POP      as
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
                T02.PLANT_ID,
                
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
                T02.PLANT_ID,
                
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

}

annotate TableService.PCH_T03_PO_POP with @(Capabilities: {FilterRestrictions: {NonFilterableProperties: [D_NO,SUPPLIER_MAT,CD_BY,PLANT_ID,PO_TYPE_NAME,STATUS_NAME,ZABC1_NAME,PO_D_TXZ01,PO_PUR_QTY,PO_PUR_UNIT,CURRENCY,DEL_PRICE,UNIT_PRICE,DEL_AMOUNT,MEMO,USER_TYPE,STORAGE_LOC,STORAGE_TXT,TYPE,BP_NAME1,DOWN_FLAG,MANU_MATERIAL,ISSUEDAMOUNT,BP_ID,checkOk,BYNAME,POCDBY,SAP_CD_BY,INT_NUMBER,CUST_MATERIAL,PR_BY,EMAIL_ADDRESS,IMP_COMP,PO_PUR_QTY2,DEL_PRICE2]}});
annotate TableService.PCH01_STATUS_POP_1 with {
  VALUE @Common.Text: {$value: NAME}
};

annotate TableService.PCH10_STATUS_POP with {
  VALUE @Common.Text: {$value: NAME}
};

annotate TableService.PCH10_INITIAL_OBJ_POP with {
  VALUE @Common.Text: {$value: NAME}
};
annotate TableService.PCH02_STATUS_POP with {
  VALUE @Common.Text: {$value: NAME}
};

annotate TableService.MST_BP_ZABC_POP with {
  VALUE @Common.Text: {$value: NAME}
};

annotate TableService.PCH03_STATUS_POP with {
  VALUE @Common.Text: {$value: NAME}
};

annotate TableService.PCH04_STATUS_POP1 with {
  VALUE @Common.Text: {$value: NAME}
};

annotate TableService.PO_TYPE_POP with {
  VALUE @Common.Text: {$value: NAME}
};
