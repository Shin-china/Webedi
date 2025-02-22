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
  file_type         : LargeString;
  file_name         : String(50);
  object_type       : String(10);
  mime_type         : LargeString;
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

    action aaa()                                                          returns String; //bp ceshi  

    action IF_S4_BP()                                                     returns String; //IF_S4_BP bp接口外部启动
    action IF_S4_BPPURCHASE()                                             returns String; //IF_S4_BPPURCHASE 采购订单接口外部启动
    action IF_S4_MST()                                                    returns String; //IF_S4_MST 主数据接口外部启动
    action IF_S4_PO()                                                     returns String; //IF_S4_PO 采购订单接口外部启动
    action IF_S4_PR()                                                     returns String; //IF_S4_PR 采购订单接口外部启动
    action IF_S4_PAY()                                                    returns String; //IF_S4_PAY 采购订单接口外部启动

}