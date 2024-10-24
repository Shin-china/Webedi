using {TableService as view} from './table';
using {SYS} from '../db/model-sys';
using {PCH} from '../db/model-pch';

// MM-028 納期回答照会

extend service TableService {

    entity PCH_T02_USER as
            select from PCH.T01_PO_H as T01
            left join PCH.T02_PO_D as T02
                on(
                    T01.PO_NO = T02.PO_NO
                )
            left join PCH.T03_PO_C as T03
                on(
                        T02.PO_NO = T03.PO_NO
                    and T02.D_NO  = T03.D_NO
                )
            left join PCH.T08_UPLOAD as T04
                on(
                        T02.PO_NO = T04.PO_NO
                    and T02.D_NO  = T04.D_NO
                )
  
            distinct {
                key T02.PO_NO,              // 発注番号
                key T02.D_NO,               // 明細番号
                key T03.SEQ,                // 連続番号
                    T01.SUPPLIER,           // 仕入先コード
                    T02.MAT_ID,             // 品目コード
                    T03.STATUS,             // ステータス
                    T02.PO_D_TXZ01,         // 品目テキスト
                    T02.PO_PUR_QTY,         // 発注数量
                    T02.PO_PUR_UNIT,        // 単位
                    T02.SUPPLIER_MAT,       // 仕入先品目コード
                    T03.DELIVERY_DATE,      // 納品日
                    T03.QUANTITY,           // 納品数
                    T02.DEL_PRICE,          // 発注単価
                    T02.DEL_AMOUNT,         // 発注金額
                    T02.MEMO,               // 備考
                    T02.STORAGE_LOC || '' || T02.STORAGE_TXT as STORAGE_NAME : String(255), // 納品先名
                    T03.DEL_FLAG,           // 削除フラグ
                    T03.ExtNumber,          // 参照
                    T02.PO_NO || REPEAT('0', 5 - LENGTH(CAST(T02.D_NO AS String))) || CAST(T02.D_NO AS String) as NO_DETAILS : String(15), // 購買伝票\明細NO				
                    // T04.MAT_ID,				// 品目						
                    T04.MAT_NAME,			// テキスト（短）						
                    // T04.QUANTITY,			// 発注数						
                    T04.PLANT_ID,			// プラント						
                    T04.LOCATION_ID,		// 保管場所						
                    T04.INPUT_DATE,			// 納入日付						
                    T04.INPUT_QTY,			// 納入数						
                    // T04.ExtNumber,			// 参照						
                    T04.CD_DATE,			// 登録日付						
                    T04.CD_TIME,			// 時刻						

            }

        // union all
        //     select from PCH.PCH_T01_PO_H as T01
        //     left join PCH.PCH_T02_PO_D as T02
        //         on(
        //             T01.PO_NO = T02.PO_NO
        //         )
        //     left join PCH.PCH_T03_PO_C as T03
        //         on(
        //                 T02.PO_NO = T03.PO_NO
        //             and T02.D_NO  = T03.D_NO
        //         )
         

        //     distinct {
        //         key T02.PO_NO,            // 発注番号
        //         key T02.D_NO,             // 明細番号
        //             T01.SUPPLIER,         // 仕入先コード
        //             T02.MAT_ID,           // 品目コード
        //             T03.STATUS,           // ステータス
        //             T02.PO_D_TXZ01,       // 品目テキスト
        //             T02.PO_PUR_QTY,       // 発注数量
        //             T02.PO_PUR_UNIT,      // 単位
        //             T02.SUPPLIER_MAT,     // 仕入先品目コード
        //             T03.DELIVERY_DATE,    // 納品日
        //             T03.QUANTITY,         // 納品数
        //             T02.DEL_PRICE,        // 発注単価
        //             T02.DEL_AMOUNT,       // 発注金額
        //             T02.MEMO,             // 備考
        //             T02.STORAGE_LOC || '' || T02.STORAGE_TXT as STORAGE_NAME : String(255), // 納品先名
        //             T03.SEQ,              //連続番号
        //             T03.DEL_FLAG          //削除フラグ
        //     }

 action PCH02_CONFIRMATION_REQUEST(parms : String) returns String;

}

annotate TableService.PCH_T02_USER with {
  
  STATUS @(Common: {ValueList: {entity: 'PCH02_STATUS_POP', }}); 

};