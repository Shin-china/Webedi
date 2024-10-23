using {TableService as view} from './table';
using {PCH} from '../db/model-pch';
using {MST} from '../db/model-mst';

//MM-036 購買見積登録

extend service TableService {

    entity PCH_T07_QUO_ITEM as
            select from PCH.T06_QUOTATION_H as T01
            left join PCH.T07_QUOTATION_D as T02
                on T01.QUO_NUMBER = T02.QUO_NUMBER
           
            {
                KEY T01.PLANT_ID,                   // プラント
                KEY T02.MATERIAL_NUMBER,            // SAP品目コード
                    T01.QUO_NUMBER,                 // 販売見積バージョン
                    T02.QUO_ITEM,                   // 管理No
                CAST(T02.QUO_NUMBER AS Integer) AS QUO_NUMBER_INT,
                CAST(T02.QUO_ITEM AS Integer) AS QUO_ITEM_INT
            };

    entity PCH_T07_QUO_ITEM_max as
      select from PCH_T07_QUO_ITEM
      
            {
                KEY PLANT_ID,                   // プラント
                KEY MATERIAL_NUMBER,            // SAP品目コード
                MAX(QUO_NUMBER) AS QUO_NUMBER_MAX : Integer,
                MAX(QUO_ITEM) AS QUO_ITEM_MAX  : Integer
            }
            GROUP BY PLANT_ID,MATERIAL_NUMBER;

            action PCH07_CHECK_DATA(shelfJson : String) returns String;
            action PCH07_SAVE_DATA(shelfJson : String) returns String;

};