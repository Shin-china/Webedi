using {TableService as view} from './table';
using {PCH} from '../db/model-pch';
using {MST} from '../db/model-mst';
using {SYS} from '../db/model-sys';

// MM-005 買掛金明細

extend service TableService {

     entity PCH_T05_ACCOUNT_DETAIL1           as
        select from PCH.T05_PAYMENT_D as T05
        left join PCH.T04_PAYMENT_H as T04
            on T05.INV_NO = T04.INV_NO
            and T04.HEADER_TEXT <> '仮払消費税調整'
        left join PCH.T01_PO_H as T01
            on T05.PO_NO = T01.PO_NO
        left join MST.T03_SAP_BP as T03
            on T04.SUPPLIER = T03.BP_ID
        left join SYS.T08_COM_OP_D as T08
            on T08.H_CODE = 'MM0011'

        distinct {
            key T05.INV_NO, // 伝票番号
            key T05.GL_YEAR, // 会計年度
            key T05.PO_NO, // 購買伝票
            key T05.D_NO, // 明細
            key T04.SUPPLIER, // 仕入先
                T01.PO_BUKRS, // 会社コード
                T05.ITEM_NO, // 請求書明細
                T04.SUPPLIER_DESCRIPTION, // 仕入先名称
                T04.INV_CONFIRMATION, // インボイス
                T05.PO_TRACK_NO, // 購買依頼追跡番号
                T05.PR_BY, // 購買依頼者
                T05.GL_ACCOUNT, // G/L勘定
                T05.COST_CENTER, // 原価センター
                T05.PURCHASE_GROUP, // 購買グループ
                T05.PURCHASE_GROUP_DESC, // 購買グループテキスト
                T05.CURRENCY, // 通貨コード
                T05.MAT_ID, // 品目
                T05.QUANTITY, // 数量
                T05.UNIT, // 発注数量単位
                T05.MAT_DESC, // 品目テキスト
                T05.TAX_CODE, // 税コード
                T05.TAX_RATE, // 税率
                T04.INV_POST_DATE, // 転記日付
                T04.SEND_FLAG, // 送信ステータス
                case
                    when
                        T05.CURRENCY = 'JYP'
                    then
                        cast(
                            T05.UNIT_PRICE as                         Decimal(18, 3)
                        ) // 保留三位小数
                    else
                        cast(
                            T05.UNIT_PRICE as                         Decimal(18, 5)
                        ) // 保留五位小数
                end                                 as UNIT_PRICE   : Decimal(18, 5),

                T04.INV_BASE_DATE, // 支払い基準日
                T05.GR_DATE, // 伝票日付
                T08.VALUE01,
                T03.LOG_NO, // 登録番号
                T03.POSTCODE, // 郵便番号
                T03.PLACE_NAME || '' || T03.REGIONS as ADRESS       : String(255), // 仕入先のアドレス
                T05.SHKZG, // 借方/貸方フラグ
                T05.PO_NO || T05.D_NO               as NO_DETAILS   : String(15), // 購買伝票\明細NO

                case
                    when
                        T05.SHKZG = 'S'
                    then
                        T05.PRICE_AMOUNT // 借方为正
                    when
                        T05.SHKZG = 'H'
                    then
                        -T05.PRICE_AMOUNT // 贷方为负
                    else
                        T05.PRICE_AMOUNT // 默认情况
                end                                 as PRICE_AMOUNT : Decimal(18, 3), // 本体金額

                case
                    when
                        T05.SHKZG = 'S'
                    then
                        T05.TOTAL_AMOUNT // 借方为正
                    when
                        T05.SHKZG = 'H'
                    then
                        -T05.TOTAL_AMOUNT // 贷方为负
                    else
                        T05.TOTAL_AMOUNT // 默认情况
                end                                 as TOTAL_AMOUNT : Decimal(18, 3), // 計上金額

                case
                    when
                        T05.SHKZG = 'S'
                    then
                        T05.TAX_AMOUNT // 借方为正
                    when
                        T05.SHKZG = 'H'
                    then
                        -T05.TAX_AMOUNT // 贷方为负
                    else
                        T05.TAX_AMOUNT // 默认情况
                end                                 as TAX_AMOUNT   : Decimal(18, 3), // 計上金額

                TO_CHAR(
                    T04.INV_POST_DATE, 'YYYYMM'
                )                                   as INV_MONTH    : String

        }

        entity PCH_T05_ACCOUNT_DETAIL_SUM1       as
        select from PCH_T05_ACCOUNT_DETAIL1 as T05

        distinct {
            key T05.INV_NO, // 伝票番号
            key T05.GL_YEAR, // 会計年度
            key T05.PO_NO, // 購買伝票
            key T05.D_NO, // 明細
            key T05.SUPPLIER, // 仕入先
                T05.ITEM_NO, // 請求書明細
                T05.PO_BUKRS, // 会社コード
                T05.SUPPLIER_DESCRIPTION, // 仕入先名称
                T05.INV_CONFIRMATION, // インボイス
                T05.PO_TRACK_NO, // 購買依頼追跡番号
                T05.PR_BY, // 購買依頼者
                T05.GL_ACCOUNT, // G/L勘定
                T05.COST_CENTER, // 原価センター
                T05.PURCHASE_GROUP, // 購買グループ
                T05.PURCHASE_GROUP_DESC, // 購買グループテキスト
                T05.CURRENCY, // 通貨コード
                T05.MAT_ID, // 品目
                T05.QUANTITY, // 数量
                T05.UNIT, // 発注数量単位
                T05.MAT_DESC, // 品目テキスト
                T05.TAX_CODE, // 税コード
                T05.TAX_RATE, // 税率
                T05.INV_POST_DATE, // 転記日付
                T05.SEND_FLAG, // 送信ステータス
                T05.UNIT_PRICE, // 単価
                T05.PRICE_AMOUNT, // 本体金額
                T05.TAX_AMOUNT, // 消費税額
                T05.TOTAL_AMOUNT, // 計上金額
                T05.INV_BASE_DATE, // 支払い基準日
                T05.GR_DATE, // 伝票日付
                T05.LOG_NO, // 登録番号
                T05.INV_MONTH, // 提取年月，作为月度字段
                VALUE01,
                POSTCODE,
                ADRESS,
                NO_DETAILS,
                // 新增计算字段
                case
                    when
                        T05.TAX_RATE = 10
                    then
                        cast(
                            T05.PRICE_AMOUNT as Decimal(15, 2)
                        )
                end as CALC_10_PRICE_AMOUNT :   Decimal(15, 2), // 10% 税抜金额

                case
                    when
                        T05.TAX_RATE = 8
                    then
                        cast(
                            T05.PRICE_AMOUNT as Decimal(15, 2)
                        )
                end as CALC_8_PRICE_AMOUNT  :   Decimal(15, 2), // 8% 税抜金额

                // SAP 税额
                case
                    when
                        T05.TAX_RATE = 10
                    then
                        cast(
                            T05.TAX_AMOUNT as   Decimal(15, 2)
                        )
                end as SAP_TAX_AMOUNT_10    :   Decimal(15, 2), // 10% SAP 税额

                case
                    when
                        T05.TAX_RATE = 8
                    then
                        cast(
                            T05.TAX_AMOUNT as   Decimal(15, 2)
                        )
                end as SAP_TAX_AMOUNT_8     :   Decimal(15, 2), // 8% SAP 税额

                case
                    when
                        T05.TAX_RATE = 10
                    then
                        cast(
                            T05.TOTAL_AMOUNT as Decimal(15, 2)
                        )
                end as TOTAL_AMOUNT_10      :   Decimal(15, 2),

                case
                    when
                        T05.TAX_RATE = 8
                    then
                        cast(
                            T05.TOTAL_AMOUNT as Decimal(15, 2)
                        )
                end as TOTAL_AMOUNT_8       :   Decimal(15, 2),

        }

    entity PCH_T05_PRICE_AMOUNT_SUM1         as

        select from PCH_T05_ACCOUNT_DETAIL_SUM1 as T01

        distinct {
            key T01.SUPPLIER,
                ROUND(
                    SUM(CALC_8_PRICE_AMOUNT), 2
                ) as CALC_8_PRICE_AMOUNT_TOTAL  : Decimal(15, 2),
                ROUND(
                    SUM(CALC_10_PRICE_AMOUNT), 2
                ) as CALC_10_PRICE_AMOUNT_TOTAL : Decimal(15, 2),
                ROUND(
                    SUM(SAP_TAX_AMOUNT_8), 2
                ) as SAP_TAX_AMOUNT_8_TOTAL     : Decimal(15, 2),
                ROUND(
                    SUM(SAP_TAX_AMOUNT_10), 2
                ) as SAP_TAX_AMOUNT_10_TOTAL    : Decimal(15, 2),
                ROUND(
                    SUM(TOTAL_AMOUNT_8), 2
                ) as TOTAL_AMOUNT_8_TOTAL       : Decimal(15, 2),
                ROUND(
                    SUM(TOTAL_AMOUNT_10), 2
                ) as TOTAL_AMOUNT_10_TOTAL      : Decimal(15, 2),
                ROUND(
                    SUM(PRICE_AMOUNT), 2
                ) as PRICE_AMOUNT_TOTAL         : Decimal(15, 2),
        }
        group by
            T01.SUPPLIER;

    entity PCH_T05_ACCOUNT_DETAIL_EXCEL1     as

        select from PCH_T05_ACCOUNT_DETAIL1 as T01
        left join PCH_T05_PRICE_AMOUNT_SUM1 as T02
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
                T02.PRICE_AMOUNT_TOTAL,//本体金額(総合計:)
                T02.TOTAL_AMOUNT_8_TOTAL,//計上金額8%(合計(税率8％軽減税率):)
                T02.TOTAL_AMOUNT_10_TOTAL,//計上金額10%(合計(税率10％):)
                T02.SAP_TAX_AMOUNT_8_TOTAL,//消費税額8%(合計(税率8％軽減税率):)
                T02.SAP_TAX_AMOUNT_10_TOTAL,//消費税額10%(合計(税率10％):)
                T02.CALC_8_PRICE_AMOUNT_TOTAL, //本体金額8%(合計(税率8％軽減税率):)
                T02.CALC_10_PRICE_AMOUNT_TOTAL,//本体金額10%(合計(税率10％):)

                SUBSTRING(
                    T01.INV_MONTH, 1, 4
                ) || '年' || SUBSTRING(
                    T01.INV_MONTH, 5, 2
                ) || '月'                   as INV_MONTH_FORMATTED : String,

                // 计算每个 SUPPLIER 的条数
                COUNT( * ) over(
                    partition by T01.SUPPLIER
                )                          as TOTAL_COUNT         : Integer, // 按照 SUPPLIER 维度计算条数
     
                '(' || T01.SUPPLIER || ')' as SUPPLIER_1          : String

        } 

///////////////////////////////////////////////////////////////////////////////////////

    entity PCH_T05_ACCOUNT_DETAIL           as
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
            key T05.INV_NO, // 伝票番号
            key T05.GL_YEAR, // 会計年度
            key T05.PO_NO, // 購買伝票
            key T05.D_NO, // 明細
            key T04.SUPPLIER, // 仕入先
                T01.PO_BUKRS, // 会社コード
                T05.ITEM_NO, // 請求書明細
                T05.TAX_CODE, // 税コード
                T05.TAX_RATE, // 税率
                T05.CURRENCY, // 通貨コード
                T04.INV_POST_DATE, // 転記日付
                T04.HEADER_TEXT,
                T04.AMOUNT,
                T04.TAX_AMOUNT AS TAX_AMOUNT_1,
                case
                    when
                        T05.CURRENCY = 'JYP'
                    then
                        cast(
                            T05.UNIT_PRICE as                         Decimal(18, 3)
                        ) // 保留三位小数
                    else
                        cast(
                            T05.UNIT_PRICE as                         Decimal(18, 5)
                        ) // 保留五位小数
                end                                 as UNIT_PRICE   : Decimal(18, 5),

                T04.INV_BASE_DATE, // 支払い基準日
                T05.GR_DATE, // 伝票日付
                T05.SHKZG, // 借方/貸方フラグ
                T05.PO_NO || T05.D_NO               as NO_DETAILS   : String(15), // 購買伝票\明細NO

                 CASE
                    WHEN T04.HEADER_TEXT <> '仮払消費税調整' THEN
                        CASE
                            WHEN T05.SHKZG = 'S' THEN
                                T04.TAX_AMOUNT // 借方为正
                            WHEN T05.SHKZG = 'H' THEN
                                -T04.TAX_AMOUNT // 贷方为负
                            ELSE
                                T04.TAX_AMOUNT // 默认情况
                        END
                    ELSE
                        T04.TAX_AMOUNT 
                END AS TAX_AMOUNT_HEADER : Decimal(18, 3),

                CASE
                    WHEN T04.HEADER_TEXT <> '仮払消費税調整' THEN
                        CASE
                            WHEN T05.SHKZG = 'S' THEN
                                T04.AMOUNT // 借方为正
                            WHEN T05.SHKZG = 'H' THEN
                                -T04.AMOUNT // 贷方为负
                            ELSE
                                T04.AMOUNT // 默认情况
                        END
                    ELSE
                        T04.AMOUNT 
                END AS AMOUNT_HEADER : Decimal(18, 3),

                case
                    when
                        T05.SHKZG = 'S'
                    then
                        T05.PRICE_AMOUNT // 借方为正
                    when
                        T05.SHKZG = 'H'
                    then
                        -T05.PRICE_AMOUNT // 贷方为负
                    else
                        T05.PRICE_AMOUNT // 默认情况
                end    as PRICE_AMOUNT : Decimal(18, 3), // 本体金額

                case
                    when
                        T05.SHKZG = 'S'
                    then
                        T05.TOTAL_AMOUNT // 借方为正
                    when
                        T05.SHKZG = 'H'
                    then
                        -T05.TOTAL_AMOUNT // 贷方为负
                    else
                        T05.TOTAL_AMOUNT // 默认情况
                end     as TOTAL_AMOUNT : Decimal(18, 3), // 計上金額

                case
                    when
                        T05.SHKZG = 'S'
                    then
                        T05.TAX_AMOUNT // 借方为正
                    when
                        T05.SHKZG = 'H'
                    then
                        -T05.TAX_AMOUNT // 贷方为负
                    else
                        T05.TAX_AMOUNT // 默认情况
                end                                 as TAX_AMOUNT   : Decimal(18, 3), // 計上金額

                TO_CHAR(
                    T04.INV_POST_DATE, 'YYYYMM'
                )                                   as INV_MONTH    : String

        }

    entity PCH_T05_ACCOUNT_DETAIL_SUM       as
        select from PCH_T05_ACCOUNT_DETAIL

        distinct {
            key INV_NO, // 伝票番号
            key GL_YEAR, // 会計年度
            key PO_NO, // 購買伝票
            key D_NO, // 明細
            key SUPPLIER, // 仕入先
                ITEM_NO, // 請求書明細
                PO_BUKRS, // 会社コード
                TAX_CODE, // 税コード
                TAX_RATE, // 税率
                INV_POST_DATE, // 転記日付
                UNIT_PRICE, // 単価
                PRICE_AMOUNT, // 本体金額
                TAX_AMOUNT, // 消費税額
                TOTAL_AMOUNT, // 計上金額
                INV_BASE_DATE, // 支払い基準日
                GR_DATE, // 伝票日付
                INV_MONTH, // 提取年月，作为月度字段
                CURRENCY, // 通貨コード
                NO_DETAILS,
                HEADER_TEXT,
                // 新增计算字段
                case
                    when
                        TAX_RATE = 10
                    then
                        cast(
                            PRICE_AMOUNT as Decimal(15, 2)
                        )
                end as CALC_10_PRICE_AMOUNT :   Decimal(15, 2), // 10% 税抜金额

                case
                    when
                        TAX_RATE = 8
                    then
                        cast(
                            PRICE_AMOUNT as Decimal(15, 2)
                        )
                end as CALC_8_PRICE_AMOUNT  :   Decimal(15, 2), // 8% 税抜金额

                // SAP 税额
                case
                    when
                        TAX_RATE = 10
                    then
                        cast(
                           TAX_AMOUNT as   Decimal(15, 2)
                        )
                end as SAP_TAX_AMOUNT_10    :   Decimal(15, 2), // 10% SAP 税额

                case
                    when
                        TAX_RATE = 8
                    then
                        cast(
                            TAX_AMOUNT as   Decimal(15, 2)
                        )
                end as SAP_TAX_AMOUNT_8     :   Decimal(15, 2), // 8% SAP 税额

                case
                    when
                        TAX_RATE = 10
                    then
                        cast(
                           TOTAL_AMOUNT as Decimal(15, 2)
                        )
                end as TOTAL_AMOUNT_10      :   Decimal(15, 2),

                case
                    when
                        TAX_RATE = 8
                    then
                        cast(
                          TOTAL_AMOUNT as Decimal(15, 2)
                        )
                end as TOTAL_AMOUNT_8       :   Decimal(15, 2),

                 case
                    when
                        TAX_RATE = 10
                    then
                        cast(
                            TAX_AMOUNT_HEADER as Decimal(15, 2)
                        )
                end as TAX_AMOUNT_HEADER_10 :   Decimal(15, 2), // 10% 税抜金额HEADER

                case
                    when
                        TAX_RATE = 8
                    then
                        cast(
                            TAX_AMOUNT_HEADER as Decimal(15, 2)
                        )
                end as TAX_AMOUNT_HEADER_8  :   Decimal(15, 2), // 8% 税抜金额HEADER

                  case
                    when
                        TAX_RATE = 10
                    then
                        cast(
                            AMOUNT_HEADER as Decimal(15, 2)
                        )
                end as AMOUNT_HEADER_10 :   Decimal(15, 2), // 10% 税抜金额HEADER

                case
                    when
                        TAX_RATE = 8
                    then
                        cast(
                            AMOUNT_HEADER as Decimal(15, 2)
                        )
                end as AMOUNT_HEADER_8  :   Decimal(15, 2), // 8% 税抜金额HEADER

        }

    entity PCH_T05_ACCOUNT_DETAIL_SUM_GRO   as

        select from PCH_T05_ACCOUNT_DETAIL_SUM as T01

        distinct {
            key T01.PO_BUKRS,
            key T01.SUPPLIER,
            key T01.INV_MONTH,
                SUM(CALC_10_PRICE_AMOUNT)    as CALC_10_PRICE_AMOUNT    : Decimal(15, 2),    // 10% 税抜金额
                SUM(CALC_8_PRICE_AMOUNT)     as CALC_8_PRICE_AMOUNT     : Decimal(15, 2),    // 8%  税抜金额
                SUM(SAP_TAX_AMOUNT_10)       as SAP_TAX_AMOUNT_10       : Decimal(15, 2),    // 10% SAP税额
                SUM(SAP_TAX_AMOUNT_8)        as SAP_TAX_AMOUNT_8        : Decimal(15, 2),    // 8%  SAP税额
                SUM(TAX_AMOUNT_HEADER_10)    as TAX_AMOUNT_HEADER_10    : Decimal(15, 2),    // 10% SAP税额HEADER TAX_AMOUNT_HEADER
                SUM(TAX_AMOUNT_HEADER_8)     as TAX_AMOUNT_HEADER_8     : Decimal(15, 2),    // 8%  SAP税额HEADER TAX_AMOUNT_HEADER
                SUM(AMOUNT_HEADER_10)        as AMOUNT_HEADER_10        : Decimal(15, 2),    // 10% SAP税额HEADER AMOUNT_HEADER
                SUM(AMOUNT_HEADER_8)         as AMOUNT_HEADER_8         : Decimal(15, 2),    // 8%  SAP税额HEADER AMOUNT_HEADER
        }
        group by
            T01.PO_BUKRS,
            T01.SUPPLIER,
            T01.INV_MONTH;

    entity PCH_T05_ACCOUNT_DETAIL_SUM_END   as

        select from PCH_T05_ACCOUNT_DETAIL_SUM_GRO as T02
        left join PCH_T05_ACCOUNT_DETAIL as T03
            on  T02.SUPPLIER  = T03.SUPPLIER
            and T02.INV_MONTH = T03.INV_MONTH
            and T02.PO_BUKRS  = T03.PO_BUKRS

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
                T03.NO_DETAILS,
                T03.UNIT_PRICE,
                T03.PRICE_AMOUNT,
                T03.INV_POST_DATE,
                T02.CALC_10_PRICE_AMOUNT,
                T02.CALC_8_PRICE_AMOUNT,
                T02.SAP_TAX_AMOUNT_10,
                T02.SAP_TAX_AMOUNT_8,
                T02.TAX_AMOUNT_HEADER_10,
                T02.TAX_AMOUNT_HEADER_8,
                T02.AMOUNT_HEADER_10,
                T02.AMOUNT_HEADER_8,
                T03.HEADER_TEXT,
                T03.SHKZG,
                // 再计算的税额（根据 CURRENCY 处理小数点后位数）
                case
                    when
                        T03.CURRENCY     = 'JPY'
                        and T03.TAX_RATE = 10
                    then
                        cast(
                            floor(
                                case
                                    when
                                        T03.TAX_RATE = 10
                                    then
                                        T02.CALC_10_PRICE_AMOUNT
                                end * 0.10
                            ) as                Decimal(15, 0)
                        )
                    when
                        T03.CURRENCY     in (
                            'USD', 'EUR'
                        )
                        and T03.TAX_RATE =  10
                    then
                        cast(
                            floor(
                                case
                                    when
                                        T03.TAX_RATE = 10
                                    then
                                        T02.CALC_10_PRICE_AMOUNT
                                end * 0.10 * 100
                            ) / 100 as          Decimal(15, 2)
                        )
                    else
                        null // 其他情况返回 NULL
                end as RECALC_PRICE_AMOUNT_10 : Decimal(15, 2), // 再计算 10% 税额

                case
                    when
                        T03.CURRENCY     = 'JPY'
                        and T03.TAX_RATE = 8
                    then
                        cast(
                            floor(
                                case
                                    when
                                        T03.TAX_RATE = 8
                                    then
                                        T02.CALC_8_PRICE_AMOUNT
                                end * 0.08
                            ) as                Decimal(15, 0)
                        )
                    when
                        T03.CURRENCY     in (
                            'USD', 'EUR'
                        )
                        and T03.TAX_RATE =  8
                    then
                        cast(
                            floor(
                                case
                                    when
                                        T03.TAX_RATE = 8
                                    then
                                        T02.CALC_8_PRICE_AMOUNT
                                end * 0.08 * 100
                            ) / 100 as          Decimal(15, 2)
                        )
                    else
                        null // 其他情况返回 NULL
                end as RECALC_PRICE_AMOUNT_8  : Decimal(15, 2), // 再计算 8% 税额

        }

    entity PCH_T05_ACCOUNT_DETAIL_SUM_FINAL as
        select from PCH_T05_ACCOUNT_DETAIL_SUM_END as T03

        distinct {
            key SUPPLIER,
            key INV_NO,
                PO_NO,
                D_NO,
                PO_BUKRS,
                INV_MONTH,
                CURRENCY,
                TAX_RATE,
                TAX_CODE,
                GR_DATE,
                CALC_10_PRICE_AMOUNT,
                CALC_8_PRICE_AMOUNT,
                SAP_TAX_AMOUNT_10,
                SAP_TAX_AMOUNT_8,
                NO_DETAILS,
                UNIT_PRICE,
                PRICE_AMOUNT,
                INV_POST_DATE,
                TAX_AMOUNT_HEADER_10,
                TAX_AMOUNT_HEADER_8,
                AMOUNT_HEADER_10,
                AMOUNT_HEADER_8,
                HEADER_TEXT,
                SHKZG,

                case
                    when
                        CURRENCY     = 'JPY'
                        and TAX_RATE = 10
                    then
                        cast(
                            floor(
                                case
                                    when
                                        TAX_RATE = 10
                                    then
                                        CALC_10_PRICE_AMOUNT
                                end * 0.10
                            ) as                      Decimal(15, 0)
                        )
                    when
                        T03.CURRENCY     in (
                            'USD', 'EUR'
                        )
                        and TAX_RATE =  10
                    then
                        cast(
                            floor(
                                case
                                    when
                                        TAX_RATE = 10
                                    then
                                        CALC_10_PRICE_AMOUNT
                                end * 0.10 * 100
                            ) / 100 as                Decimal(15, 2)
                        )
                end as RECALC_PRICE_AMOUNT_10       : Decimal(15, 2), // 再计算 10% 税额

                case
                    when
                        CURRENCY     = 'JPY'
                        and TAX_RATE = 8
                    then
                        cast(
                            floor(
                                case
                                    when
                                        TAX_RATE = 8
                                    then
                                        CALC_8_PRICE_AMOUNT
                                end * 0.08
                            ) as                      Decimal(15, 0)
                        )
                    when
                        CURRENCY     in (
                            'USD', 'EUR'
                        )
                        and TAX_RATE =  8
                    then
                        cast(
                            floor(
                                case
                                    when
                                        TAX_RATE = 8
                                    then
                                        CALC_8_PRICE_AMOUNT
                                end * 0.08 * 100
                            ) / 100 as                Decimal(15, 2)
                        )
                end as RECALC_PRICE_AMOUNT_8        : Decimal(15, 2), // 再计算 8% 税额

                // 消费税差额
                case
                    when
                        TAX_RATE = 10
                    then
                        COALESCE(
                            RECALC_PRICE_AMOUNT_10, 0
                        )-COALESCE(
                            SAP_TAX_AMOUNT_10, 0
                        )
                end as DIFF_TAX_AMOUNT_10           : Decimal(15, 2),

                case
                    when
                        TAX_RATE = 8
                    then
                        COALESCE(
                            RECALC_PRICE_AMOUNT_8, 0
                        )-COALESCE(
                            SAP_TAX_AMOUNT_8, 0
                        )
                end as DIFF_TAX_AMOUNT_8            : Decimal(15, 2),

                // 合计金额
                case
                    when
                        TAX_RATE = 10
                    then
                        COALESCE(
                            CALC_10_PRICE_AMOUNT, 0
                        )+COALESCE(
                            RECALC_PRICE_AMOUNT_10, 0
                        )
                end as TOTAL_10_TAX_INCLUDED_AMOUNT : Decimal(15, 2),

                case
                    when
                        TAX_RATE = 8
                    then
                        COALESCE(
                            CALC_8_PRICE_AMOUNT, 0
                        )+COALESCE(
                            RECALC_PRICE_AMOUNT_8, 0
                        )
                end as TOTAL_8_TAX_INCLUDED_AMOUNT  : Decimal(15, 2),


        }


    entity PCH_T05_ACCOUNT_DETAIL_DISPLAY   as

        select from PCH_T05_ACCOUNT_DETAIL_SUM_GRO as T02
        left join PCH_T05_ACCOUNT_DETAIL_SUM_FINAL as T03
            on  T02.SUPPLIER  = T03.SUPPLIER
            and T02.INV_MONTH = T03.INV_MONTH
            and T02.PO_BUKRS  = T03.PO_BUKRS

        distinct {
            key T02.SUPPLIER,
            key T02.INV_MONTH,
            key T02.PO_BUKRS,
                T03.CURRENCY,
                T02.CALC_10_PRICE_AMOUNT, // 10% 税抜金额
                T02.CALC_8_PRICE_AMOUNT, // 8%  税抜金额
                T02.SAP_TAX_AMOUNT_10, // 10% SAP税额
                T02.SAP_TAX_AMOUNT_8, // 8%  SAP税额
                T03.RECALC_PRICE_AMOUNT_10, // 再計算10％税額
                T03.RECALC_PRICE_AMOUNT_8, // 再計算8％税額
                T03.DIFF_TAX_AMOUNT_10, // 10％消費税差額
                T03.DIFF_TAX_AMOUNT_8, // 8％消費税差額
                T03.TOTAL_10_TAX_INCLUDED_AMOUNT, // 合計10％税込金額
                T03.TOTAL_8_TAX_INCLUDED_AMOUNT, // 合計8％税込金額
                T03.TAX_CODE,
                // T02.TAX_AMOUNT_HEADER_10,
                // T02.TAX_AMOUNT_HEADER_8,
                // T02.AMOUNT_HEADER_10,
                // T02.AMOUNT_HEADER_8,
                T02.TAX_AMOUNT_HEADER_8 + T02.AMOUNT_HEADER_8 as TAX_AMOUNT_HEADER_8_TOTAL : Decimal(15, 2),
                T02.TAX_AMOUNT_HEADER_10 + T02.AMOUNT_HEADER_10 as TAX_AMOUNT_HEADER_10_TOTAL : Decimal(15, 2)

        }

    entity PCH_T05_ACCOUNT_DETAIL_DISPLAY2  as

        select from PCH_T05_ACCOUNT_DETAIL_DISPLAY

        distinct {
            key SUPPLIER,
            key INV_MONTH,
            key PO_BUKRS,
            key CURRENCY,
                SUM(RECALC_PRICE_AMOUNT_10)       as RECALC_PRICE_AMOUNT_10       : Decimal(15, 2), //再計算10％税額
                SUM(RECALC_PRICE_AMOUNT_8)        as RECALC_PRICE_AMOUNT_8        : Decimal(15, 2), //再計算8％税額
                SUM(DIFF_TAX_AMOUNT_10)           as DIFF_TAX_AMOUNT_10           : Decimal(15, 2), //10％消費税差額
                SUM(DIFF_TAX_AMOUNT_8)            as DIFF_TAX_AMOUNT_8            : Decimal(15, 2), //8％消費税差額
                SUM(TOTAL_10_TAX_INCLUDED_AMOUNT) as TOTAL_10_TAX_INCLUDED_AMOUNT : Decimal(15, 2), //合計10％税込金額
                SUM(TOTAL_8_TAX_INCLUDED_AMOUNT)  as TOTAL_8_TAX_INCLUDED_AMOUNT  : Decimal(15, 2) //合計8％税込金額
        }
        group by
            PO_BUKRS,
            SUPPLIER,
            INV_MONTH,
            CURRENCY;

    entity PCH_T05_ACCOUNT_DETAIL_DISPLAY3  as

        select from PCH_T05_ACCOUNT_DETAIL_SUM_GRO as T01
        left join PCH_T05_ACCOUNT_DETAIL_DISPLAY2 as T02
            on  T01.SUPPLIER  = T02.SUPPLIER
            and T01.INV_MONTH = T02.INV_MONTH
            and T01.PO_BUKRS  = T02.PO_BUKRS
        left join PCH_T05_ACCOUNT_DETAIL_DISPLAY as T03
            on  T01.SUPPLIER  = T03.SUPPLIER
            and T01.INV_MONTH = T03.INV_MONTH
            and T01.PO_BUKRS  = T03.PO_BUKRS

        distinct {
            key T01.SUPPLIER,
            key T01.INV_MONTH,
            key T01.PO_BUKRS,
                T02.CURRENCY,
                T01.CALC_10_PRICE_AMOUNT, // 10% 税抜金额
                T01.CALC_8_PRICE_AMOUNT, // 8%  税抜金额
                T01.SAP_TAX_AMOUNT_10, // 10% SAP税额
                T01.SAP_TAX_AMOUNT_8, // 8%  SAP税额
                T02.RECALC_PRICE_AMOUNT_10, // 再計算10％税額
                T02.RECALC_PRICE_AMOUNT_8, // 再計算8％税額
                T02.DIFF_TAX_AMOUNT_10, // 10％消費税差額
                T02.DIFF_TAX_AMOUNT_8, // 8％消費税差額
                T02.TOTAL_10_TAX_INCLUDED_AMOUNT, // 合計10％税込金額
                T02.TOTAL_8_TAX_INCLUDED_AMOUNT, // 合計8％税込金額                
                // T03.TAX_AMOUNT_HEADER_10,
                // T03.TAX_AMOUNT_HEADER_8,
                // T03.AMOUNT_HEADER_10,
                // T03.AMOUNT_HEADER_8,
                T03.TAX_AMOUNT_HEADER_8_TOTAL,
                T03.TAX_AMOUNT_HEADER_10_TOTAL,

                case
                    when
                        T02.DIFF_TAX_AMOUNT_10 is not null
                    then
                        T02.DIFF_TAX_AMOUNT_10
                    when
                        T02.DIFF_TAX_AMOUNT_8 is not null
                    then
                        T02.DIFF_TAX_AMOUNT_8
                    else
                        null
                end       as DIFF_TAX_AMOUNT : Decimal(15, 2),

                // 计算借方/贷方标志
                case
                    when
                        T02.DIFF_TAX_AMOUNT_10 is null
                        and T02.DIFF_TAX_AMOUNT_8  is null
                    then
                        null
                    when
                        T02.DIFF_TAX_AMOUNT_10 >= 0
                        or T02.DIFF_TAX_AMOUNT_8   >= 0
                    then
                        'S'
                    else
                        'H'
                end       as SHKZG_FLAG      : String, // 借方/贷方标志

                // 计算取引
                case
                    when
                        T02.DIFF_TAX_AMOUNT_10 is null
                        and T02.DIFF_TAX_AMOUNT_8  is null
                    then
                        null
                    when
                        T02.DIFF_TAX_AMOUNT_10 >= 0
                        or T02.DIFF_TAX_AMOUNT_8   >= 0
                    then
                        1
                    else
                        2
                end       as TRANSACTION     : String, // 取引类型

                case
                    when
                        T02.DIFF_TAX_AMOUNT_10 is not null
                    then
                        cast(
                            floor(
                                T02.DIFF_TAX_AMOUNT_10 / 0.1
                            ) as               Decimal(15, 0)
                        )
                    when
                        T02.DIFF_TAX_AMOUNT_8 is not null
                    then
                        cast(
                            floor(
                                T02.DIFF_TAX_AMOUNT_8 / 0.08
                            ) as               Decimal(15, 0)
                        )
                    else
                        null
                end       as TAX_BASE_AMOUNT : Decimal(15, 0), // 税基金额

                null      as LASTDATE          : Date,
                ''        as REFERENCE         : String, // REFERENCE 字段赋值为 null
                '仮払消費税調整' as DETAILTEXT  : String, // DETAILTEXT 字段赋值为仮払消費税調整
                12600000  as ACCOUNT           : String, // account 字段赋值为 12600000
                'RE'      as DOCUMENTTYPE      : String, // documentType 字段固定值为 'RE'
                '仮払消費税調整' as HEADERTEXT  : String, // headertext 字段固定值为仮払消費税調整

        }

    entity PCH_T05_FOREXCEL                 as

        select from PCH_T05_ACCOUNT_DETAIL_DISPLAY2 as T01
        left join PCH_T05_ACCOUNT_DETAIL_DISPLAY as T02
            on  T01.SUPPLIER  = T02.SUPPLIER
            and T01.INV_MONTH = T02.INV_MONTH
            and T01.PO_BUKRS  = T02.PO_BUKRS
        left join PCH_T05_ACCOUNT_DETAIL_DISPLAY3 as T03
            on  T02.SUPPLIER  = T03.SUPPLIER
            and T02.INV_MONTH = T03.INV_MONTH
            and T02.PO_BUKRS  = T03.PO_BUKRS

        distinct {
            key T01.SUPPLIER,
            key T01.INV_MONTH,
            key T01.PO_BUKRS,
                T02.CURRENCY,
                T02.CALC_10_PRICE_AMOUNT, // 10% 税抜金额
                T02.CALC_8_PRICE_AMOUNT, // 8%  税抜金额
                T02.SAP_TAX_AMOUNT_10, // 10% SAP税额
                T02.SAP_TAX_AMOUNT_8, // 8%  SAP税额
                T01.RECALC_PRICE_AMOUNT_10, // 再計算10％税額
                T01.RECALC_PRICE_AMOUNT_8, // 再計算8％税額
                T01.DIFF_TAX_AMOUNT_10, // 10％消費税差額
                T01.DIFF_TAX_AMOUNT_8, // 8％消費税差額
                T01.TOTAL_10_TAX_INCLUDED_AMOUNT, // 合計10％税込金額
                T01.TOTAL_8_TAX_INCLUDED_AMOUNT, // 合計8％税込金額
                T03.TAX_AMOUNT_HEADER_8_TOTAL,
                T03.TAX_AMOUNT_HEADER_10_TOTAL,
                T03.TRANSACTION,
                T03.REFERENCE,
                T03.DOCUMENTTYPE,
                T03.HEADERTEXT,
                T03.LASTDATE,
                T03.ACCOUNT,
                T03.DETAILTEXT,
                T03.SHKZG_FLAG,
                T03.DIFF_TAX_AMOUNT,
                T02.TAX_CODE,
                T03.TAX_BASE_AMOUNT,
                ROW_NUMBER() over() as INVOICEID    : Integer,
                '仮払消費税調整'           as DETAILTEXT50 : String, // headertext 字段固定值为仮払消費税調整

        }

    action PCH05_SENDEMAIL(parms : String)     returns String;
    action PCH05_CONFIRM(parms : String)       returns String;
    action PCH05_CANCEL(parms : String)        returns String;
    action PCH05_EXCELDOWNLOAD(parms : String) returns LargeBinary;

}
