namespace COMM;


  @title: '{i18n>CUID_FILED}'
aspect CUID_FILED {
  @title: '{i18n>CD_TIME}' CD_TIME   : DateTime   @cds.on.insert: $now; //创建时间
  @title: '{i18n>UP_TIME}' UP_TIME   : DateTime   @cds.on.update: $now; //修改时间
  @title: '{i18n>CD_BY}' CD_BY       : String(36) @cds.on.insert: $user; //创建人
  @title: '{i18n>UP_BY}' UP_BY       : String(36) @cds.on.update: $user; //修改人
  @title: '{i18n>UP_FLAG}' UP_FLAG   : Integer default 0; //修改标记
  @title: '{i18n>DEL_FLAG}' DEL_FLAG : String(1) default 'N'; //删除标记
}

  @title: '{i18n>COMMON_UP_FILED}'
aspect UP_FILED {
  @title: '{i18n>UP_TIME}' UP_TIME : DateTime   @cds.on.update: $now; //修改时间
  @title: '{i18n>UP_BY}' UP_BY     : String(36) @cds.on.update: $user; //修改人
}

aspect IF_CUID_FILED {
  @title: '{i18n>CD_TIME}' CD_TIME         : DateTime   @cds.on.insert: $now; //创建时间
  @title: '{i18n>UP_TIME}' UP_TIME         : DateTime   @cds.on.update: $now; //修改时间
  @title: '{i18n>CD_BY}' CD_BY             : String(36) @cds.on.insert: $user; //创建人
  @title: '{i18n>UP_BY}' UP_BY             : String(36) @cds.on.update: $user; //修改人
  @title: '{i18n>SAP_CD_TIME}' SAP_CD_TIME : DateTime; //创建时间
  @title: '{i18n>SAP_UP_TIME}' SAP_UP_TIME : DateTime; //修改时间
  @title: '{i18n>SAP_CD_BY}' SAP_CD_USER   : String(15); //创建人
  @title: '{i18n>SAP_UP_BY}' SAP_UP_USER   : String(15); //修改人
  @title: '{i18n>DEL_FLAG}' DEL_FLAG       : String(1) default 'N'; //删除标记
}
