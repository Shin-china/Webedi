using {TableService as view} from '../srv/table.cds';
using {SYS} from '../db/model-sys';

extend service TableService {

  entity SYS07_EMAIL  as
    select from SYS.T16_EMAIL_H as t01
    inner join SYS.T17_EMAIL_D as t02
    on t01.ID = t02.H_ID

    {
      key t01.ID,
      key t02.ID AS D_ID,
         t01.H_CODE,
         t01.H_NAME,
         t01.BP_ID,
         t01.PLANT_ID,
         t02.EMAIL_ADDRESS,
         t02.EMAIL_ADDRESS_NAME,
         t02.CD_BY,t02.CD_TIME,t02.UP_BY,t02.UP_TIME

    };

action SYS07_SAVE_DATA(json : String) returns String; //Insert User
action SYS07_CHECK_DATA(json : String) returns String;//Edit User
action SYS07_SAVE_DATA_L(json : String) returns String; //Insert User
action SYS07_DELETE_DATA_L(json : String) returns String; //删除明细
action SYS07_DELETE(json : String) returns String; //删除头
action SYS07_EXCEL(json : String) returns LargeBinary; //Insert User
}
annotate TableService.T16_EMAIL_H with {

    H_CODE      @(Common : {FieldControl : #Mandatory});
    H_NAME @(Common : {FieldControl : #Mandatory});
    BP_ID   @(Common : {FieldControl : #Mandatory});

};
annotate TableService.T17_EMAIL_D with {

    EMAIL_ADDRESS      @(Common : {FieldControl : #Mandatory});
    EMAIL_ADDRESS_NAME @(Common : {FieldControl : #Mandatory});

};

annotate TableService.SYS07_EMAIL with {
    BP_ID @(Common : {ValueList : {
        entity     : 'MST_T03_SAP_BP_POP',
        Parameters : [

            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'BP_NAME1'
            },
            {
                $Type             : 'Common.ValueListParameterInOut',
                LocalDataProperty : 'BP_ID',
                ValueListProperty : 'BP_ID'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'BP_TYPE'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'SEARCH2'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'FAX'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'TEL'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'POSTCODE'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'REGIONS'
            },
            {
                $Type             : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'PLACE_NAME'
            },

        ]
    }});
   
};