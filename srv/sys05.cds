using {TableService} from '../srv/table.cds';


extend service TableService with{
  action SYS05_MAILTEMP_add(userJson : String) returns String; //Insert & Update User
  action SYS05_MAILTEMP_del(userJson : String) returns String;//Delete User
  action sendEmail(emailJson : String) returns String;//Send Email
}