namespace MST;

using {COMM.IF_CUID_FILED as IF_CUID_FILED} from './model-common';
using {COMM.CUID_FILED as CUID_FILED} from './model-common';
using {cuid} from '@sap/cds/common';

entity T02_SAP_PLANT : IF_CUID_FILED { //工厂表
  @title: '{i18n>PLANT_ID}' key PLANT_ID                               : String(4) not null; //工厂code
                                @title: '{i18n>PLANT_NAME}' PLANT_NAME : String(40); //工厂名称


                                TO_LOCATIONS                           : Association to many T04_SAP_LOC
                                                                           on TO_LOCATIONS.PLANT_ID = PLANT_ID
}


entity T04_SAP_LOC : IF_CUID_FILED { //保管场所表
  @title: '{i18n>PLANT_ID}' key PLANT_ID                           : String(4) not null; //工厂
  @title: '{i18n>LOC_ID}' key   LOC_ID                             : String(4) not null; //保管场所编号
                                @title: '{i18n>LOC_NAME}' LOC_NAME : String(40); //保管场所名称

                                TO_PLANT                           : Association to one T02_SAP_PLANT
                                                                       on TO_PLANT.PLANT_ID = PLANT_ID;

}
