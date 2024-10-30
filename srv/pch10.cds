using {TableService as view} from './table';
using {PCH} from '../db/model-pch';

//MM-012 FCレポート

extend service TableService {


    entity PCH10_Header as
        select from PCH.T06_QUOTATION_H as T01 distinct {

            key T01.ID,
                T01.QUO_NUMBER,
                T01.CUSTOMER,
                T01.MACHINE_TYPE,
                T01.Item,
                T01.QUANTITY,
                T01.TIME,
                T01.LOCATION,
                T01.VALIDATE_START,
                T01.VALIDATE_END,
                T01.PLANT_ID,
                T01.SALES_NUMBER,
                T01.STATUS,
                T01.TOTAL_JPY,
                T01.TOTAL_USD,
                T01.TOTAL_CNY,
                T01.TOTAL_HKD,
                T01.TOTAL_THB,
                T01.CD_DATE,
                T01.CD_DATE_TIME

        }

};
