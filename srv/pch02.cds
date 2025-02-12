using {TableService as view} from './table';
using {SYS} from '../db/model-sys';
using {PCH} from '../db/model-pch';

// MM-028 納期回答照会

extend service TableService {

    entity PCH_T02_USER as
            select from PCH.T01_PO_H as T01
            inner join PCH.T02_PO_D as T02
                on(
                    T01.PO_NO = T02.PO_NO
                )
            inner join PCH.T03_PO_C as T03
                on(
                        T02.PO_NO = T03.PO_NO
                    and T02.D_NO  = T03.D_NO
                )
            join view.SYS_T01_USER as Tu
                on Tu.USER_ID = COALESCE($user, 'anonymous')
                and (Tu.USER_TYPE = '1' or (T01.SUPPLIER in (select BP_ID from view.AUTH_USER_BP   ) and Tu.USER_TYPE = '2') )

  
            distinct {
                // key T02.PO_NO || T02.D_NO || T03.SEQ || T04.ID as KEYID  : String,
                key COALESCE(T01.PO_NO, '') || COALESCE(CAST(T02.D_NO AS String), '') || COALESCE(CAST(T03.SEQ AS String), '') as KEYID : String,
                    T01.PO_NO,              // 発注番号
                    T02.D_NO,               // 明細番号
                    T03.SEQ,                // 連続番号
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
                    T02.STORAGE_LOC || '' || T02.STORAGE_TXT as STORAGE_NAME : String(255), // 納品先名
                    T02.DEL_FLAG,           // 削除フラグ
                    T03.ExtNumber,          // 参照
                    // T02.PO_NO || REPEAT('0', 5 - LENGTH(CAST(T02.D_NO AS String))) || CAST(T02.D_NO AS String) as NO_DETAILS : String(15), // 購買伝票\明細NO		
                    // T02.PO_NO || LPAD(CAST(T02.D_NO AS String), 5, '0') AS NO_DETAILS : String(15),
                    T02.PO_NO || T02.D_NO as NO_DETAILS : String(15), // 購買伝票\明細NO	
						

            }

             entity PCH_T02_USER_2 as
            select from PCH.T08_UPLOAD as T01
            inner join PCH.T01_PO_H as T02
                on(T01.PO_NO = T02.PO_NO)
           join view.SYS_T01_USER as Tu
                on Tu.USER_ID = COALESCE($user, 'anonymous')
                and (Tu.USER_TYPE = '1' or (T02.SUPPLIER in (select BP_ID from view.AUTH_USER_BP   ) and Tu.USER_TYPE = '2') )


    distinct {

                    key T01.PO_NO,          // 発注番号
                    key T01.D_NO,           // 明細番号
                    key T01.ID,             // key ID
                    T01.MAT_ID,             // 品目コード
                    T01.QUANTITY,           // 納品数
                    T01.ExtNumber,          // 参照
                    // T01.PO_NO || REPEAT('0', 5 - LENGTH(CAST(T01.D_NO AS String))) || CAST(T01.D_NO AS String) as NO_DETAILS : String(15), // 購買伝票\明細NO			
                    T01.PO_NO || T01.D_NO as NO_DETAILS : String(15), // 購買伝票\明細NO					
                    T01.MAT_NAME,			// テキスト（短）											
                    T01.PLANT_ID,			// プラント						
                    T01.LOCATION_ID,		// 保管場所						
                    T01.INPUT_DATE,			// 納入日付						
                    T01.INPUT_QTY,			// 納入数											
                    T01.CD_DATE,			// 登録日付						
                    T01.CD_DATE_TIME,	    // 時刻	
                    T01.CD_BY				// 登録者

    }

        // union all
        //     select from PCH.PCH_T01_PO_H as T01
        //     left join PCH.PCH_T02_PO_D as T02
        //         on(
        //             T01.PO_NO = T02.PO_NO
        //         )
        //     left join PCH.PCH_T03_PO_C as T03
        //         on(
        //                 T02.PO_NO = T03.PO_NO
        //             and T02.D_NO  = T03.D_NO
        //         )
         

        //     distinct {
        //         key T02.PO_NO,            // 発注番号
        //         key T02.D_NO,             // 明細番号
        //             T01.SUPPLIER,         // 仕入先コード
        //             T02.MAT_ID,           // 品目コード
        //             T03.STATUS,           // ステータス
        //             T02.PO_D_TXZ01,       // 品目テキスト
        //             T02.PO_PUR_QTY,       // 発注数量
        //             T02.PO_PUR_UNIT,      // 単位
        //             T02.SUPPLIER_MAT,     // 仕入先品目コード
        //             T03.DELIVERY_DATE,    // 納品日
        //             T03.QUANTITY,         // 納品数
        //             T02.DEL_PRICE,        // 発注単価
        //             T02.DEL_AMOUNT,       // 発注金額
        //             T02.MEMO,             // 備考
        //             T02.STORAGE_LOC || '' || T02.STORAGE_TXT as STORAGE_NAME : String(255), // 納品先名
        //             T03.SEQ,              //連続番号
        //             T03.DEL_FLAG          //削除フラグ
        //     }

 action PCH02_CONFIRMATION_REQUEST(parms : String) returns String;

}

annotate TableService.PCH_T02_USER with {
  
  STATUS @(Common: {ValueList: {entity: 'PCH02_STATUS_POP', }}); 

};

annotate TableService.PCH_T02_USER with {
    PO_NO @(Common : {ValueList : {
        entity     : 'PCH_T03_PO_POP',
        Parameters : [

            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'SUPPLIER'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'PCH03_BP_NAME1'
            },
            {
                $Type             : 'Common.ValueListParameterInOut',
                LocalDataProperty : 'PO_NO',
                ValueListProperty : 'PO_NO'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'ID'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'PO_TYPE'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'MAT_ID'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'PO_DATE'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'STATUS'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'SAP_CD_BY_TEXT'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'PO_D_DATE'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'ZABC'
            }

        ]
    }});
   
};

annotate TableService.PCH_T02_USER with {
     SUPPLIER @(Common : {ValueList : {
        entity     : 'MST_T03_SAP_BP_POP',
        Parameters : [

            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'BP_NAME1'
            },
            {
                $Type             : 'Common.ValueListParameterInOut',
                LocalDataProperty : 'SUPPLIER',
                ValueListProperty : 'BP_ID'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'BP_TYPE'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'SEARCH2'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'FAX'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'TEL'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'POSTCODE'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'REGIONS'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'PLACE_NAME'
            },

        ]
    }});
   
};