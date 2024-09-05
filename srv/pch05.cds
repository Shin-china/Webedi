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
        //  left join MST.MST_T03_SAP_BP as T03
        //      on T05.LOG_NO = T03.LOG_NO
            
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
                // T05.GR_DATE,                       // 伝票日付
                // T03.LOG_NO                          // 登録番号
            // 新增的计算字段
            cast(T05.PRICE_AMOUNT * 1.10 as Decimal(15,2)) as CALC_10_TAX_AMOUNT : Decimal(15, 2),  // 10％税抜き金額
            cast(T05.PRICE_AMOUNT * 1.08 as Decimal(15,2)) as CALC_8_TAX_AMOUNT : Decimal(15, 2),   // 8％税抜き金額
            (cast(T05.PRICE_AMOUNT * 0.10 as Decimal(15,2))) as RECALC_TAX_AMOUNT_10 : Decimal(15, 2),  // 再計算10％税額
            (cast(T05.PRICE_AMOUNT * 0.08 as Decimal(15,2))) as RECALC_TAX_AMOUNT_8 : Decimal(15, 2),   // 再計算8％税額
            (cast(T05.PRICE_AMOUNT * 0.10 as Decimal(15,2)) - T05.TAX_AMOUNT) as DIFF_10_TAX_AMOUNT : Decimal(15, 2), // 10％消費税差額
            (cast(T05.PRICE_AMOUNT * 0.08 as Decimal(15,2)) - T05.TAX_AMOUNT) as DIFF_8_TAX_AMOUNT : Decimal(15, 2), // 8％消費税差額
            (T05.PRICE_AMOUNT + cast(T05.PRICE_AMOUNT * 0.10 as Decimal(15,2))) as TOTAL_10_TAX_AMOUNT : Decimal(15, 2), // 合計10％税込金額
            (T05.PRICE_AMOUNT + cast(T05.PRICE_AMOUNT * 0.08 as Decimal(15,2))) as TOTAL_8_TAX_AMOUNT : Decimal(15, 2)  // 合計8％税込金額
        }
        where T05.TAX_RATE in (10, 8); // 仅选择税率为10%和8%的记录
}