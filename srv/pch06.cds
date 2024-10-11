using {TableService as view} from './table';

extend service TableService {
 

    entity PCH_T06_PO_ITEM as
        select from view.PCH_T01_PO_H as T01
         join view.PCH_T02_PO_D as T02 
            ON (T01.PO_NO = T02.PO_NO)
         join view.PCH_T03_PO_C as T03 
            on (T01.PO_NO = T03.PO_NO and T02.D_NO = T03.D_NO)

   
   
   
        
        distinct {
           KEY T01.PO_NO || T02.D_NO ||T03.SEQ  as ID : String(100),
            KEY T01.PO_NO,                    // 発注番号
            KEY T02.D_NO,                     // 明細番号
            KEY T03.SEQ,                     // 明細番号
            T02.MAT_ID,                       // 品目コード
            T02.PO_D_TXZ01,                   // 品目テキスト
            T03.DELIVERY_DATE,                   // 納品日
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
            
            T03.ExtNumber,                          //参照
            
          
            
            

        }
        

}

annotate TableService.PCH_T03_PO_C with @odata.draft.enabled;
annotate TableService.PCH_T03_PO_C with {
    PO_NO @(Common : {ValueList : {
        entity     : 'PCH_T03_PO_ITEM',
        Parameters : [
            {
                $Type             : 'Common.ValueListParameterOut',
                LocalDataProperty : 'PO_NO',
                ValueListProperty : 'PO_NO'
            },
        ]
    }});
   
};