using {TableService as view} from './table';
using {SYS} from '../db/model-sys';
using {PCH} from '../db/model-pch';

// MM-029 支払通知照会

extend service TableService {

    entity PCH_T04_PAYMENT as
            select from PCH.PCH_T04_PAYMENT_H as T04
            left join PCH.PCH_T05_PAYMENT_D as T05
                on (
                    T04.INV_NO = T05.INV_NO
                 and T04.GL_YEAR = T05.GL_YEAR
                )
             left join PCH.PCH_T02_PO_D as T02 
               on (
                    T02.PO_NO = T05.PO_NO
                 and T02.D_NO = T05.D_NO
                )  
        {
        KEY T05.PO_NO,                     // UMC発注番号
        KEY T05.D_NO,                      // 明細番号                       
            T04.SUPPLIER,                  // 業者コード
            T04.SUPPLIER_DESCRIPTION,      // 業者名
            T05.MAT_ID,                    // 品目コード
            T04.SEND_FLAG,                 // ステータス
            T04.INV_POST_DATE,             // 検収月
            T05.PO_NO || '' || T05.D_NO AS NO_DETAILS : String(255), // 発注明細NO
            T05.MAT_DESC,                  // 品目名称
            T04.INV_DATE,                  // 入荷日(不确定)
            T05.QUANTITY,                  // 仕入単位数
            T05.UNIT_PRICE,                // 取引通貨単価
            T05.CURRENCY,                  // INV通貨コード
            cast(T04.EXCHANGE as Decimal(15, 2)) as EXCHANGE_DECIMAL, // 換算レート（转换为Decimal类型）
            T05.PO_TRACK_NO,               // 備考
            T05.TAX_RATE,                  // INV税率
            T04.INV_BASE_DATE,             // 支払日
            T05.UNIT_PRICE * cast(T04.EXCHANGE as Decimal(15, 2)) AS UNIT_PRICE_IN_YEN : Decimal(15, 2), // 円換算後単価(PO)
            (T05.UNIT_PRICE * cast(T04.EXCHANGE as Decimal(15, 2))) * (T05.QUANTITY * (1 + T05.TAX_RATE / 100)) AS TOTAL_AMOUNT_IN_YEN : Decimal(20, 2), // 円換算後税込金額(検収)
            T02.SUPPLIER_MAT               // 仕入先品目コード
        };

};
