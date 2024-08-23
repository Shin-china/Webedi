using {TableService as view} from './table';
using {SYS} from '../db/model-sys';
using {PCH} from '../db/model-pch';


extend service TableService {

    entity PCH_T02_USER as
            select from PCH.PCH_T01_PO_H as T01
            left join PCH.PCH_T02_PO_D as T02
                on(
                    T01.PO_NO = T02.PO_NO
                )
            left join PCH.PCH_T03_PO_C as T03
                on(
                        T02.PO_NO = T03.PO_NO
                    and T02.D_NO  = T03.D_NO
                )
            inner join view.SYS_T01_USER as T04
                on  ifnull($user,'anonymous')   =  T04.USER_ID
                and T04.USER_TYPE in ('1')

            inner join view.SYS_T09_USER_2_PLANT as T05
                on  T05.USER_ID  = ifnull($user,'anonymous') 
                and T02.PLANT_ID = T05.PLANT_ID

            distinct {
                key T02.PO_NO,              // 発注番号
                key T02.D_NO,               // 明細番号
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
                    T02.STORAGE_LOC || '' || T02.STORAGE_TXT as STORAGE_NAME : String(255) // 納品先名
            }

        union all
            select from PCH.PCH_T01_PO_H as T01
            left join PCH.PCH_T02_PO_D as T02
                on(
                    T01.PO_NO = T02.PO_NO
                )
            left join PCH.PCH_T03_PO_C as T03
                on(
                        T02.PO_NO = T03.PO_NO
                    and T02.D_NO  = T03.D_NO
                )
            inner join view.SYS_T01_USER as T04
                on  $user         =  T04.USER_ID
                and T04.USER_TYPE in ('2')
                and T01.SUPPLIER  =  T04.BP_NUMBER

            distinct {
                key T02.PO_NO,            // 発注番号
                key T02.D_NO,             // 明細番号
                    T01.SUPPLIER,         // 仕入先コード
                    T02.MAT_ID,           // 品目コード
                    T03.STATUS,           // ステータス
                    T02.PO_D_TXZ01,       // 品目テキスト
                    T02.PO_PUR_QTY,       // 発注数量
                    T02.PO_PUR_UNIT,      // 単位
                    T02.SUPPLIER_MAT,     // 仕入先品目コード
                    T03.DELIVERY_DATE,    // 納品日
                    T03.QUANTITY,         // 納品数
                    T02.DEL_PRICE,        // 発注単価
                    T02.DEL_AMOUNT,       // 発注金額
                    T02.MEMO,             // 備考
                    T02.STORAGE_LOC || '' || T02.STORAGE_TXT as STORAGE_NAME : String(255) // 納品先名
            }

 action PCH02_CONFIRMATION_REQUEST(parms : String) returns String;

}

annotate TableService.PCH_T02_USER with {
  
  STATUS @(Common: {ValueList: {entity: 'PCH02_STATUS_POP', }}); 

};