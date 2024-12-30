using {TableService as view} from '../srv/table.cds';
using {SYS} from '../db/model-sys';

extend service TableService {

  entity SYS07_EMAIL  as
    select from SYS.T16_EMAIL_H 

    {
      key ID,
         H_CODE,
         H_NAME,
         BP_ID,
         TO_ITEMS.EMAIL_ADDRESS,
         TO_ITEMS.EMAIL_ADDRESS_NAME,
         TO_ITEMS.CD_BY,TO_ITEMS.CD_TIME,TO_ITEMS.UP_BY,TO_ITEMS.UP_TIME

    };

action SYS07_SAVE_DATA(json : String) returns String; //Insert User
action SYS07_CHECK_DATA(json : String) returns String;//Edit User
action SYS07_SAVE_DATA_L(json : String) returns String; //Insert User
action SYS07_DELETE_DATA_L(json : String) returns String; //Insert User
action SYS07_EXCEL(json : String) returns LargeBinary; //Insert User
}
