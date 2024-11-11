using {TableService as view} from './table';

extend service TableService {
 

    entity PCH_T06_PO_ITEM as
        select from view.T01_PO_H as T01
         join view.T02_PO_D as T02 
            ON (T01.PO_NO = T02.PO_NO)
         left join view.T03_PO_C as T03 
            on (T01.PO_NO = T03.PO_NO and T02.D_NO = T03.D_NO)

        distinct {
           KEY T01.PO_NO || T02.D_NO || COALESCE(T03.SEQ,1)  as ID : String(100),
            KEY T01.PO_NO,                    // 発注番号
            KEY T02.D_NO,                     // 明細番号
            KEY COALESCE(T03.SEQ,1) as SEQ :Integer,                     // 明細番号
            T02.MAT_ID,                       // 品目コード
            T02.PO_D_TXZ01,                   // 品目テキスト
            // T03.DELIVERY_DATE,                   // 納品日
            TO_CHAR(T03.DELIVERY_DATE, 'yyyy-MM-dd') as DELIVERY_DATE : String(100),
            T03.QUANTITY,                   // 納品数量
            T02.STATUS,                       // ステータス  01：送信済　02：照会済
            T02.PO_PUR_QTY,                   // 発注数量
            T02.PO_PUR_UNIT,                  // 単位
            T01.SUPPLIER,                     // SUPPLIER業者コード
            T02.SUPPLIER_MAT as BP_NAME1,     // 業者名
            T02.STORAGE_LOC || T02.STORAGE_TXT as LOCNAME : String(100),     // 納品先
            T02.CURRENCY,                  // 通貨
            ROUND(T02.DEL_PRICE/T02.UNIT_PRICE,3) as PRICE: Decimal(13, 3),  ///発注単価PO明細の単価/価格単位
            T02.DEL_AMOUNT,                  // 発注金額（値）
            T01.PO_DATE,                       //発注日
            T02.CUSTOMER_MAT,                       //顧客品番
            T02.SUPPLIER_MAT ,                  // 仕入先品目コード
            T02.PO_D_DATE,                     //所要日付 
            T02.PO_TYPE,                       // 発注区分  C：新規 U：変更 D：削除
            T02.MEMO,                           // 備考
            T03.RelevantQuantity as RQ,                           // 備考
            
            T03.ExtNumber,                          //参照
            T03.CD_BY,                          //R
            T03.CD_TIME,                          //参照
            
            

        }
        action PCH06_SAVE_DATA(str : String) returns String;//Send Email
        action PCH06_TQ(str : String) returns String;//同期

}

// annotate TableService.T03_PO_C with @odata.draft.enabled;
annotate TableService.T03_PO_C with {
    PO_NO @(Common : {ValueList : {
        entity     : 'PCH_T03_PO_ITEM',
        Parameters : [
            {
                $Type             : 'Common.ValueListParameterInOut',
                LocalDataProperty : 'PO_NO',
                ValueListProperty : 'PO_NO'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'D_NO'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'STORAGE'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'SUPPLIER'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'SUPPLIER_MAT'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'MAT_ID'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'PO_TYPE'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'PO_DATE'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'STATUS'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'CD_BY'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'PO_D_DATE'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'ZABC'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'PO_D_TXZ01'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'PO_PUR_QTY'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'PO_PUR_UNIT'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'CURRENCY'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'DEL_PRICE'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'UNIT_PRICE'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'DEL_AMOUNT'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'MEMO'
            },

        ]
    }});
   
};