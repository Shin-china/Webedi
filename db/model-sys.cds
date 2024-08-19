namespace SYS;

using {cuid} from '@sap/cds/common';
using {COMM.CUID_FILED as CUID_FILED} from './model-common';
using {COMM.UP_FILED as UP_FILED} from './model-common';
using {MST} from './model-mst';

entity T01_USER : cuid{
  @title: '{i18n>USER_ID}' USER_ID                     : String(50);    //USER ID:Email
  @title: '{i18n>USER_TYPE}' USER_TYPE                 : String(1);     //User Type: 1.Internal Employee 2.External Vendor
  @title: '{i18n>BP_NUMBER}' BP_NUMBER                 : String(10);   //BP Number
  @title: '{i18n>USER_STATUS}' USER_STATUS             : String(1);     //User Status: 1.Active 2.Inactive 3.Locked
  @title: '{i18n>USER_NAME}' USER_NAME                 : String(50);   //User Name
  @title: '{i18n>VALID_DATE_FROM}' VALID_DATE_FROM     : Date not null; //有效期(From)
  @title: '{i18n>VALID_DATE_TO}' VALID_DATE_TO         : Date;          //有效期(To)
  @title: '{i18n>CD_TIME}' CD_TIME                     : DateTime   @cds.on.insert: $now; //创建时间
  @title: '{i18n>UP_TIME}' UP_TIME                     : DateTime; //修改时间
  @title: '{i18n>CD_BY}' CD_BY                         : String(36) @cds.on.insert: $user; //创建人
  @title: '{i18n>UP_BY}' UP_BY                         : String(36); //修改人
  @title: '{i18n>UP_FLAG}' UP_FLAG                     : Integer default 0; //修改标记
  @title: '{i18n>DEL_FLAG}' DEL_FLAG                   : String(1) default 'N'; //删除标记

  USER_2_ROLES                                         : Composition of many T04_USER_2_ROLE
                                                           on USER_2_ROLES.USER_ID = ID; //ITEM信息

  USER_2_PLANT                                         : Composition of many T09_USER_2_PLANT
                                                           on USER_2_PLANT.USER_ID = ID; //ITEM信息
}

entity T02_ROLE : CUID_FILED {
  @title: '{i18n>ROLE_ID}' key ID                                   : UUID; //
                               @title: '{i18n>ROLE_CODE}' ROLE_CODE : String(20); //
                               @title: '{i18n>ROLE_NAME}' ROLE_NAME : localized String(200); //
}


entity T03_AUTH {
  @title: '{i18n>AUTH_ID}' key ID                                   : String(50); //权限ID
                               @title: '{i18n>AUTH_NAME}' AUTH_NAME : localized String(200); //权限名称
                               @title: '{i18n>MENU_ID}' MENU_ID     : String(50); //菜单code

                               TO_MENU                              : Association to one T10_MENU
                                                                        on TO_MENU.MENU_ID = MENU_ID; //

}

entity T04_USER_2_ROLE : cuid, UP_FILED {
  @title: '{i18n>USER_ID}' USER_ID : String(36); //
  @title: '{i18n>ROLE_ID}' ROLE_ID : String(36); //

  TO_ROLE                          : Association to one T02_ROLE
                                       on TO_ROLE.ID = ROLE_ID; //

  TO_USER                          : Association to one T01_USER
                                       on TO_USER.ID = USER_ID; //
}


entity T05_ROLE_2_AUTH : UP_FILED {
  @title: '{i18n>ID}' key ID                               : String(36); //ID
                          @title: '{i18n>ROLE_ID}' ROLE_ID : String(36); //
                          @title: '{i18n>AUTH_ID}' AUTH_ID : String(36); //权限ID

                          TO_ROLE                          : Association to one T02_ROLE
                                                               on TO_ROLE.ID = ROLE_ID; //

                          TO_AUTH                          : Association to one T03_AUTH
                                                               on TO_AUTH.ID = AUTH_ID; //


}

entity T06_DOC_NO {
  @title: '{i18n>DOC_KEY}' key DOC_KEY                                : String(100); //取号KEY
                               @title: '{i18n>DOC_DATE}' DOC_DATE     : String(20); //日期
                               @title: '{i18n>DOC_PREFEX}' DOC_PREFEX : String(20); //单号前缀
                               @title: '{i18n>DOC_INDEX}' DOC_INDEX   : Decimal(13, 0) default 0 not null; //当前序号
                               @title: '{i18n>UP_FLAG}' UP_FLAG       : Integer default 0 not null; //修改标记
                               @title: '{i18n>DOC_REMARK}' DOC_REMARK : String(50); //备注
}

entity T07_COM_OP_H : cuid, CUID_FILED {
  @title: '{i18n>H_CODE}' H_CODE       : String(20) not null; //汎用マスタ区分
  @title: '{i18n>H_NAME}' H_NAME       : String(200); //描述
  @title: '{i18n>BE_CHANGE}' BE_CHANGE : Boolean; //修正可能
  @title: '{i18n>UP_FLAG}' UP_FLAG     : Integer default 0; //修改标记


  TO_ITEMS                             : Composition of many T08_COM_OP_D
                                           on TO_ITEMS.H_ID = ID; //ITEM信息
}


entity T08_COM_OP_D : cuid, CUID_FILED {
  @title: '{i18n>H_ID}' H_ID         : String(36) not null; //

  @title: '{i18n>H_CODE}' H_CODE     : String(20); //汎用マスタ区分
  @title: '{i18n>D_CODE}' D_CODE     : String(20); //汎用明細番号
  @title: '{i18n>D_NO}' D_NO         : Integer; //順番
  @title: '{i18n>D_NAME}' D_NAME     : String(100); //汎用明細テキスト


  @title: '{i18n>VALUE01}' VALUE01   : String(100); //值1
  @title: '{i18n>VALUE02}' VALUE02   : String(100); //值2
  @title: '{i18n>VALUE03}' VALUE03   : String(100); //值3
  @title: '{i18n>VALUE04}' VALUE04   : String(100); //值4
  @title: '{i18n>VALUE05}' VALUE05   : String(100); //值5

  @title: '{i18n>DEL_FLAG}' DEL_FLAG : String(1) default 'N'; //删除标记


  TO_HEAD                            : Association to one T07_COM_OP_H
                                         on TO_HEAD.ID = H_ID; //
}


entity T09_USER_2_PLANT : cuid, UP_FILED {
  @title: '{i18n>USER_ID}' USER_ID   : String(36); //
  @title: '{i18n>PLANT_ID}' PLANT_ID : String(4); //

  TO_USER                            : Association to one T01_USER
                                         on TO_USER.ID = USER_ID; //
  TO_PLANT                           : Association to one MST.T02_SAP_PLANT
                                         on TO_PLANT.PLANT_ID = PLANT_ID; //

}



entity T10_MENU {
  @title: '{i18n>MENU_ID}' key MENU_ID                                          : String(20); //菜单ID
                               @title: '{i18n>MENU_NAME}' MENU_NAME             : localized String(200); //菜单名称
                               @title: '{i18n>MENU_SUB_NAME}' MENU_SUB_NAME     : localized String(200); //菜单SHORT名称
                               @title: '{i18n>MENU_ICON}' MENU_ICON             : String(100); //菜单图标
                               @title: '{i18n>MENU_MODULE}' MENU_MODULE         : String(10); //菜单模块
                               @title: '{i18n>MENU_ROUTE_ID}' MENU_ROUTE_ID     : String(100); //ROUTE ID
                               @title: '{i18n>MENU_UNIT}' MENU_UNIT             : String(20); //
                               @title: '{i18n>MENU_FOOTER}' MENU_FOOTER         : String(20); //
                               @title: '{i18n>MENU_VALUECOLOR}' MENU_VALUECOLOR : String(15); //
                               @title: '{i18n>MENU_VALUE}' MENU_VALUE           : String(20); //
                               @title: '{i18n>MENU_TYPE}' MENU_TYPE             : String(3); //
                               @title: '{i18n>MENU_SORT}' MENU_SORT             : Integer; //排序  功能排序
                               @title: '{i18n>SHOW_IN_MENU}' SHOW_IN_MENU       : Boolean default true; //
}

entity T11_MAIL_TEMPLATE{
    @title: '{i18n>TEMPLATE_ID}' key TEMPLATE_ID : String(36); //邮件ID
    @title: '{i18n>MAIL_NAME}' MAIL_NAME :  String(200); //邮件名称
    @title: '{i18n>MAIL_TITLE}' MAIL_TITLE : String(50); //邮件标题
    @title: '{i18n>MAIL_CONTENT}' MAIL_CONTENT :  String(2000); //邮件内容
}

entity T12_CONFIG {
  @title: '{i18n>CON_CODE}' key CON_CODE                             : String(50); //   系统配置
                                @title: '{i18n>CON_VALUE}' CON_VALUE : String(200); //   系统配置值
                                @title: '{i18n>CON_DESC}' CON_DESC   : String(500); //  系统配置描述
                                @title: '{i18n>CON_SORT}' CON_SORT   : Integer; //   排序
}
