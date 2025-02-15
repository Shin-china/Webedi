using {TableService as view} from './table';
using {PCH} from '../db/model-pch';

//MM-012 FCレポート

extend service TableService {


    entity PCH09_LIST as
        select from PCH.T09_FORCAST as T01 
        join view.SYS_T01_USER as Tu
            on Tu.USER_ID = COALESCE($user, 'anonymous')
                and (Tu.USER_TYPE = '1' or (T01.SUPPLIER in (select BP_ID from view.AUTH_USER_BP   ) and Tu.USER_TYPE = '2') )

        
        distinct {

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

annotate TableService.PCH09_LIST with {
    SUPPLIER @(Common : {ValueList : {
        entity     : 'MST_T03_SAP_BP_POP',
        Parameters : [

            {
                $Type             : 'Common.ValueListParameterInOut',
                LocalDataProperty : 'SUPPLIER',
                ValueListProperty : 'BP_ID'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'BP_NAME1'
            },
        ]
    }});
   
};