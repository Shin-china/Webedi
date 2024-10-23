using {TableService as view} from './table';
using {PCH} from '../db/model-pch';

//MM-012 FCレポート

extend service TableService {


    entity PCH09_LIST as
        select from PCH.T09_FORCAST as T01 distinct {

            key T01.PUR_GROUP,
            key T01.SUPPLIER,
            key T01.MATERIAL,
                T01.PR_NUMBER,
                T01.D_NO,
                T01.PUR_GROUP_NAME,
                T01.NAME1,
                T01.MATERIAL_TEXT,
                T01.SUPPLIER_MATERIAL,
                T01.DELIVARY_DAYS,
                
                    SUM(
                        T01.MIN_DELIVERY_QTY
                    ) 
                as MIN_DELIVERY_QTY_SUM : Decimal(18, 3),
                T01.MANUF_CODE,
                T01.ARRANGE_START_DATE,
                T01.ARRANGE_END_DATE,
                T01.ARRANGE_QTY,
                T01.PLANT,
                T01.SUPPLIER_TEL,
                T01.STATUS,

        }
        group by PUR_GROUP,SUPPLIER,MATERIAL,PR_NUMBER,D_NO,PUR_GROUP_NAME,NAME1,MATERIAL_TEXT,SUPPLIER_MATERIAL,DELIVARY_DAYS,MANUF_CODE,ARRANGE_START_DATE,ARRANGE_END_DATE,ARRANGE_QTY,PLANT,SUPPLIER_TEL,STATUS

};
