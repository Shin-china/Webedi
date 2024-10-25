using {TableService as view} from './table';
using {PCH} from '../db/model-pch';

//MM-012 FCレポート

extend service TableService {


    entity PCH09_LIST as
        select from PCH.T09_FORCAST as T01 distinct {

            key T01.PUR_GROUP,
            key T01.SUPPLIER,
            key T01.MATERIAL,
            key T01.ARRANGE_END_DATE,
            key T01.PLANT,
                T01.PUR_GROUP_NAME,
                T01.NAME1,
                T01.MATERIAL_TEXT,
                T01.SUPPLIER_MATERIAL,
                T01.DELIVARY_DAYS,
                T01.MIN_DELIVERY_QTY,
                T01.MANUF_CODE,
                T01.ARRANGE_START_DATE,
                SUM(
                    T01.ARRANGE_QTY
                ) as ARRANGE_QTY_SUM : Decimal(18, 3)
        }
        group by
            PUR_GROUP,
            SUPPLIER,
            MATERIAL,
            ARRANGE_END_DATE,
            PLANT,
            PUR_GROUP_NAME,
            NAME1,
            MATERIAL_TEXT,
            SUPPLIER_MATERIAL,
            DELIVARY_DAYS,
            MIN_DELIVERY_QTY,
            MANUF_CODE,
            ARRANGE_START_DATE


};
