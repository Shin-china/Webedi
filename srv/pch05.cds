using {TableService as view} from './table';
using {PCH} from '../db/model-pch';
using {MST} from '../db/model-mst';
using {SYS} from '../db/model-sys';

// MM-005 買掛金明細

extend service TableService {

    entity PCH_T05_ACCOUNT_DETAIL as
        select from PCH.T05_PAYMENT_D as T05
        left join PCH.T04_PAYMENT_H as T04
            on T05.INV_NO = T04.INV_NO
        left join PCH.T01_PO_H as T01
            on T05.PO_NO = T01.PO_NO
        left join MST.T03_SAP_BP as T03
            on T04.SUPPLIER = T03.BP_ID
        left join SYS.T08_COM_OP_D as T08
            on T08.H_CODE = 'MM0011'
            
        distinct {
            key T05.INV_NO,                        // 伝票番号
            key T05.GL_YEAR,                       // 会計年度
            key T05.PO_NO,                         // 購買伝票
            key T05.D_NO,                          // 明細
            key T04.SUPPLIER,                      // 仕入先
                T01.PO_BUKRS,                      // 会社コード
                T05.ITEM_NO,                       // 請求書明細
                T04.SUPPLIER_DESCRIPTION,          // 仕入先名称
                T04.INV_DATE,                      // インボイス
                T05.PO_TRACK_NO,                   // 購買依頼追跡番号
                T05.PR_BY,                         // 購買依頼者
                T05.GL_ACCOUNT,                    // G/L勘定
                T05.COST_CENTER,                   // 原価センター
                T05.PURCHASE_GROUP,                // 購買グループ
                T05.PURCHASE_GROUP_DESC,           // 購買グループテキスト
                T05.CURRENCY,                      // 通貨コード
                T05.MAT_ID,                        // 品目
                T05.QUANTITY,                      // 数量
                T05.UNIT,                          // 発注数量単位
                T05.MAT_DESC,                      // 品目テキスト
                T05.TAX_CODE,                      // 税コード
                T05.TAX_RATE,                      // 税率
                T04.INV_POST_DATE,                 // 転記日付
                T04.SEND_FLAG,                     // 送信ステータス
                // T05.UNIT_PRICE,                    // 単価
                CASE 
                    WHEN T05.CURRENCY = 'JYP' THEN CAST(T05.UNIT_PRICE AS Decimal(15, 3))  // 保留三位小数
                    ELSE CAST(T05.UNIT_PRICE AS Decimal(15, 5))  // 保留五位小数
                END AS UNIT_PRICE : Decimal(15, 5),

                T05.TAX_AMOUNT,                    // 消費税額
                T04.CE_DOC,                        // 差額伝票番号
                T04.INV_BASE_DATE,                 // 支払い基準日
                T05.GR_DATE,                       // 伝票日付
                T08.VALUE01,
                T03.LOG_NO,                        // 登録番号
                T03.POSTCODE,                      // 郵便番号
                T03.PLACE_NAME || '' || T03.REGIONS as ADRESS : String(255), // 仕入先のアドレス    
                T05.PO_NO || REPEAT('0', 5 - LENGTH(CAST(T05.D_NO AS String))) || CAST(T05.D_NO AS String) as NO_DETAILS : String(15), // 注番
                T05.SHKZG,                         // 借方/貸方フラグ

                CASE 
                    WHEN T05.SHKZG = 'S' THEN T05.PRICE_AMOUNT    // 借方为正
                    WHEN T05.SHKZG = 'H' THEN -T05.PRICE_AMOUNT   // 贷方为负
                    ELSE T05.PRICE_AMOUNT                         // 默认情况
                END as PRICE_AMOUNT : Decimal(18, 3),             // 本体金額

                CASE 
                    WHEN T05.SHKZG = 'S' THEN T05.TOTAL_AMOUNT    // 借方为正
                    WHEN T05.SHKZG = 'H' THEN -T05.TOTAL_AMOUNT   // 贷方为负
                    ELSE T05.TOTAL_AMOUNT                         // 默认情况
                END as TOTAL_AMOUNT : Decimal(18, 3),             // 計上金額

                TO_CHAR(T04.INV_POST_DATE, 'YYYYMM') as INV_MONTH : String
      
         }         
          entity PCH_T05_ACCOUNT_DETAIL_SUM as
        select from PCH_T05_ACCOUNT_DETAIL as T05

        distinct {
            key T05.INV_NO,                        // 伝票番号
            key T05.GL_YEAR,                       // 会計年度
            key T05.PO_NO,                         // 購買伝票
            key T05.D_NO,                          // 明細
            key T05.SUPPLIER,                      // 仕入先
                T05.ITEM_NO,                       // 請求書明細
                T05.PO_BUKRS,                      // 会社コード
                T05.SUPPLIER_DESCRIPTION,          // 仕入先名称
                T05.INV_DATE,                      // インボイス
                T05.PO_TRACK_NO,                   // 購買依頼追跡番号
                T05.PR_BY,                         // 購買依頼者
                T05.GL_ACCOUNT,                    // G/L勘定
                T05.COST_CENTER,                   // 原価センター
                T05.PURCHASE_GROUP,                // 購買グループ
                T05.PURCHASE_GROUP_DESC,           // 購買グループテキスト
                T05.CURRENCY,                      // 通貨コード
                T05.MAT_ID,                        // 品目
                T05.QUANTITY,                      // 数量
                T05.UNIT,                          // 発注数量単位
                T05.MAT_DESC,                      // 品目テキスト
                T05.TAX_CODE,                      // 税コード
                T05.TAX_RATE,                      // 税率
                T05.INV_POST_DATE,                 // 転記日付
                T05.SEND_FLAG,                     // 送信ステータス
                T05.UNIT_PRICE,                    // 単価
                T05.PRICE_AMOUNT,                  // 本体金額
                T05.TAX_AMOUNT,                    // 消費税額
                T05.TOTAL_AMOUNT,                  // 計上金額
                T05.INV_BASE_DATE,                 // 支払い基準日
                T05.GR_DATE,                       // 伝票日付
                T05.LOG_NO,                        // 登録番号
                T05.INV_MONTH,                     // 提取年月，作为月度字段
                VALUE01,
                POSTCODE,
                ADRESS,
                NO_DETAILS,
            // 新增计算字段
            case when T05.TAX_RATE = 10 then 
                cast(T05.PRICE_AMOUNT as Decimal(15,2)) 
            end as CALC_10_PRICE_AMOUNT : Decimal(15, 2),  // 10% 税抜金额
            
            case when T05.TAX_RATE = 8 then 
                cast(T05.PRICE_AMOUNT as Decimal(15,2)) 
            end as CALC_8_PRICE_AMOUNT : Decimal(15, 2),   // 8% 税抜金额

            // SAP 税额
            case when T05.TAX_RATE = 10 then 
                cast(T05.TAX_AMOUNT as Decimal(15,2)) 
            end as SAP_TAX_AMOUNT_10 : Decimal(15, 2),   // 10% SAP 税额
            
            case when T05.TAX_RATE = 8 then 
                cast(T05.TAX_AMOUNT as Decimal(15,2)) 
            end as SAP_TAX_AMOUNT_8 : Decimal(15, 2),    // 8% SAP 税额

            case when T05.TAX_RATE = 10 then 
                cast(T05.TOTAL_AMOUNT as Decimal(15,2)) 
            end as TOTAL_AMOUNT_10 : Decimal(15, 2),   
            
            case when T05.TAX_RATE = 8 then 
                cast(T05.TOTAL_AMOUNT as Decimal(15,2)) 
            end as TOTAL_AMOUNT_8 : Decimal(15, 2),   

        }

        entity PCH_T05_PRICE_AMOUNT_SUM as

        select from PCH_T05_ACCOUNT_DETAIL_SUM as T01

        distinct {      
            key T01.SUPPLIER,  
            ROUND(SUM(CALC_8_PRICE_AMOUNT), 2) as CALC_8_PRICE_AMOUNT_TOTAL : Decimal(15, 2),
            ROUND(SUM(CALC_10_PRICE_AMOUNT), 2) as CALC_10_PRICE_AMOUNT_TOTAL : Decimal(15, 2),
            ROUND(SUM(SAP_TAX_AMOUNT_8), 2) as SAP_TAX_AMOUNT_8_TOTAL : Decimal(15, 2),
            ROUND(SUM(SAP_TAX_AMOUNT_10), 2) as SAP_TAX_AMOUNT_10_TOTAL : Decimal(15, 2),
            ROUND(SUM(TOTAL_AMOUNT_8), 2) as TOTAL_AMOUNT_8_TOTAL : Decimal(15, 2),
            ROUND(SUM(TOTAL_AMOUNT_10), 2) as TOTAL_AMOUNT_10_TOTAL : Decimal(15, 2),     
            ROUND(SUM(PRICE_AMOUNT), 2) as PRICE_AMOUNT_TOTAL : Decimal(15, 2),
        }
        group by
            T01.SUPPLIER;  
    
    entity PCH_T05_ACCOUNT_DETAIL_SUM_GRO as

        select from   PCH_T05_ACCOUNT_DETAIL_SUM  as T01

        distinct {      
            key T01.PO_BUKRS,  
            key T01.SUPPLIER,
            key T01.INV_MONTH,   
            SUM(CALC_10_PRICE_AMOUNT) as CALC_10_PRICE_AMOUNT: Decimal(15, 2),                      // 10% 税抜金额
            SUM(CALC_8_PRICE_AMOUNT) as CALC_8_PRICE_AMOUNT: Decimal(15, 2),                        // 8%  税抜金额
            SUM(SAP_TAX_AMOUNT_10) as SAP_TAX_AMOUNT_10: Decimal(15, 2),                        // 10% SAP税额
            SUM(SAP_TAX_AMOUNT_8) as SAP_TAX_AMOUNT_8: Decimal(15, 2),                          // 8%  SAP税额
        }
        group by
            T01.PO_BUKRS,
            T01.SUPPLIER,
            T01.INV_MONTH;     

    entity PCH_T05_ACCOUNT_DETAIL_SUM_END as

        select from   PCH_T05_ACCOUNT_DETAIL_SUM_GRO  as T02
        left join PCH_T05_ACCOUNT_DETAIL as T03
        on T02.SUPPLIER = T03.SUPPLIER
        and T02.INV_MONTH = T03.INV_MONTH
        and T02.PO_BUKRS = T03.PO_BUKRS

        distinct {   
            key T02.SUPPLIER,  
            key T03.INV_MONTH,
            key T03.PO_BUKRS,     
            key T03.INV_NO,
            T03.PO_NO,
            T03.D_NO,                     
            T03.CURRENCY,
            T03.TAX_RATE,
            T03.TAX_CODE,
            T03.GR_DATE,
            T03.VALUE01,   
            T03.POSTCODE,
            T03.ADRESS,   
            T03.NO_DETAILS,
            T03.LOG_NO,
            T03.SUPPLIER_DESCRIPTION,
            T03.MAT_ID,
            T03.QUANTITY,
            T03.UNIT,
            T03.UNIT_PRICE,
            T03.PRICE_AMOUNT,
            T03.INV_POST_DATE,
            T03.MAT_DESC,
            T02.CALC_10_PRICE_AMOUNT,  
            T02.CALC_8_PRICE_AMOUNT,   
            T02.SAP_TAX_AMOUNT_10,
            T02.SAP_TAX_AMOUNT_8,  
            // 再计算的税额（根据 CURRENCY 处理小数点后位数）
            case 
                when T03.CURRENCY = 'JPY' and T03.TAX_RATE = 10 then 
                    cast(floor(CASE WHEN T03.TAX_RATE = 10 THEN T02.CALC_10_PRICE_AMOUNT END * 0.10) as Decimal(15,0))
                when T03.CURRENCY in ('USD', 'EUR') and T03.TAX_RATE = 10 then 
                    cast(floor(CASE WHEN T03.TAX_RATE = 10 THEN T02.CALC_10_PRICE_AMOUNT END * 0.10 * 100) / 100 as Decimal(15,2))
            else 
            null // 其他情况返回 NULL
            end as RECALC_PRICE_AMOUNT_10 : Decimal(15, 2),  // 再计算 10% 税额
            
            case 
                when T03.CURRENCY = 'JPY' and T03.TAX_RATE = 8 then 
                    cast(floor(CASE WHEN T03.TAX_RATE = 8 THEN T02.CALC_8_PRICE_AMOUNT END * 0.08) as Decimal(15,0))
                when T03.CURRENCY in ('USD', 'EUR') and T03.TAX_RATE = 8 then 
                    cast(floor(CASE WHEN T03.TAX_RATE = 8 THEN T02.CALC_8_PRICE_AMOUNT END * 0.08 * 100) / 100 as Decimal(15,2))
            else 
            null // 其他情况返回 NULL
            end as RECALC_PRICE_AMOUNT_8 : Decimal(15, 2),   // 再计算 8% 税额

        }

        entity PCH_T05_ACCOUNT_DETAIL_SUM_FINAL as
        select from   PCH_T05_ACCOUNT_DETAIL_SUM_END  as T03

        distinct {    
            key T03.SUPPLIER, 
            key T03.INV_NO,
            T03.PO_NO,
            T03.D_NO,       
            T03.PO_BUKRS,                       
            T03.INV_MONTH,
            T03.CURRENCY,
            T03.TAX_RATE,
            TAX_CODE,
            GR_DATE,
            CALC_10_PRICE_AMOUNT,  
            CALC_8_PRICE_AMOUNT,   
            SAP_TAX_AMOUNT_10,
            SAP_TAX_AMOUNT_8,  
            VALUE01, 
            POSTCODE,
            ADRESS,     
            NO_DETAILS,
            LOG_NO,
            SUPPLIER_DESCRIPTION,
            MAT_ID,
            QUANTITY,
            UNIT,
            UNIT_PRICE,
            PRICE_AMOUNT,
            INV_POST_DATE,
            MAT_DESC,

            COALESCE(CALC_8_PRICE_AMOUNT, 0) + COALESCE(CALC_10_PRICE_AMOUNT, 0) as TOTAL_PRICE_AMOUNT : Decimal(15, 3),
            COALESCE(RECALC_PRICE_AMOUNT_8, 0) + COALESCE(RECALC_PRICE_AMOUNT_10, 0) as TOTAL_RECALC_PRICE_AMOUNT : Decimal(15, 3),

            // 再计算的税额（根据 CURRENCY 处理小数点后位数）
            case 
                when T03.CURRENCY = 'JPY' and T03.TAX_RATE = 10 then 
                    cast(floor(CASE WHEN T03.TAX_RATE = 10 THEN T03.CALC_10_PRICE_AMOUNT END * 0.10) as Decimal(15,0))
                when T03.CURRENCY in ('USD', 'EUR') and T03.TAX_RATE = 10 then 
                    cast(floor(CASE WHEN T03.TAX_RATE = 10 THEN T03.CALC_10_PRICE_AMOUNT END * 0.10 * 100) / 100 as Decimal(15,2))
            end as RECALC_PRICE_AMOUNT_10 : Decimal(15, 2),  // 再计算 10% 税额
            
            case 
                when T03.CURRENCY = 'JPY' and T03.TAX_RATE = 8 then 
                    cast(floor(CASE WHEN T03.TAX_RATE = 8 THEN T03.CALC_8_PRICE_AMOUNT END * 0.08) as Decimal(15,0))
                when T03.CURRENCY in ('USD', 'EUR') and T03.TAX_RATE = 8 then 
                    cast(floor(CASE WHEN T03.TAX_RATE = 8 THEN T03.CALC_8_PRICE_AMOUNT END * 0.08 * 100) / 100 as Decimal(15,2))
            end as RECALC_PRICE_AMOUNT_8 : Decimal(15, 2),   // 再计算 8% 税额

          // 消费税差额
        case 
            when T03.TAX_RATE = 10 then 
                COALESCE(RECALC_PRICE_AMOUNT_10, 0) - COALESCE(T03.SAP_TAX_AMOUNT_10, 0)
        end as DIFF_TAX_AMOUNT_10 : Decimal(15, 2), 

        case 
            when T03.TAX_RATE = 8 then 
                COALESCE(RECALC_PRICE_AMOUNT_8, 0) - COALESCE(T03.SAP_TAX_AMOUNT_8, 0)
        end as DIFF_TAX_AMOUNT_8 : Decimal(15, 2),

        // 合计金额
        case 
            when T03.TAX_RATE = 10 then 
                COALESCE(T03.CALC_10_PRICE_AMOUNT, 0) + COALESCE(RECALC_PRICE_AMOUNT_10, 0)
        end as TOTAL_10_TAX_INCLUDED_AMOUNT : Decimal(15, 2), 

        case 
            when T03.TAX_RATE = 8 then 
                COALESCE(T03.CALC_8_PRICE_AMOUNT, 0) + COALESCE(RECALC_PRICE_AMOUNT_8, 0)
        end as TOTAL_8_TAX_INCLUDED_AMOUNT : Decimal(15, 2),


        }

         entity PCH_T05_ACCOUNT_DETAIL_EXCEL as

        select from   PCH_T05_ACCOUNT_DETAIL_SUM_FINAL as T01
        left join PCH_T05_PRICE_AMOUNT_SUM as T02
        on T01.SUPPLIER = T02.SUPPLIER


        distinct {
            key T01.INV_NO,                     
            key T01.PO_NO,
            key T01.D_NO,      
            key T01.SUPPLIER,   
            T01.PO_BUKRS,                                             
            T01.INV_MONTH,
            T01.CURRENCY,
            T01.TAX_RATE,
            T01.TAX_CODE,  
            T01.GR_DATE,
            T01.CALC_10_PRICE_AMOUNT,  
            T01.CALC_8_PRICE_AMOUNT,   
            T01.SAP_TAX_AMOUNT_10,
            T01.SAP_TAX_AMOUNT_8,  
            T01.DIFF_TAX_AMOUNT_10,
            T01.DIFF_TAX_AMOUNT_8,
            T01.RECALC_PRICE_AMOUNT_8,
            T01.RECALC_PRICE_AMOUNT_10,
            T01.TOTAL_8_TAX_INCLUDED_AMOUNT,
            T01.TOTAL_10_TAX_INCLUDED_AMOUNT,
            T01.TOTAL_PRICE_AMOUNT,
            T01.TOTAL_RECALC_PRICE_AMOUNT,
            T01.VALUE01,
            T01.POSTCODE,
            T01.ADRESS,
            T01.NO_DETAILS,
            T01.LOG_NO,
            T01.SUPPLIER_DESCRIPTION,
            T01.MAT_ID,
            T01.QUANTITY,
            T01.UNIT,
            T01.UNIT_PRICE,
            T01.PRICE_AMOUNT,
            T01.INV_POST_DATE,
            T01.MAT_DESC,
            T02.PRICE_AMOUNT_TOTAL,
            T02.TOTAL_AMOUNT_8_TOTAL,
            T02.TOTAL_AMOUNT_10_TOTAL,
            T02.SAP_TAX_AMOUNT_8_TOTAL,
            T02.SAP_TAX_AMOUNT_10_TOTAL,
            T02.CALC_8_PRICE_AMOUNT_TOTAL,
            T02.CALC_10_PRICE_AMOUNT_TOTAL,

            CONCAT(
                SUBSTRING(T01.INV_MONTH, 1, 4),  // 提取年份
                '年', 
                SUBSTRING(T01.INV_MONTH, 5, 2),  // 提取月份
                '月'
            ) as INV_MONTH_FORMATTED : String,

            // 计算每个 SUPPLIER 的条数
            COUNT(*) OVER (PARTITION BY T01.SUPPLIER) as TOTAL_COUNT : Integer, // 按照 SUPPLIER 维度计算条数
            COALESCE(T02.TOTAL_AMOUNT_8_TOTAL, 0) + COALESCE(T02.TOTAL_AMOUNT_10_TOTAL, 0) as TOTAL_TOTAL_AMOUNT : Decimal(15, 2), // 計上金額总合计
            COALESCE(T02.SAP_TAX_AMOUNT_8_TOTAL, 0) + COALESCE(T02.SAP_TAX_AMOUNT_10_TOTAL, 0) as TOTAL_TAX_AMOUNT : Decimal(15, 2), // 消費税額总合计

            case 
                when T01.DIFF_TAX_AMOUNT_10 IS NOT NULL THEN DIFF_TAX_AMOUNT_10
                when T01.DIFF_TAX_AMOUNT_8 IS NOT NULL THEN DIFF_TAX_AMOUNT_8
                else NULL
            end AS DIFF_TAX_AMOUNT :Decimal(15, 2),

            // 计算借方/贷方标志
            case 
                when T01.DIFF_TAX_AMOUNT_10 is null and DIFF_TAX_AMOUNT_8 is null then null 
                when T01.DIFF_TAX_AMOUNT_10 >= 0 or DIFF_TAX_AMOUNT_8 >= 0 then 'S' 
                else 'H' 
            end as SHKZG_FLAG : String,                      // 借方/贷方标志

            // 计算取引
            case 
                when T01.DIFF_TAX_AMOUNT_10 is null and DIFF_TAX_AMOUNT_8 is null then null 
                when T01.DIFF_TAX_AMOUNT_10 >= 0 or DIFF_TAX_AMOUNT_8 >= 0 then 1 
                else 2 
            end as TRANSACTION : String,                 // 取引类型

            case 
                when T01.DIFF_TAX_AMOUNT_10 is not null then cast(floor(DIFF_TAX_AMOUNT_10 / 0.1) as Decimal(15,0)) 
                when T01.DIFF_TAX_AMOUNT_8 is not null then cast(floor(DIFF_TAX_AMOUNT_8 / 0.08) as Decimal(15,0)) 
                else null 
            end as TAX_BASE_AMOUNT : Decimal(15,0), // 税基金额

            ROW_NUMBER() OVER () as INVOICEID: Integer,

    TO_CHAR(
        CAST(
            TO_DATE(CONCAT(
                EXTRACT(YEAR FROM CURRENT_DATE), '-', 
                EXTRACT(MONTH FROM CURRENT_DATE) + 1, '-01'
            ), 'YYYY-MM-DD') - 1 AS Date
        ), 'YYYY/MM/DD'
    ) as LASTDATE : String,

            '' as REFERENCE: String,                         // REFERENCE 字段赋值为 null
            '' as DETAILTEXT: String,                        // DETAILTEXT 字段赋值为 null
            12600000 as ACCOUNT: String,                     // account 字段赋值为 12600000
            'RE' as DOCUMENTTYPE: String,                    // documentType 字段固定值为 'RE'
            'TAX' as HEADERTEXT: String,                      // headertext 字段固定值为 'TAX'
             CONCAT('(', CONCAT(T01.SUPPLIER, ')')) as SUPPLIER_1 : String
                
        }

          entity PCH_T05_ACCOUNT_DETAIL_DISPLAY as

          select from PCH_T05_ACCOUNT_DETAIL_SUM_GRO as T02
        left join PCH_T05_PRICE_AMOUNT_SUM as T01
            on T02.SUPPLIER = T01.SUPPLIER
        left join PCH_T05_ACCOUNT_DETAIL_EXCEL as T03
            on T02.SUPPLIER = T03.SUPPLIER
            and T02.INV_MONTH = T03.INV_MONTH
            and T02.PO_BUKRS = T03.PO_BUKRS

        distinct {      
            key T02.SUPPLIER,  
            key T02.INV_MONTH,   
            key T02.PO_BUKRS,
            T03.CURRENCY,
            T02.CALC_10_PRICE_AMOUNT,                                                  // 10% 税抜金额
            T02.CALC_8_PRICE_AMOUNT,                                                   // 8%  税抜金额
            T02.SAP_TAX_AMOUNT_10,                                                     // 10% SAP税额
            T02.SAP_TAX_AMOUNT_8,                                                      // 8%  SAP税额
            T03.RECALC_PRICE_AMOUNT_10,       // 再計算10％税額
            T03.RECALC_PRICE_AMOUNT_8,        // 再計算8％税額
            T03.DIFF_TAX_AMOUNT_10,           // 10％消費税差額
            T03.DIFF_TAX_AMOUNT_8,            // 8％消費税差額
            T03.TOTAL_10_TAX_INCLUDED_AMOUNT, // 合計10％税込金額
            T03.TOTAL_8_TAX_INCLUDED_AMOUNT,  // 合計8％税込金額
            T03.TRANSACTION,
            T03.REFERENCE,
            T03.DOCUMENTTYPE,
            T03.HEADERTEXT,
            T03.LASTDATE,
            T03.ACCOUNT,
            T03.DETAILTEXT,
            T03.SHKZG_FLAG,
            T03.DIFF_TAX_AMOUNT,
            T03.TAX_CODE,
            T03.TAX_BASE_AMOUNT
        }

}

