using {TableService as view} from './table';

extend service TableService {

    entity PCH_T03_PO_ITEM as
        select from view.PCH_T01_PO_H as T01
        left join view.PCH_T02_PO_D as T02 
            ON (T01.PO_NO = T02.PO_NO)
        left join view.PCH_T03_PO_C as T03 
            on (T01.PO_NO = T03.PO_NO and T02.D_NO = T03.D_NO)
        inner join view.SYS_T01_USER as T05
            on (T01.PO_NO = T03.PO_NO and T02.D_NO = T03.D_NO)
        
        
        distinct {
           
            KEY T01.PO_NO,                    // 発注番号
            KEY T02.D_NO,                     // 明細番号
            T01.SUPPLIER,                     // 仕入先コード
            T02.SUPPLIER_MAT,                 // 仕入先品目コード
            T02.MAT_ID,                       // 品目コード
            T02.PO_TYPE,                       // 発注区分
            T01.PO_DATE,                       //発注日
            T03.STATUS,                       // ステータス
            T02.CD_BY,                          //登録者
            T02.PO_D_DATE,                          //所要日付

            




            T02.PO_D_TXZ01,                   // 品目テキスト
            T02.PO_PUR_QTY,                   // 発注数量
            T02.PO_PUR_UNIT,                  // 単位
            
            T03.DELIVERY_DATE,                // 納品日
            T03.QUANTITY,                     // 納品数
            T02.DEL_PRICE,                    // 発注単価
            T02.DEL_AMOUNT,                   // 発注金額
            T02.MEMO,                         // 備考
            T02.STORAGE_LOC || '' || T02.STORAGE_TXT as STORAGE_NAME : String(255) // 納品先名
        }
}
