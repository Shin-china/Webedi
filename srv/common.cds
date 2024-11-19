using {PCH} from '../db/model-pch';

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
  object            : String(40);
  value             : LargeString;
  file_type         : String(64);
  file_name         : String(50);
  object_type       : String(10);
}
service Common {

    entity PCH_T06_QUOTATION_H as projection on PCH.T06_QUOTATION_H;
    entity PCH_T07_QUOTATION_D as projection on PCH.T07_QUOTATION_D;

    action sendEmail(emailJson : array of  mailJson) returns String;//Send Email
    action attachemnt(templateId : String) returns String;//Get Mail Template
    action getS3List(key : String) returns String;//Get S3 List
    action s3Attachment(attachmentJson : array of attachmentJson) returns String;//S3 Insert Attachment
    action s3uploadAttachment(attachmentJson : array of attachmentJson) returns String;//S3 Insert Attachment
    action s3DownloadAttachment(attachmentJson : array of attachmentJson) returns LargeBinary;//S3 Insert Attachment
    action deleteS3Object(key : String) returns String;//S3 Insert Attachment

    action pch06BatchImport(pch06 : array of PCH_T06_QUOTATION_H)               returns String; //IFM054 購買見積依頼受信
    action pch06BatchSending(json : String)               returns String; //IFM055 購買見積結果送信
    

}