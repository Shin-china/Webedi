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
        T05.UNIT_PRICE,                    // 取引通貨単価
        T05.CURRENCY,                      // INV通貨コード
        T04.EXCHANGE,                      // 換算レート
        T05.PO_TRACK_NO,                   // 備考
        T05.TAX_RATE,                      // INV税率
        T04.INV_BASE_DATE,                 // 支払日
        T05.UNIT_PRICE * COALESCE(T04.EXCHANGE, 1) AS UNIT_PRICE_IN_YEN : Decimal(15, 6), 
        T05.TOTAL_AMOUNT * COALESCE(T04.EXCHANGE, 1) AS TOTAL_AMOUNT_IN_YEN : Decimal(20, 6), 
        T02.SUPPLIER_MAT,                  // 仕入先品目コード
        M03.LOG_NO,                        // 登録番号    
        M04.ZABC                           // ABC区分
    }

    action PCH04_SENDEMAIL(parms : String) returns String;

}

annotate TableService.PCH_T04_PAYMENT with {
    SEND_FLAG @(Common: {ValueList: {entity: 'PCH04_STATUS_POP1', }}); 
};