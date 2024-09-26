using { TableService as view } from './table';
using { SYS } from '../db/model-sys';
using { PCH } from '../db/model-pch';
using { MST } from '../db/model-mst';

// MM-029 支払通知照会

extend service TableService {

    entity PCH_T04_PAYMENT as
        select from PCH.PCH_T02_PO_D as T02
        left join PCH.PCH_T05_PAYMENT_D as T05
            on (
                T02.PO_NO = T05.PO_NO
                and T02.D_NO = T05.D_NO
            )
        left join PCH.PCH_T01_PO_H AS T01
            on (
                T01.PO_NO = T05.PO_NO
            )
        left join PCH.PCH_T04_PAYMENT_H as T04
            on (
                T05.INV_NO = T04.INV_NO
                and T05.GL_YEAR = T04.GL_YEAR
            )
        left join MST.MST_T03_SAP_BP as M03
            on (
                T04.SUPPLIER = M03.BP_ID
            )
        left join MST.MST_T04_SAP_BP_PURCHASE AS M04
            on (
                M04.SUPPLIER = T01.SUPPLIER
                and M04.PURCHASE_ORG = T01.PO_ORG
            )

    {
        KEY T02.PO_NO,                     // UMC発注番号
        KEY T02.D_NO,                      // 明細番号 
        T01.SUPPLIER,                      // 仕入先
        T01.PO_ORG,                        // 購買組織
        T04.SUPPLIER_DESCRIPTION,          // 業者名
        T05.MAT_ID,                        // 品目コード
        T05.MAT_DESC,                      // 品目名称
        T05.GR_DATE,                       // 入荷日
        T05.QUANTITY,                      // 仕入単位数
        T05.TAX_RATE,                      // INV税率
        T05.PO_TRACK_NO,                   // 備考
        T04.INV_BASE_DATE,                 // 支払日
        T05.PRICE_AMOUNT,
        T04.INV_NO,                        // 发票号
        T04.SEND_FLAG,                     // ステータス
        T04.INV_POST_DATE,                 // 検収月
        T05.CURRENCY,                      // INV通貨コード
        T04.EXCHANGE,                      // 換算レート
        T02.SUPPLIER_MAT,                  // 仕入先品目コード
        M03.LOG_NO,                        // 登録番号    
        M04.ZABC,                          // ABC区分
        T05.TOTAL_AMOUNT,
        T05.UNIT_PRICE,
        T02.PO_NO || '' || T02.D_NO AS NO_DETAILS : String(255), // 発注\明細NO
        TO_CHAR(T04.INV_POST_DATE, 'YYYYMM') as INV_MONTH : String,  //检收月

         }

      entity PCH_T04_PAYMENT_UNIT as
        select from PCH_T04_PAYMENT as 

    ![distinct] {
        KEY PO_NO,                     // UMC発注番号
        KEY D_NO,                      // 明細番号 
        KEY TAX_RATE,                  // INV税率
        KEY UNIT_PRICE,
        KEY EXCHANGE,                  // 換算レート
        SUPPLIER,                      // 仕入先
        PO_ORG,                        // 購買組織
        SUPPLIER_DESCRIPTION,          // 業者名
        MAT_ID,                        // 品目コード
        MAT_DESC,                      // 品目名称
        GR_DATE,                       // 入荷日
        QUANTITY,                      // 仕入単位数
        PO_TRACK_NO,                   // 備考
        INV_BASE_DATE,                 // 支払日
        INV_NO,                        // 发票号
        SEND_FLAG,                     // ステータス
        INV_POST_DATE,                 // 検収月
        CURRENCY,                      // INV通貨コード
        SUPPLIER_MAT,                  // 仕入先品目コード
        LOG_NO,                        // 登録番号    
        ZABC,                          // ABC区分
        NO_DETAILS,
        PRICE_AMOUNT,
        TOTAL_AMOUNT,
        INV_MONTH,                     //检收月

        FLOOR(UNIT_PRICE * COALESCE(EXCHANGE, 1) * 1000) / 1000 AS UNIT_PRICE_IN_YEN : Decimal(15, 3), // 円換算後単価(PO)      
        FLOOR(TOTAL_AMOUNT * COALESCE(EXCHANGE, 1)) AS TOTAL_AMOUNT_IN_YEN : Decimal(20, 0), // 円換算後税込金額（檢収）
        
            case when TAX_RATE = 8 then 
                cast(PRICE_AMOUNT as Decimal(18,3)) 
            end as PRICE_AMOUNT_8 : Decimal(18, 3),   // 仕入金額計(8%対象)

             case when TAX_RATE = 10 then 
                cast(PRICE_AMOUNT as Decimal(18,3)) 
            end as PRICE_AMOUNT_10 : Decimal(18, 3),   // 仕入金額計(10%対象)

    }

     entity PCH_T04_PAYMENT_SUM as
        select from PCH_T04_PAYMENT_UNIT as 

    ![distinct] {
        KEY TAX_RATE,                  // INV税率
        KEY SUPPLIER,
        PO_NO,                     // UMC発注番号
        D_NO,                      // 明細番号 
        UNIT_PRICE,
        SUPPLIER_DESCRIPTION,
        MAT_ID,                        // 品目コード
        MAT_DESC,                      // 品目名称
        GR_DATE,                       // 入荷日
        QUANTITY,                      // 仕入単位数
        PO_TRACK_NO,                   // 備考
        INV_BASE_DATE,                 // 支払日
        LOG_NO,
        INV_NO,
        NO_DETAILS,
        EXCHANGE,                      // 換算レート
        CURRENCY,                      // INV通貨コード
        PRICE_AMOUNT,
        TOTAL_AMOUNT,
        UNIT_PRICE_IN_YEN,
        TOTAL_AMOUNT_IN_YEN,
        PRICE_AMOUNT_8,
        PRICE_AMOUNT_10, 
        SUM(PRICE_AMOUNT_8)  as TOTAL_PRICE_AMOUNT_8: Decimal(18, 3),       // 仕入金額計(8%対象)
        SUM(PRICE_AMOUNT_10) as TOTAL_PRICE_AMOUNT_10: Decimal(18, 3),      // 仕入金額計(8%対象)
        SUM(case when TAX_RATE not in (8, 10) then TOTAL_AMOUNT_IN_YEN else 0 end) as NON_APPLICABLE_AMOUNT: Decimal(18, 3), // 计算対象外金額
    }
    group by PO_NO,D_NO,SUPPLIER,TAX_RATE,UNIT_PRICE,SUPPLIER_DESCRIPTION,MAT_ID,MAT_DESC,GR_DATE,QUANTITY,PO_TRACK_NO,INV_BASE_DATE,LOG_NO,INV_NO,NO_DETAILS,EXCHANGE,CURRENCY,PRICE_AMOUNT,TOTAL_AMOUNT,UNIT_PRICE_IN_YEN,TOTAL_AMOUNT_IN_YEN,PRICE_AMOUNT_8,PRICE_AMOUNT_10;

     entity PCH_T04_PAYMENT_GRO as
        select from PCH_T04_PAYMENT_SUM as 

    ![distinct] {
        KEY PO_NO,                     // UMC発注番号
        KEY D_NO,                      // 明細番号 
        KEY SUPPLIER,
        UNIT_PRICE,
        SUPPLIER_DESCRIPTION,
        MAT_ID,                        // 品目コード
        MAT_DESC,                      // 品目名称
        GR_DATE,                       // 入荷日
        QUANTITY,                      // 仕入単位数
        TAX_RATE,                      // INV税率
        PO_TRACK_NO,                   // 備考
        INV_BASE_DATE,                 // 支払日
        INV_NO,
        LOG_NO,
        NO_DETAILS,
        EXCHANGE,                      // 換算レート
        CURRENCY,                      // INV通貨コード 
        PRICE_AMOUNT,
        TOTAL_AMOUNT,
        UNIT_PRICE_IN_YEN,
        TOTAL_AMOUNT_IN_YEN,
        PRICE_AMOUNT_8,
        PRICE_AMOUNT_10, 
        TOTAL_PRICE_AMOUNT_8,
        TOTAL_PRICE_AMOUNT_10,
        NON_APPLICABLE_AMOUNT,
        TOTAL_PRICE_AMOUNT_8 * 0.08 as CONSUMPTION_TAX_8 : Decimal(15, 0),     // 计算消費税計(8%対象)
        TOTAL_PRICE_AMOUNT_10 * 0.10 as CONSUMPTION_TAX_10 : Decimal(15, 0),   // 计算消費税計(10%対象)
    }

       entity PCH_T04_PAYMENT_END as
        select from PCH_T04_PAYMENT_GRO as 

    ![distinct] {
        KEY PO_NO,                     // UMC発注番号
        KEY D_NO,                      // 明細番号 
        KEY SUPPLIER,
        UNIT_PRICE,
        SUPPLIER_DESCRIPTION,
        MAT_ID,                        // 品目コード
        MAT_DESC,                      // 品目名称
        GR_DATE,                       // 入荷日
        QUANTITY,                      // 仕入単位数
        TAX_RATE,                      // INV税率
        PO_TRACK_NO,                   // 備考
        INV_BASE_DATE,                 // 支払日
        INV_NO,
        LOG_NO,
        NO_DETAILS,
        EXCHANGE,                      // 換算レート
        CURRENCY,                      // INV通貨コード 
        PRICE_AMOUNT,
        TOTAL_AMOUNT,
        UNIT_PRICE_IN_YEN,
        TOTAL_AMOUNT_IN_YEN,
        PRICE_AMOUNT_8,
        PRICE_AMOUNT_10, 
        TOTAL_PRICE_AMOUNT_8,
        TOTAL_PRICE_AMOUNT_10,
        NON_APPLICABLE_AMOUNT,
        CONSUMPTION_TAX_8,
        CONSUMPTION_TAX_10,   
        CONSUMPTION_TAX_8 + TOTAL_PRICE_AMOUNT_8 as TOTAL_PAYMENT_AMOUNT_8_END : Decimal(18, 3),     // 计算税込支払金額(8%対象)
        CONSUMPTION_TAX_8 + TOTAL_PRICE_AMOUNT_10 as TOTAL_PAYMENT_AMOUNT_10_END : Decimal(18, 3),   // 计算税込支払金額(10%対象)
    }

    entity PCH_T04_PAYMENT_FINAL as
        select from PCH_T04_PAYMENT_END as 

    ![distinct] {
        KEY PO_NO,                     // UMC発注番号
        KEY D_NO,                      // 明細番号 
        KEY SUPPLIER,
        UNIT_PRICE,
        SUPPLIER_DESCRIPTION,
        MAT_ID,                        // 品目コード
        MAT_DESC,                      // 品目名称
        GR_DATE,                       // 入荷日
        QUANTITY,                      // 仕入単位数
        TAX_RATE,                      // INV税率AWER
        PO_TRACK_NO,                   // 備考
        INV_BASE_DATE,                 // 支払日
        INV_NO,
        LOG_NO,
        NO_DETAILS,
        EXCHANGE,                      // 換算レート
        CURRENCY,                      // INV通貨コード 
        PRICE_AMOUNT,
        TOTAL_AMOUNT,
        UNIT_PRICE_IN_YEN,
        TOTAL_AMOUNT_IN_YEN,
        PRICE_AMOUNT_8,
        PRICE_AMOUNT_10, 
        TOTAL_PRICE_AMOUNT_8,
        TOTAL_PRICE_AMOUNT_10,
        NON_APPLICABLE_AMOUNT,
        CONSUMPTION_TAX_8,
        CONSUMPTION_TAX_10,   
        TOTAL_PAYMENT_AMOUNT_8_END,     // 计算税込支払金額(8%対象)
        TOTAL_PAYMENT_AMOUNT_10_END,    // 计算税込支払金額(10%対象)
        TOTAL_PAYMENT_AMOUNT_8_END + TOTAL_PAYMENT_AMOUNT_8_END + NON_APPLICABLE_AMOUNT as TOTAL_PAYMENT_AMOUNT_FINAL : Decimal(18, 3),     // 総合計
        case 
            when CURRENCY = 'JPY' then cast(PRICE_AMOUNT as Decimal(18,3))
            when CURRENCY in ('USD', 'EUR') then cast(floor(PRICE_AMOUNT * EXCHANGE) as Decimal(18,0))
        end as BASE_AMOUNT_EXCLUDING_TAX : Decimal(18,0),   //基準通貨金額税抜

    };

    action PCH04_SENDEMAIL(parms : String) returns String;

}

annotate TableService.PCH_T04_PAYMENT with {
    SEND_FLAG @(Common: {ValueList: {entity: 'PCH04_STATUS_POP1', }}); 
};
