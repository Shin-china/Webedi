namespace MST;

using {COMM.IF_CUID_FILED as IF_CUID_FILED} from './model-common';
using {COMM.CUID_FILED as CUID_FILED} from './model-common';
using {cuid} from '@sap/cds/common';

entity T02_SAP_PLANT : IF_CUID_FILED { //工厂表
  @title: '{i18n>PLANT_ID}' key PLANT_ID                               : String(4) not null; //工厂code
  @title: '{i18n>PLANT_NAME}' PLANT_NAME                               : String(40); //工厂名称


                                TO_LOCATIONS                           : Association to many T04_SAP_LOC
                                                                           on TO_LOCATIONS.PLANT_ID = PLANT_ID
}

entity MST_T03_SAP_BP : IF_CUID_FILED { //工厂表
  @title: '{i18n>SUPPLIER_CODE}' key BP_ID                             : String(4) not null; //BPcode
  @title: '{i18n>BP_TYPE}'           BP_TYPE                           : String(5); //BP类型
  @title: '{i18n>BP_NAME1}'          BP_NAME1                          : String(40); //BP Name 1
  @title: '{i18n>BP_NAME2}'          BP_NAME2                          : String(40); //BP Name 1
  @title: '{i18n>BP_NAME3}'          BP_NAME3                          : String(20); //BP Name 1
  @title: '{i18n>BP_NAME4}'          BP_NAME4                          : String(70); //BP Name 1
  @title: '{i18n>LOG_NO}'            LOG_NO                            : String(18); //登録番号
}


entity T04_SAP_LOC : IF_CUID_FILED { //保管场所表
  @title: '{i18n>PLANT_ID}' key PLANT_ID                           : String(4) not null; //工厂
  @title: '{i18n>LOC_ID}' key   LOC_ID                             : String(4) not null; //保管场所编号
                                @title: '{i18n>LOC_NAME}' LOC_NAME : String(40); //保管场所名称

                                TO_PLANT                           : Association to one T02_SAP_PLANT
                                                                       on TO_PLANT.PLANT_ID = PLANT_ID;

}
