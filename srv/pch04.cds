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
        T01.SUPPLIER,                      // 業者コード
        T01.PO_ORG,                        // 購買組織
        T04.SUPPLIER_DESCRIPTION,          // 業者名
        T05.MAT_ID,                        // 品目コード
        T04.INV_NO,                        // 发票号
        T04.SEND_FLAG,                     // ステータス
        T04.INV_POST_DATE,                 // 検収月
        T02.PO_NO || '' || T02.D_NO AS NO_DETAILS : String(255), // 発注\明細NO
        T05.MAT_DESC,                      // 品目名称
        T05.GR_DATE,                       // 入荷日
        T05.QUANTITY,                      // 仕入単位数
        T05.PRICE_AMOUNT,
        T05.CURRENCY,                      // INV通貨コード
        T04.EXCHANGE,                      // 換算レート
        T05.PO_TRACK_NO,                   // 備考
        T05.TAX_RATE,                      // INV税率
        T04.INV_BASE_DATE,                 // 支払日
        
        // 円換算後単価(PO) 保留到小数点3桁
        FLOOR(T05.UNIT_PRICE * COALESCE(T04.EXCHANGE, 1) * 1000) / 1000 AS UNIT_PRICE_IN_YEN : Decimal(15, 3), // 円換算後単価(PO)
        
        // 円換算後税込金額（檢収） 保留整数
        FLOOR(T05.TOTAL_AMOUNT * COALESCE(T04.EXCHANGE, 1)) AS TOTAL_AMOUNT_IN_YEN : Decimal(20, 0), // 円換算後税込金額（檢収）

        T02.SUPPLIER_MAT,                  // 仕入先品目コード
        M03.LOG_NO,                        // 登録番号    
        M04.ZABC,                          // ABC区分
        T05.UNIT_PRICE,                    // 取引通貨単価

       // 基準通貨金額税抜
        CASE 
            WHEN T05.CURRENCY = 'JPY' THEN T05.PRICE_AMOUNT
            WHEN T05.CURRENCY = 'USD' THEN FLOOR(T05.PRICE_AMOUNT * T04.EXCHANGE)
            WHEN T05.CURRENCY = 'EUR' THEN FLOOR(T05.PRICE_AMOUNT * T04.EXCHANGE)
            ELSE T05.PRICE_AMOUNT
        END AS PRICE_AMOUNT_TAX_EXCLUDED : Decimal(20, 0), // 统一在这里指定类型

        // 计算字段
        sum(case when T05.TAX_RATE = 8 then T05.TOTAL_AMOUNT else 0 end) AS TOTAL_AMOUNT_8 : Decimal(20, 6), // 仕入金額計(8%対象)
        sum(case when T05.TAX_RATE = 8 then T05.TOTAL_AMOUNT * 0.08 else 0 end) AS TAX_AMOUNT_8 : Decimal(20, 6), // 消費税計(8%対象)
        (sum(case when T05.TAX_RATE = 8 then T05.TOTAL_AMOUNT else 0 end) + sum(case when T05.TAX_RATE = 8 then T05.TOTAL_AMOUNT * 0.08 else 0 end)) AS TOTAL_PAYMENT_8 : Decimal(20, 6), // 税込支払金額(8%対象)

        sum(case when T05.TAX_RATE = 10 then T05.TOTAL_AMOUNT else 0 end) AS TOTAL_AMOUNT_10 : Decimal(20, 6), // 仕入金額計(10%対象)
        sum(case when T05.TAX_RATE = 10 then T05.TOTAL_AMOUNT * 0.10 else 0 end) AS TAX_AMOUNT_10 : Decimal(20, 6), // 消費税計(10%対象)
        (sum(case when T05.TAX_RATE = 10 then T05.TOTAL_AMOUNT else 0 end) + sum(case when T05.TAX_RATE = 10 then T05.TOTAL_AMOUNT * 0.10 else 0 end)) AS TOTAL_PAYMENT_10 : Decimal(20, 6), // 税込支払金額(10%対象)

        sum(case when T05.TAX_RATE not in (8, 10) then T05.TOTAL_AMOUNT else 0 end) AS EXCLUDED_AMOUNT : Decimal(20, 6), // 対象外金額
        
        // 总合计字段
        (   
            (sum(case when T05.TAX_RATE = 8 then T05.TOTAL_AMOUNT else 0 end) +
            sum(case when T05.TAX_RATE = 8 then T05.TOTAL_AMOUNT * 0.08 else 0 end)) + // TOTAL_PAYMENT_8
            
            (sum(case when T05.TAX_RATE = 10 then T05.TOTAL_AMOUNT else 0 end) +
            sum(case when T05.TAX_RATE = 10 then T05.TOTAL_AMOUNT * 0.10 else 0 end)) + // TOTAL_PAYMENT_10
            
            sum(case when T05.TAX_RATE not in (8, 10) then T05.TOTAL_AMOUNT else 0 end) // EXCLUDED_AMOUNT
        ) AS TOTAL_SUM : Decimal(20, 6) // 総合計
    }

    action PCH04_SENDEMAIL(parms : String) returns String;

}

annotate TableService.PCH_T04_PAYMENT with {
    SEND_FLAG @(Common: {ValueList: {entity: 'PCH04_STATUS_POP1', }}); 
};
