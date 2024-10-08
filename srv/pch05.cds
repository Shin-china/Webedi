using {TableService as view} from './table';
using {PCH} from '../db/model-pch';
using {MST} from '../db/model-mst';

// MM-005 買掛金明細

extend service TableService {

    entity PCH_T05_ACCOUNT_DETAIL as
        select from PCH.PCH_T05_PAYMENT_D as T05
        left join PCH.PCH_T04_PAYMENT_H as T04
            on T05.INV_NO = T04.INV_NO
        left join PCH.PCH_T01_PO_H as T01
            on T05.PO_NO = T01.PO_NO
        left join MST.MST_T03_SAP_BP as T03
            on T04.SUPPLIER = T03.LOG_NO
            
        distinct {
            key T05.INV_NO,                        // 伝票番号
            key T05.GL_YEAR,                       // 会計年度
            key T05.ITEM_NO,                       // 請求書明細
                T05.PO_NO,                         // 購買伝票
                T05.D_NO,                          // 明細
                T01.PO_BUKRS,                      // 会社コード
                T04.SUPPLIER,                      // 仕入先
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
                T05.UNIT_PRICE,                    // 単価
                T05.PRICE_AMOUNT,                  // 本体金額
                T05.TAX_AMOUNT,                    // 消費税額
                T05.TOTAL_AMOUNT,                  // 計上金額
                T04.INV_BASE_DATE,                 // 支払い基準日
                T05.GR_DATE,                       // 伝票日付
                T03.LOG_NO,                        // 登録番号
                T05.SHKZG,                         // 借方/貸方フラグ 
                TO_CHAR(T04.INV_POST_DATE, 'YYYYMM') as INV_MONTH : String
      
         }         
          entity PCH_T05_ACCOUNT_DETAIL_SUM as
        select from PCH_T05_ACCOUNT_DETAIL as T05

        distinct {
            key T05.INV_NO,                        // 伝票番号
            key T05.GL_YEAR,                       // 会計年度
            key T05.ITEM_NO,                       // 請求書明細
                T05.PO_NO,                         // 購買伝票
                T05.D_NO,                          // 明細
                T05.PO_BUKRS,                      // 会社コード
                T05.SUPPLIER,                      // 仕入先
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
            // 新增计算字段
            case when T05.TAX_RATE = 10 then 
                cast(T05.PRICE_AMOUNT as Decimal(15,2)) 
            end as CALC_10_TAX_AMOUNT : Decimal(15, 2),  // 10% 税抜金额
            
            case when T05.TAX_RATE = 8 then 
                cast(T05.PRICE_AMOUNT as Decimal(15,2)) 
            end as CALC_8_TAX_AMOUNT : Decimal(15, 2),   // 8% 税抜金额

            // SAP 税额
            case when T05.TAX_RATE = 10 then 
                cast(T05.TAX_AMOUNT as Decimal(15,2)) 
            end as SAP_TAX_AMOUNT_10 : Decimal(15, 2),   // 10% SAP 税额
            
            case when T05.TAX_RATE = 8 then 
                cast(T05.TAX_AMOUNT as Decimal(15,2)) 
            end as SAP_TAX_AMOUNT_8 : Decimal(15, 2),    // 8% SAP 税额

        }
    
    entity PCH_T05_ACCOUNT_DETAIL_SUM_GRO as

        select from   PCH_T05_ACCOUNT_DETAIL_SUM  as T01

        distinct {
            key T01.PO_BUKRS,                       
            key T01.SUPPLIER,                       
            key T01.INV_MONTH,            
            SUM(CALC_10_TAX_AMOUNT) as CALC_10_TAX_AMOUNT: Decimal(15, 2),                      // 10% 税抜金额
            SUM(CALC_8_TAX_AMOUNT) as CALC_8_TAX_AMOUNT: Decimal(15, 2),                        // 8%  税抜金额
            SUM(SAP_TAX_AMOUNT_10) as SAP_TAX_AMOUNT_10: Decimal(15, 2),                        // 10% SAP税额
            SUM(SAP_TAX_AMOUNT_8) as SAP_TAX_AMOUNT_8: Decimal(15, 2),                          // 8%  SAP税额
            CURRENCY,
            TAX_RATE,
            TAX_CODE,
            GR_DATE,

        }
        group by
            T01.PO_BUKRS,           // 会社コード (聚合维度)
            T01.SUPPLIER,           // 仕入先 (聚合维度)
            T01.INV_MONTH,          // 月度
            T01.CURRENCY,
            T01.TAX_RATE,
            TAX_CODE,
            GR_DATE;

    entity PCH_T05_ACCOUNT_DETAIL_SUM_END as

        select from   PCH_T05_ACCOUNT_DETAIL_SUM_GRO  as T02

        distinct {
            key T02.PO_BUKRS,                       
            key T02.SUPPLIER,                       
            key T02.INV_MONTH,
            key T02.CURRENCY,
            key T02.TAX_RATE,
            TAX_CODE,
            GR_DATE,
            CALC_10_TAX_AMOUNT,  
            CALC_8_TAX_AMOUNT,   
            SAP_TAX_AMOUNT_10,
            SAP_TAX_AMOUNT_8,        

            // 再计算的税额（根据 CURRENCY 处理小数点后位数）
            case 
                when T02.CURRENCY = 'JPY' and T02.TAX_RATE = 10 then 
                    cast(floor(CASE WHEN T02.TAX_RATE = 10 THEN T02.CALC_10_TAX_AMOUNT END * 0.10) as Decimal(15,0))
                when T02.CURRENCY in ('USD', 'EUR') and T02.TAX_RATE = 10 then 
                    cast(floor(CASE WHEN T02.TAX_RATE = 10 THEN T02.CALC_10_TAX_AMOUNT END * 0.10 * 100) / 100 as Decimal(15,2))
            end as RECALC_TAX_AMOUNT_10 : Decimal(15, 2),  // 再计算 10% 税额
            
            case 
                when T02.CURRENCY = 'JPY' and T02.TAX_RATE = 8 then 
                    cast(floor(CASE WHEN T02.TAX_RATE = 8 THEN T02.CALC_8_TAX_AMOUNT END * 0.08) as Decimal(15,0))
                when T02.CURRENCY in ('USD', 'EUR') and T02.TAX_RATE = 8 then 
                    cast(floor(CASE WHEN T02.TAX_RATE = 8 THEN T02.CALC_8_TAX_AMOUNT END * 0.08 * 100) / 100 as Decimal(15,2))
            end as RECALC_TAX_AMOUNT_8 : Decimal(15, 2),   // 再计算 8% 税额

        }

         entity PCH_T05_ACCOUNT_DETAIL_SUM_FINAL as

        select from   PCH_T05_ACCOUNT_DETAIL_SUM_END  as T03

        distinct {
            key T03.PO_BUKRS,                       
            key T03.SUPPLIER,                       
            key T03.INV_MONTH,
            key T03.CURRENCY,
            key T03.TAX_RATE,
            TAX_CODE,
            GR_DATE,
            CALC_10_TAX_AMOUNT,  
            CALC_8_TAX_AMOUNT,   
            SAP_TAX_AMOUNT_10,
            SAP_TAX_AMOUNT_8,        

            // 再计算的税额（根据 CURRENCY 处理小数点后位数）
            case 
                when T03.CURRENCY = 'JPY' and T03.TAX_RATE = 10 then 
                    cast(floor(CASE WHEN T03.TAX_RATE = 10 THEN T03.CALC_10_TAX_AMOUNT END * 0.10) as Decimal(15,0))
                when T03.CURRENCY in ('USD', 'EUR') and T03.TAX_RATE = 10 then 
                    cast(floor(CASE WHEN T03.TAX_RATE = 10 THEN T03.CALC_10_TAX_AMOUNT END * 0.10 * 100) / 100 as Decimal(15,2))
            end as RECALC_TAX_AMOUNT_10 : Decimal(15, 2),  // 再计算 10% 税额
            
            case 
                when T03.CURRENCY = 'JPY' and T03.TAX_RATE = 8 then 
                    cast(floor(CASE WHEN T03.TAX_RATE = 8 THEN T03.CALC_8_TAX_AMOUNT END * 0.08) as Decimal(15,0))
                when T03.CURRENCY in ('USD', 'EUR') and T03.TAX_RATE = 8 then 
                    cast(floor(CASE WHEN T03.TAX_RATE = 8 THEN T03.CALC_8_TAX_AMOUNT END * 0.08 * 100) / 100 as Decimal(15,2))
            end as RECALC_TAX_AMOUNT_8 : Decimal(15, 2),   // 再计算 8% 税额

          // 消费税差额
            case 
                when T03.TAX_RATE = 10 then 
                    (RECALC_TAX_AMOUNT_10 - T03.SAP_TAX_AMOUNT_10)
            end as DIFF_TAX_AMOUNT_10 : Decimal(15, 2), 

            case 
                when T03.TAX_RATE = 8 then 
                    (RECALC_TAX_AMOUNT_8 - T03.SAP_TAX_AMOUNT_8)
            end as DIFF_TAX_AMOUNT_8 : Decimal(15, 2),
            // 合计金额
            case 
                when T03.TAX_RATE = 10 then 
                    (T03.CALC_10_TAX_AMOUNT + RECALC_TAX_AMOUNT_10)
            end as TOTAL_10_TAX_INCLUDED_AMOUNT : Decimal(15, 2), 
            
            case 
                when T03.TAX_RATE = 8 then 
                    (T03.CALC_8_TAX_AMOUNT + RECALC_TAX_AMOUNT_8)
            end as TOTAL_8_TAX_INCLUDED_AMOUNT : Decimal(15, 2)  
        }

         entity PCH_T05_ACCOUNT_DETAIL_EXCEL as

        select from   PCH_T05_ACCOUNT_DETAIL_SUM_FINAL

        distinct {
            key PO_BUKRS,                       
            key SUPPLIER,                       
            key INV_MONTH,
            key CURRENCY,
            key TAX_RATE,
            TAX_CODE,  
            GR_DATE,
            CALC_10_TAX_AMOUNT,  
            CALC_8_TAX_AMOUNT,   
            SAP_TAX_AMOUNT_10,
            SAP_TAX_AMOUNT_8,  
            DIFF_TAX_AMOUNT_10,
            DIFF_TAX_AMOUNT_8,
            RECALC_TAX_AMOUNT_8,
            RECALC_TAX_AMOUNT_10,
            TOTAL_8_TAX_INCLUDED_AMOUNT,
            TOTAL_10_TAX_INCLUDED_AMOUNT,

            case 
                when DIFF_TAX_AMOUNT_10 IS NOT NULL THEN DIFF_TAX_AMOUNT_10
                when DIFF_TAX_AMOUNT_8 IS NOT NULL THEN DIFF_TAX_AMOUNT_8
                else NULL
            end AS DIFF_TAX_AMOUNT :Decimal(15, 2),

            // 计算借方/贷方标志
            case 
                when DIFF_TAX_AMOUNT_10 is null and DIFF_TAX_AMOUNT_8 is null then null 
                when DIFF_TAX_AMOUNT_10 >= 0 or DIFF_TAX_AMOUNT_8 >= 0 then 'S' 
                else 'H' 
            end as SHKZG_FLAG : String,                      // 借方/贷方标志

            // 计算取引
            case 
                when DIFF_TAX_AMOUNT_10 is null and DIFF_TAX_AMOUNT_8 is null then null 
                when DIFF_TAX_AMOUNT_10 >= 0 or DIFF_TAX_AMOUNT_8 >= 0 then 1 
                else 2 
            end as TRANSACTION : String,                 // 取引类型

            case 
                when DIFF_TAX_AMOUNT_10 is not null then cast(floor(DIFF_TAX_AMOUNT_10 / 0.1) as Decimal(15,0)) 
                when DIFF_TAX_AMOUNT_8 is not null then cast(floor(DIFF_TAX_AMOUNT_8 / 0.08) as Decimal(15,0)) 
                else null 
            end as TAX_BASE_AMOUNT : Decimal(15,0), // 税基金额

            ROW_NUMBER() OVER () as INVOICEID: Integer,

           null as LASTDATE: Date,

            '' as REFERENCE: String,                         // REFERENCE 字段赋值为 null
            '' as DETAILTEXT: String,                        // DETAILTEXT 字段赋值为 null
            12600000 as ACCOUNT: String,                     // account 字段赋值为 12600000
            'RE' as DOCUMENTTYPE: String,                    // documentType 字段固定值为 'RE'
            'TAX' as HEADERTEXT: String                      // headertext 字段固定值为 'TAX'
                
                }

}

