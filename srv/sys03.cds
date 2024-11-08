using {TableService} from '../srv/table.cds';
using {SYS} from '../db/model-sys';

extend service TableService with {


    entity SYS03_DICT_LIST as
        select from SYS.T08_COM_OP_D as T08 {
            key T08.ID,
                T08.H_ID       as H_ID,
                T08.H_CODE     as H_CODE,
                T08.D_CODE     as D_CODE,
                T08.D_NO       as D_NO,
                TO_HEAD.H_NAME as H_NAME,
                T08.D_NAME     as D_NAME,
                T08.VALUE01    as VALUE01,
                T08.VALUE02    as VALUE02,
                T08.VALUE03    as VALUE03,
                T08.VALUE04    as VALUE04,
                T08.VALUE05    as VALUE05,
                T08.DEL_FLAG   as DEL_FLAG

        }
        order by
            H_CODE,
            D_NO;


    action SYS03_DELETE_ITEMS(parms : String) returns String;

}
