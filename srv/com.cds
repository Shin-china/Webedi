using {TableService} from '../srv/table';
using {MST} from '../db/model-mst';
using {SYS} from '../db/model-sys';


extend service TableService with {
  //
  entity PCH01_STATUS_POP_1      as
    select from SYS.T07_COM_OP_H T01
    inner join SYS.T08_COM_OP_D T02
    ON T01.H_CODE=T02.H_CODE
    
    {
      key D_NAME as NAME,
          VALUE01 as VALUE
    }
    where
      T01.H_CODE = 'PCH01_STATUS';

  entity PCH02_STATUS_POP      as
    select from SYS.T07_COM_OP_H T03
    inner join SYS.T08_COM_OP_D T04
    ON T03.H_CODE=T04.H_CODE 
    
    {
      key D_NAME as NAME,
          VALUE01 as VALUE
    }
    where
      T03.H_CODE = 'PCH02_STATUS';
      

}


annotate TableService.PCH01_STATUS_POP_1 with {
  VALUE @Common.Text: {$value: NAME}
};



annotate TableService.PCH02_STATUS_POP with {
  VALUE @Common.Text: {$value: NAME}
};

