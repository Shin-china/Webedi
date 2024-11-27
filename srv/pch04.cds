using { TableService as view } from './table';
using { SYS } from '../db/model-sys';
using { PCH } from '../db/model-pch';
using { MST } from '../db/model-mst';

// MM-029 支払通知照会

extend service TableService {

    entity PCH_T04_PAYMENT as
        select from PCH.T02_PO_D as T02
        left join PCH.T05_PAYMENT_D as T05
            on (
                T02.PO_NO = T05.PO_NO
                and T02.D_NO = T05.D_NO
            )
        left join PCH.T01_PO_H AS T01
            on (
                T01.PO_NO = T05.PO_NO
            )
        left join PCH.T04_PAYMENT_H as T04
            on (
                T05.INV_NO = T04.INV_NO
                and T05.GL_YEAR = T04.GL_YEAR
            )
        left join MST.T03_SAP_BP as M03
            on (
                T04.SUPPLIER = M03.BP_ID
            )
        left join MST.T05_SAP_BP_PURCHASE AS M04
            on (
                M04.SUPPLIER = T01.SUPPLIER
                and M04.PURCHASE_ORG = T01.PO_BUKRS
            )

    {
        KEY T02.PO_NO,                     // UMC発注番号
        KEY T02.D_NO,                      // 明細番号 
        KEY T04.INV_NO,                    // 发票号
        T01.SUPPLIER,                      // 仕入先
        T01.PO_BUKRS,                      // 会社コード
        T04.SUPPLIER_DESCRIPTION,          // 業者名
        T05.MAT_ID,                        // 品目コード
        T05.MAT_DESC,                      // 品目名称
        T05.GR_DATE,                       // 入荷日
        T05.QUANTITY,                      // 仕入単位数
        T05.TAX_RATE,                      // INV税率
        T05.PO_TRACK_NO,                   // 備考
        T04.INV_BASE_DATE,                 // 支払日
        T05.SHKZG,
        T04.SEND_FLAG,                     // ステータス
        T04.INV_POST_DATE,                 // 検収月
        T05.CURRENCY,                      // INV通貨コード
        T04.EXCHANGE,                      // 換算レート
        T02.SUPPLIER_MAT,                  // 仕入先品目コード
        M03.LOG_NO,                        // 登録番号    
        M04.ZABC,                          // ABC区分
        T05.Company_Code,
        T05.UNIT_PRICE,
        // T02.PO_NO || REPEAT('0', 5 - LENGTH(CAST(T02.D_NO AS String))) || CAST(T02.D_NO AS String) as NO_DETAILS : String(15), // 発注\明細NO
        T02.PO_NO || T02.D_NO as NO_DETAILS : String(15), // 購買伝票\明細NO	
        TO_CHAR(T04.INV_POST_DATE, 'YYYYMM') as INV_MONTH : String,  //检收月

        CASE 
            WHEN T05.SHKZG = 'S' THEN T05.PRICE_AMOUNT    // 借方为正
            WHEN T05.SHKZG = 'H' THEN -T05.PRICE_AMOUNT   // 贷方为负
            ELSE T05.PRICE_AMOUNT                         // 默认情况
        END as PRICE_AMOUNT : Decimal(18, 3),             // 本体金額

        CASE 
            WHEN T05.SHKZG = 'S' THEN T05.TOTAL_AMOUNT    // 借方为正
            WHEN T05.SHKZG = 'H' THEN -T05.TOTAL_AMOUNT   // 贷方为负
            ELSE T05.TOTAL_AMOUNT                         // 默认情况
        END as TOTAL_AMOUNT : Decimal(18, 3)   

         }

      entity PCH_T04_PAYMENT_UNIT as
        select from PCH_T04_PAYMENT as 

    ![distinct] {
        KEY PO_NO,                     // UMC発注番号
        KEY D_NO,                      // 明細番号 
        KEY INV_NO,                    // 发票号
        INV_MONTH,                 //检收月
        TAX_RATE,                      // INV税率
        @title: '{i18n>PCH04_UNIT_PRICE}'
        UNIT_PRICE,
        EXCHANGE,                      // 換算レート
        SUPPLIER,                      // 仕入先
        PO_BUKRS,                        // 会社コード
        SUPPLIER_DESCRIPTION,          // 業者名
        MAT_ID,                        // 品目コード
        MAT_DESC,                      // 品目名称
        GR_DATE,                       // 入荷日
        @title: '{i18n>PCH04_QUANTITY}'
        QUANTITY,                      // 仕入単位数
        @title: '{i18n>PCH04_PO_TRACK_NO}'
        PO_TRACK_NO,                   // 備考
        INV_BASE_DATE,                 // 支払日
        SEND_FLAG,                     // ステータス
        INV_POST_DATE,                 // 検収月
        CURRENCY,                      // INV通貨コード
        SUPPLIER_MAT,                  // 仕入先品目コード
        LOG_NO,                        // 登録番号    
        ZABC,                          // ABC区分
        SHKZG,
        NO_DETAILS,
        PRICE_AMOUNT,
        TOTAL_AMOUNT,
        Company_Code,
        SUPPLIER || INV_MONTH  as DOWNLOADID      : String(100),
 
        CAST(UNIT_PRICE * COALESCE(EXCHANGE, 1) AS Decimal(18, 5)) AS UNIT_PRICE_IN_YEN : Decimal(18, 5),
        ROUND(TOTAL_AMOUNT * COALESCE(EXCHANGE, 1), 0) AS TOTAL_AMOUNT_IN_YEN : Decimal(20, 0), // 円換算後税込金額（檢収）

        
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
        KEY SUPPLIER,
        KEY INV_NO,
        INV_MONTH,
        NO_DETAILS,            // UMC発注番号
        TAX_RATE,                  // INV税率
        MAT_ID,                    // 品目コード
        GR_DATE,                   // 入荷日
        QUANTITY,                  // 仕入単位数
        UNIT_PRICE,                // 单价
        INV_BASE_DATE,             // 支払日
        PRICE_AMOUNT,              //要合计值
        EXCHANGE,                  // 換算レート
        MAT_DESC,                  // 品目名称
        PO_TRACK_NO,               // 備考
        UNIT_PRICE_IN_YEN,
        TOTAL_AMOUNT_IN_YEN,
        CURRENCY,
        Company_Code,
        case 
            when CURRENCY = 'JPY' then cast(PRICE_AMOUNT as Decimal(18,3))
            when CURRENCY in ('USD', 'EUR') then cast(floor(PRICE_AMOUNT * EXCHANGE) as Decimal(18,0))
        end as BASE_AMOUNT_EXCLUDING_TAX : Decimal(18,3),   //基準通貨金額税抜
    }

 entity PCH_T04_PAYMENT_SUM_FZ1 as
        select from PCH_T04_PAYMENT_UNIT as 

    ![distinct] {
        KEY SUPPLIER,
        KEY INV_MONTH,
       SUM(PRICE_AMOUNT)  as TOTAL_PRICE_AMOUNT_8: Decimal(18, 3),       // 仕入金額計(8%対象)
    }
    where TAX_RATE= 8
      and SUPPLIER is not null and SUPPLIER <> 'N/A'
    group BY SUPPLIER,INV_MONTH;

 entity PCH_T04_PAYMENT_SUM_FZ2 as
        select from PCH_T04_PAYMENT_SUM as 

    ![distinct] {
         KEY SUPPLIER,
         KEY INV_MONTH,
       SUM(PRICE_AMOUNT)  as TOTAL_PRICE_AMOUNT_10: Decimal(18, 3),       // 仕入金額計(10%対象)
    }
    where TAX_RATE= 10
      and SUPPLIER is not null and SUPPLIER <> 'N/A'
    group BY SUPPLIER,INV_MONTH;

 entity PCH_T04_PAYMENT_SUM_FZ3_1 as
        select from PCH_T04_PAYMENT_SUM as 

    ![distinct] {
         KEY SUPPLIER,
         key TAX_RATE,
         KEY INV_MONTH,
         SUM(PRICE_AMOUNT)  as TOTAL_PRICE_AMOUNT_NOT: Decimal(18, 3) ,    // 非810
    }
    where TAX_RATE != 10 AND TAX_RATE != 8 
      and SUPPLIER is not null and SUPPLIER <> 'N/A'
      and TAX_RATE is not null and SUPPLIER <> 'N/A'
    group BY SUPPLIER,TAX_RATE,INV_MONTH;

 entity PCH_T04_PAYMENT_SUM_FZ3 as
        select from PCH_T04_PAYMENT_SUM_FZ3_1 as 

    ![distinct] {
         KEY SUPPLIER,
         KEY INV_MONTH,
        SUM(TOTAL_PRICE_AMOUNT_NOT * TAX_RATE)  as TOTAL_PRICE_AMOUNT_NOT: Decimal(18, 3),       // 非810
    }
    where TAX_RATE != 10 AND TAX_RATE != 8 
      and SUPPLIER is not null and SUPPLIER <> 'N/A'
      and TAX_RATE is not null and SUPPLIER <> 'N/A'
    group BY SUPPLIER,INV_MONTH;

 entity PCH_T04_PAYMENT_SUM_HJ1 as
        select from PCH_T04_PAYMENT_UNIT t1
        left join PCH_T04_PAYMENT_SUM_FZ1 t2
        on t1.SUPPLIER = t2.SUPPLIER
        and t1.INV_MONTH = t2.INV_MONTH
        left join PCH_T04_PAYMENT_SUM_FZ2 t3
        on t1.SUPPLIER = t3.SUPPLIER 
        and t1.INV_MONTH = t3.INV_MONTH
        left join PCH_T04_PAYMENT_SUM_FZ3 t4
        on t1.SUPPLIER = t4.SUPPLIER
        and t1.INV_MONTH = t4.INV_MONTH
                                
    distinct {
        KEY t1.SUPPLIER,
        KEY t1.INV_NO,
        KEY t1.INV_MONTH,
        COALESCE(t2.TOTAL_PRICE_AMOUNT_8, 0)   as TOTAL_PRICE_AMOUNT_8: Decimal(18, 3),
        COALESCE(t3.TOTAL_PRICE_AMOUNT_10, 0)  as TOTAL_PRICE_AMOUNT_10: Decimal(18, 3),
        COALESCE(t4.TOTAL_PRICE_AMOUNT_NOT, 0) as TOTAL_PRICE_AMOUNT_NOT: Decimal(18, 3),
 
    }
    entity PCH_T04_PAYMENT_SUM_HJ2 as
        select from PCH_T04_PAYMENT_SUM_HJ1 t1
      
                                
    distinct {
        KEY t1.SUPPLIER,
        KEY t1.INV_NO,
        KEY t1.INV_MONTH,
        TOTAL_PRICE_AMOUNT_8,
        FLOOR(TOTAL_PRICE_AMOUNT_8 * 0.08) AS CONSUMPTION_TAX_8: Decimal(15, 0),  // 计算消費税計(8%対象)
        TOTAL_PRICE_AMOUNT_10,
        FLOOR(TOTAL_PRICE_AMOUNT_10 * 0.10) AS CONSUMPTION_TAX_10: Decimal(15, 0),  // 计算消費税計(10%対象)
        TOTAL_PRICE_AMOUNT_NOT,
    }
    entity PCH_T04_PAYMENT_SUM_HJ3 as
        select from PCH_T04_PAYMENT_SUM_HJ2 t1
                                      
    distinct {
        KEY t1.SUPPLIER,
        KEY t1.INV_NO,
        KEY t1.INV_MONTH,
        TOTAL_PRICE_AMOUNT_8,
        CONSUMPTION_TAX_8 ,     // 计算消費税計(8%対象)
        TOTAL_PRICE_AMOUNT_10,
        CONSUMPTION_TAX_10 ,    // 计算消費税計(10%対象)
        TOTAL_PRICE_AMOUNT_NOT,

    }

    entity PCH_T04_PAYMENT_SUM_HJ4 as
        select from PCH_T04_PAYMENT_SUM_HJ3 t1     
                                
    distinct {
        KEY t1.SUPPLIER,
        KEY t1.INV_NO,
        KEY t1.INV_MONTH,
        TOTAL_PRICE_AMOUNT_8,
        CONSUMPTION_TAX_8 ,     // 计算消費税計(8%対象)
        TOTAL_PRICE_AMOUNT_10,
        CONSUMPTION_TAX_10 ,    // 计算消費税計(10%対象)
        TOTAL_PRICE_AMOUNT_NOT,
        CONSUMPTION_TAX_8 + TOTAL_PRICE_AMOUNT_8  as TOTAL_PAYMENT_AMOUNT_8_END: Decimal(18, 3),       // 计算税込支払金額(8%対象)
        CONSUMPTION_TAX_10 + TOTAL_PRICE_AMOUNT_10 as TOTAL_PAYMENT_AMOUNT_10_END: Decimal(18, 3),      // 计算税込支払金額(10%対象)
    }

        entity PCH_T04_PAYMENT_SUM_HJ5 as
        select from PCH_T04_PAYMENT_SUM_HJ4 t1   
                                
    distinct {
        KEY t1.SUPPLIER,
        KEY t1.INV_NO,
        KEY t1.INV_MONTH,
        TOTAL_PRICE_AMOUNT_8,
        CONSUMPTION_TAX_8 ,               // 计算消費税計(8%対象)
        TOTAL_PRICE_AMOUNT_10,
        CONSUMPTION_TAX_10 ,              // 计算消費税計(10%対象)
        TOTAL_PRICE_AMOUNT_NOT,
        TOTAL_PAYMENT_AMOUNT_8_END,       // 计算税込支払金額(8%対象)
        TOTAL_PAYMENT_AMOUNT_10_END,      // 计算税込支払金額(10%対象)
        TOTAL_PAYMENT_AMOUNT_8_END + TOTAL_PAYMENT_AMOUNT_10_END + TOTAL_PRICE_AMOUNT_NOT as TOTAL_PAYMENT_AMOUNT_FINAL : Decimal(18, 3),     // 総合計

    }

     entity PCH_T04_PAYMENT_SUM_HJ6 as
        select from PCH_T04_PAYMENT_SUM_HJ5 t1
        left join PCH_T04_PAYMENT_SUM t2
        on t1.SUPPLIER = t2.SUPPLIER
        and t1.INV_NO = t2.INV_NO
        and t1.INV_MONTH = t2.INV_MONTH
        and t2.SUPPLIER is not null
        left join PCH_T04_PAYMENT_UNIT t3
        on t1.SUPPLIER = t3.SUPPLIER
        and t1.INV_NO = t3.INV_NO
        AND t1.INV_MONTH = t3.INV_MONTH
        and t3.SUPPLIER is not null
                                
    distinct {
        KEY t1.SUPPLIER,
        KEY t1.INV_NO,
        KEY t3.DOWNLOADID,
        t3.INV_MONTH,                        // 検収月
        TOTAL_PRICE_AMOUNT_8,             // 仕入金額計(8%対象)
        CONSUMPTION_TAX_8 ,               // 计算消費税計(8%対象)
        TOTAL_PAYMENT_AMOUNT_8_END,       // 计算税込支払金額(8%対象)
        TOTAL_PRICE_AMOUNT_10,            // 仕入金額計(10%対象)
        CONSUMPTION_TAX_10 ,              // 计算消費税計(10%対象)
        TOTAL_PAYMENT_AMOUNT_10_END,      // 计算税込支払金額(10%対象)
        TOTAL_PRICE_AMOUNT_NOT,           // 対象外金額
        TOTAL_PAYMENT_AMOUNT_FINAL,       // 総合計
        t2.NO_DETAILS,                       // 発注\明細NO
        t2.MAT_ID,                           // 品目コード
        t2.MAT_DESC,                         // 品目名称
        t2.GR_DATE,                          // 入荷日
        t2.QUANTITY,                         // 仕入単位数
        t2.UNIT_PRICE,                       // 基準通貨単価
        t2.UNIT_PRICE_IN_YEN,                // 基準通貨単価
        t2.BASE_AMOUNT_EXCLUDING_TAX,        // 基準通貨金額税抜
        t2.TAX_RATE,                         // INV税率
        t2.Company_Code,
        t2.PO_TRACK_NO,                      // 備考
        t2.INV_BASE_DATE,                    // 支払日
        t3.LOG_NO,                           // 登録番号


        case 
        when t2.Company_Code = '1400' then 'ＵＭＣ・Ｈエレクトロニクス株式会社'
        when t2.Company_Code = '1100' then 'ユー・エム・シー・エレクトロニクス株式会社'
        else null
        end as Company_Name : String(50),

        // CONCAT(
        //     SUBSTRING(t3.INV_MONTH, 1, 4),  // 提取年份
        //     '年', 
        //     SUBSTRING(t3.INV_MONTH, 5, 2)   // 提取月份
        // ) as INV_MONTH_FORMATTED : String,

        SUBSTRING(t3.INV_MONTH, 1, 4) || '年' || SUBSTRING(t3.INV_MONTH, 5, 2) AS INV_MONTH_FORMATTED : String,


        // concat(
        //     concat(
        //         substring(cast(current_date as String), 1, 4), '/'
        //     ),
        //     concat(
        //         substring(cast(current_date as String), 6, 2), '/'
        //     )
        //     ) || substring(cast(current_date as String), 9, 2) as CURRENT_DAY : String, // 発行日

        substring(cast(current_date as String), 1, 4) || '/' ||
        substring(cast(current_date as String), 6, 2) || '/' ||
        substring(cast(current_date as String), 9, 2) as CURRENT_DAY : String,


        t3.SUPPLIER_DESCRIPTION,             // 仕入先名称

    }

    action PCH04_SENDEMAIL(parms : String) returns String;
    action PCH04_EXCELDOWNLOAD(parms : String) returns LargeBinary;

}
annotate TableService.PCH_T04_PAYMENT_UNIT with {
  INV_MONTH @(Common : {FieldControl : #Mandatory});
};
annotate TableService.PCH_T04_PAYMENT with {
    SEND_FLAG @(Common: {ValueList: {entity: 'PCH04_STATUS_POP1', }}); 
};
