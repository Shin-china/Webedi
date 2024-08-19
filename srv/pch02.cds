using {TableService as view} from './table';
using {PCH} from '../db/model-pch';


extend service TableService {

    entity PCH_T02_USER as
        select from PCH.PCH_T03_PO_C as T01
        left join PCH.PCH_T01_PO_H as T02 ON (T01.PO_NO = T02.PO_NO)
        left join PCH.PCH_T02_PO_D as T03 on (T01.PO_NO = T03.PO_NO and T01.D_NO = T03.D_NO)
        distinct {
           
            KEY T01.PO_NO,                    // 発注番号
            KEY T01.D_NO,                     // 明細番号
            T02.SUPPLIER,                     // 仕入先コード
            T03.MAT_ID,                       // 品目コード
            T01.STATUS,                       // ステータス
            T03.PO_D_TXZ01,                   // 品目テキスト
            T03.PO_PUR_QTY,                   // 発注数量
            T03.PO_PUR_UNIT,                  // 単位
            T03.SUPPLIER_MAT,                 // 仕入先品目コード
            T01.DELIVERY_DATE,                // 納品日
            T01.QUANTITY,                     // 納品数
            T03.DEL_PRICE,                    // 発注単価
            T03.DEL_AMOUNT,                   // 発注金額
            T03.MEMO,                         // 備考
            T03.STORAGE_LOC || '' || T03.STORAGE_TXT as STORAGE_NAME : String(255) // 納品先名
        }
}
