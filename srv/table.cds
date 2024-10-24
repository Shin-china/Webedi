using {SYS} from '../db/model-sys';
using {MST} from '../db/model-mst';
using {PCH} from '../db/model-pch';
//V2的缺点：不能接收基本类型以外的参数。
//V4的缺点：UI5中 smarttable 不认v4的odata

@endpoints: [{
  path    : 'TableService',
  protocol: 'odata-v2'
}]
service TableService {

    entity SYS_T01_USER as projection on SYS.T01_USER;
    entity SYS_T02_ROLE as projection on SYS.T02_ROLE;
    entity SYS_T04_USER_2_ROLE as projection on SYS.T04_USER_2_ROLE;
    entity SYS_T06_DOC_NO as projection on SYS.T06_DOC_NO;
    entity SYS_T13_ATTACHMENT  as projection on SYS.T13_ATTACHMENT;
    entity MST_T02_SAP_PLANT as projection on MST.T02_SAP_PLANT;
    entity MST_T03_SAP_BP as projection on MST.T03_SAP_BP;
    //entity SYS_T14_USER_2_BP as projection on SYS.T14_USER_2_BP;
    entity SYS_T11_MAIL_TEMPLATE as projection on SYS.T11_MAIL_TEMPLATE;
    entity T01_PO_H as projection on PCH.T01_PO_H;
    entity T02_PO_D as projection on PCH.T02_PO_D;
    entity T03_PO_C as projection on PCH.T03_PO_C;
    entity MST_T05_SAP_BP_PURCHASE as projection on MST.T05_SAP_BP_PURCHASE;
  //  entity SYS_T20_CONFIG as projection on SYS.T20_CONFIG;
    entity SYS_T12_CONFIG as projection on SYS.T12_CONFIG;
    entity SYS_T09_USER_2_PLANT as 
      projection on SYS.T09_USER_2_PLANT{
        *,
        TO_USER : redirected to SYS_T01_USER,
        TO_PLANT : redirected to MST_T02_SAP_PLANT
      };

    entity SYS_T14_USER_2_BP as 
      projection on SYS.T14_USER_2_BP{
        *,
        TO_USER : redirected to SYS_T01_USER,
        TO_BP : redirected to MST_T03_SAP_BP
      };

  entity USER_CODE               as
    select from SYS_T01_USER 
    distinct{
      key case
        when
          $user is null
        then
          'anonymous'
        else
          $user
      end as user : String(100)
    }

  //画面
  action PCH01_CHECK_DATA(shelfJson : String)         returns String; //棚番一括アップロード画面：对于上传数据check
  action PCH01_SAVE_DATA(shelfJson : String)          returns String; //棚番一括アップロード画面：对于上传数据保存

  //Excel测试用
  action EXCEL_TEST(content : String)          returns  LargeBinary; //Excel测试用

}