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
  value             : LargeString;
}

type attachmentJson :{
  object            : String(20);
  value             : LargeString;
  file_type         : String(20);
  file_name         : String(50);
  object_type       : String(10);
}
service Common {

    action sendEmail(emailJson : array of  mailJson) returns String;//Send Email
    action attachemnt(templateId : String) returns String;//Get Mail Template
    action getS3List(key : String) returns String;//Get S3 List
    action s3Attachment(attachmentJson : array of attachmentJson) returns String;//S3 Insert Attachment
    action s3uploadAttachment(attachmentJson : array of attachmentJson) returns String;//S3 Insert Attachment
    action s3DownloadAttachment(attachmentJson : String) returns Binary;//S3 Insert Attachment

}