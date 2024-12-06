using {TableService} from '../srv/table';
using {MST} from '../db/model-mst';
using {SYS} from '../db/model-sys';


extend service TableService with {

  entity PCH01_STATUS_POP_1 as
    select from SYS.T07_COM_OP_H T01
    inner join SYS.T08_COM_OP_D T02
      on T01.H_CODE = T02.H_CODE

    {
          D_NAME  as NAME,
      key VALUE01 as VALUE
    }
    where
      T01.H_CODE = 'PCH01_STATUS';

  entity PCH02_STATUS_POP   as
    select from SYS.T07_COM_OP_H T03
    inner join SYS.T08_COM_OP_D T04
      on T03.H_CODE = T04.H_CODE

    {
      key VALUE01 as VALUE,
          D_NAME  as NAME

    }
    where
      T03.H_CODE = 'PCH02_STATUS';

  entity MST_BP_ZABC_POP    as
    select from SYS.T07_COM_OP_H T03
    inner join SYS.T08_COM_OP_D T04
      on T03.H_CODE = T04.H_CODE

    {
          D_NAME  as NAME,
      key VALUE01 as VALUE
    }
    where
      T03.H_CODE = 'BP_ZABC';

  entity PCH03_STATUS_POP   as
    select from SYS.T07_COM_OP_H T03
    inner join SYS.T08_COM_OP_D T04
      on T03.H_CODE = T04.H_CODE

    {
          D_NAME  as NAME,
      key VALUE01 as VALUE
    }
    where
      T03.H_CODE = 'PCH03_STATUS';

  entity PO_TYPE_POP        as
    select from SYS.T07_COM_OP_H T03
    inner join SYS.T08_COM_OP_D T04
      on T03.H_CODE = T04.H_CODE

    {
      key VALUE01 as VALUE,
          D_NAME  as NAME,
    }
    where
      T03.H_CODE = 'PO_TYPE';

  entity PCH04_STATUS_POP1  as
    select from SYS.T07_COM_OP_H T03
    inner join SYS.T08_COM_OP_D T04
      on T03.H_CODE = T04.H_CODE

    {
      key VALUE01 as VALUE,
          D_NAME  as NAME

    }
    where
      T03.H_CODE = 'PCH04_STATUS';


  entity PCH10_STATUS_POP   as
    select from SYS.T07_COM_OP_H T03
    inner join SYS.T08_COM_OP_D T04
      on T03.H_CODE = T04.H_CODE

    {
      key VALUE01 as VALUE,
          D_NAME  as NAME

    }
    where
      T03.H_CODE = 'PCH10_STATUS';

}


annotate TableService.PCH01_STATUS_POP_1 with {
  VALUE @Common.Text: {$value: NAME}
};


annotate TableService.PCH02_STATUS_POP with {
  VALUE @Common.Text: {$value: NAME}
};

annotate TableService.MST_BP_ZABC_POP with {
  VALUE @Common.Text: {$value: NAME}
};

annotate TableService.PCH03_STATUS_POP with {
  VALUE @Common.Text: {$value: NAME}
};

annotate TableService.PCH04_STATUS_POP1 with {
  VALUE @Common.Text: {$value: NAME}
};

annotate TableService.PO_TYPE_POP with {
  VALUE @Common.Text: {$value: NAME}
};
