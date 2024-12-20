namespace MST;

using {COMM.IF_CUID_FILED as IF_CUID_FILED} from './model-common';
using {COMM.CUID_FILED as CUID_FILED} from './model-common';
using {cuid} from '@sap/cds/common';

entity T01_SAP_MAT : IF_CUID_FILED { //物料基本信息
  @title: '{i18n>MAT_ID}' key MAT_ID                                 : String(40) not null; //物料编号
                              @title: '{i18n>MAT_UNIT}' MAT_UNIT     : String(3); //物料基本单位
                              @title: '{i18n>MAT_TYPE}' MAT_TYPE     : String(4); //物料类型区别字符MTART
                              @title: '{i18n>MAT_GROUP}' MAT_GROUP   : String(9); //物料组MATKL
                              @title: '{i18n>MAT_STATUS}' MAT_STATUS : String(1); //状态LVORM
                              @title: '{i18n>MAT_NAME}' MAT_NAME     : String(40); //物料名称
                              @title: '{i18n>MANU_CODE}' MANU_CODE   : String(10); //製造業者
                              @title: '{i18n>MANU_MATERIAL}' MANU_MATERIAL     : String(40); //制造商物料编号
                              @title: '{i18n>CUST_MATERIAL}' CUST_MATERIAL     : String(40); //制造商物料编号

                                TO_SAP_BP                           : Association to one T03_SAP_BP
                                                                       on TO_SAP_BP.BP_ID = MANU_CODE;
}

entity T02_SAP_PLANT : IF_CUID_FILED { //工厂表
  @title: '{i18n>PLANT_ID}' key PLANT_ID                               : String(4) not null; //工厂code
                                @title: '{i18n>PLANT_NAME}' PLANT_NAME : String(40); //工厂名称


                                TO_LOCATIONS                           : Association to many T04_SAP_LOC
                                                                           on TO_LOCATIONS.PLANT_ID = PLANT_ID
}

entity T03_SAP_BP : IF_CUID_FILED { //工厂表
  @title: '{i18n>SUPPLIER_CODE}' key BP_ID                                  : String(10) not null; //BPcode
                                     @title: '{i18n>BP_TYPE}' BP_TYPE       : String(5); //BP类型
                                     @title: '{i18n>BP_NAME1}' BP_NAME1     : String(40); //BP Name 1
                                     @title: '{i18n>BP_NAME2}' BP_NAME2     : String(40); //BP Name 1
                                     @title: '{i18n>BP_NAME3}' BP_NAME3     : String(20); //BP Name 1
                                     @title: '{i18n>BP_NAME4}' BP_NAME4     : String(70); //BP Name 1
                                    @title: '{i18n>SEARCH2}' SEARCH2        : String(40); //检索语句
                                    @title: '{i18n>FAX}' FAX                : String(40); //FAX
                                     @title: '{i18n>TEL}' TEL               : String(40); //TEL

                                     @title: '{i18n>LOG_NO}' LOG_NO         : String(40); //登録番号
                                     @title: '{i18n>POSTCODE}' POSTCODE     : String(40); //郵便番号
                                     @title: '{i18n>REGIONS}' REGIONS       : String(120); //地域(都道府県)
                                     @title: '{i18n>PLACE_NAME}' PLACE_NAME : String(40); //地名/番地-号

}


entity T04_SAP_LOC : IF_CUID_FILED { //保管场所表
  @title: '{i18n>PLANT_ID}' key PLANT_ID                           : String(4) not null; //工厂
  @title: '{i18n>LOC_ID}' key   LOC_ID                             : String(4) not null; //保管场所编号
                                @title: '{i18n>LOC_NAME}' LOC_NAME : String(40); //保管场所名称

                                TO_PLANT                           : Association to one T02_SAP_PLANT
                                                                       on TO_PLANT.PLANT_ID = PLANT_ID;

}


entity T05_SAP_BP_PURCHASE : IF_CUID_FILED { //BP采购视图
  @title: '{i18n>PURCHASE_ORG}' key PURCHASE_ORG                       : String(4) not null; //購買組織
  @title: '{i18n>SUPPLIER}' key SUPPLIER : String(10) not null; //仕入先
                                    @title: '{i18n>ZABC}' ZABC         : String(1); //ABC区分
                                    //供应商名


}


entity T06_MAT_PLANT : IF_CUID_FILED { //BP采购视图
  @title: '{i18n>MAT_ID}' key MAT_ID                       : String(40) not null; //物料编号
  @title: '{i18n>PLANT_ID}' key PLANT_ID : String(4) not null; //工厂code
                                    @title: '{i18n>IMP_COMP}' IMP_COMP         : String(1); //重要组件


}