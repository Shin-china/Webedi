using {TableService as VIEW} from '../srv/tables';
using {SYS} from '../db/model-sys';

extend service TableService with {

  entity SYS06_LIST  as
    select from SYS.T15_IF_LOG as T15
    join SYS.T11_IF_MANAGER as T11
      on T15.IF_CODE = T11.IF_CODE

    {
      key T15.ID,
          T15.IF_CODE,
          T11.IF_NAME,
          T11.SYSTEM_FROM,
          T11.SYSTEM_TO,
          T15.IF_PARA,
          T15.IF_RESULT,
          T15.IF_MSG,
          T15.TOTAL_NUM,
          T15.SUCCESS_NUM,
          T15.IGNORE_NUM,
          T15.ERROR_NUM,
          T15.START_TIME,
          T15.FINISH_TIME,
          T15.CD_BY,

    };

  entity T13_IF_CODE as
    select from SYS.T11_IF_MANAGER {
      key IF_CODE,
          IF_NAME
    }

}

annotate TableService.T13_IF_CODE with {
  IF_CODE @Common.Text: {$value: IF_NAME}
};

annotate TableService.SYS06_LIST with {
  IF_CODE @(Common: {ValueList: {entity: 'T13_IF_CODE', }});
}
