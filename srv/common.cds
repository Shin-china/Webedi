@endpoints: [{
  path    : 'Common',
  protocol: 'odata-v4'
}]

type mailJson     :{
  TEMPLATE_ID       : String(20);
  MAIL_TO           : String(500);
  MAIL_BODY         : array of mailBody;
}
type mailBody     :{
  object            : String(20);
  value             : String(100);
}

type attachment   :{
  OBJECT_TYPE     :String(10);
}
service Common {

    action sendEmail(emailJson : array of  mailJson) returns String;//Send Email
    action attachemnt(templateId : String) returns String;//Get Mail Template

}