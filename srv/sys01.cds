using {TableService} from '../srv/table.cds';
using {SYS} from '../db/model-sys';
using {MST} from '../db/model-mst';

extend service TableService with {
  action SYS01_USER_addUser(userJson : String) returns String; //Insert User
  action SYS01_USER_editUser(userJson : String) returns String;//Edit User
  action SYS01_USER_deleteUser(userJson : String) returns String;//Delete User
  

}
