using {TableService as view} from './table';
using {PCH} from '../db/model-pch';
using {MST} from '../db/model-mst';

//MM-036 購買見積登録

extend service TableService {

    entity PCH_T07_UploadAPI_Data as
            select from PCH.T06_QUOTATION_H as T01
            left join PCH.T07_QUOTATION_D as T02
                on(
                     T01.QUO_NUMBER = T02.QUO_NUMBER
                )
            left join MST.T01_SAP_MAT as T03
                on(
                    T02.Material = T03.MAT_NAME
                )
            left join MST.T03_SAP_BP as T04
                on(
                    T02.UWEB_USER = T04.BP_NAME1
                )
            // left join MST.T04_SAP_PLANT as T05
            //     on(
            //         T02.MAKER = T05.MANU_CODE
            //         AND T02.MANUFACT_MATERIAL = T05.MANU_MATERIAL
            //     )

                

            distinct {
            KEY T01.QUO_NUMBER,                 // 販売見積バージョン
                T01.PLANT_ID,                   // プラント
                T01.VALIDATE_START,             // 有効開始日付
                T01.VALIDATE_END,               // 有効終了日付
                T01.CD_TIME,                    // 登録時刻
                T01.CD_BY,                      // 登録者
                T01.CD_DATE,					// 登録日付		
                T02.MATERIAL_NUMBER,            // SAP品目コード
                T02.BP_NUMBER,                  // 仕入先
                T02.QTY,                        // 数量
                T02.UMC_COMMENT_1,              // UMC購買コメント1
                T02.UMC_COMMENT_2,              // UMC購買コメント2
                T02.INITIAL_OBJ,                // イニシャル費用対象
                T02.QUO_ITEM,                   // 管理No
                T02.Material,                   // 品名
                T02.MAKER,                      // メーカ
                T02.MANUFACT_MATERIAL,          // メーカー品番
                T02.UWEB_USER,                  // 仕入先連絡先（WEB EDIの担当）（必須）
                T02.UNIT,                       // Base Unit of Measure(単位：pc or kgなど)
                T02.ORIGINAL_COU,               // 原産国
                T02.PRICE_CONTROL,              // Date of pricing control(価格有効日：発注時or納入時)
                T02.MOQ,                        // MOQ
                T02.Incoterms,                  // Incoterms 1（インコタームズ
                T02.Incoterms_Text,             // Incoterms 1（納入場所）
                T02.LEAD_TIME,                  // LT（日数）
                T02.PRICE,                      // 単価
                T02.CURRENCY,                   // 通貨
                T02.STATUS,                     // ステータス
                // T02.CD_DATE,                    // 登録日付
                // T02.CD_TIME,                    // 登録時刻
                // T02.CD_BY,                      // 登録者

            }
                action PCH07_CHECK_DATA(shelfJson : String) returns String;
                action PCH07_SAVE_DATA(shelfJson : String) returns String;

};