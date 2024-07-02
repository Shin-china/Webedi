using {TableService} from '../srv/table.cds';

extend service TableService with {
  action SYS01_USER_addUser(userJson : String) returns String; //Insert User
  action SYS01_USER_editUser(userJson : String) returns String;//Edit User
}
