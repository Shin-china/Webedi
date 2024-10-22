using {TableService as view} from './table';
using {PCH} from '../db/model-pch';

//MM-012 FCレポート

extend service TableService {


    entity PCH09_LIST as
        select from PCH.T09_FORCAST as T01 distinct {

            key T01.PR_NUMBER,
            key T01.D_NO,
                T01.PUR_GROUP,
                T01.PUR_GROUP_NAME,
                T01.SUPPLIER,
                T01.NAME1,
                T01.MATERIAL,
                T01.MATERIAL_TEXT,
                T01.SUPPLIER_MATERIAL,
                T01.DELIVARY_DAYS,
                T01.MIN_DELIVERY_QTY,
                T01.MANUF_CODE,
                T01.ARRANGE_START_DATE,
                T01.ARRANGE_END_DATE,
                T01.ARRANGE_QTY,
                T01.PLANT,
                T01.SUPPLIER_TEL,
                T01.STATUS,

        }

};
