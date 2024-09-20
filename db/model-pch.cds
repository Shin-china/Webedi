namespace PCH;

using {COMM.IF_CUID_FILED as IF_CUID_FILED} from './model-common';
using {MST} from './model-mst';

entity PCH_T01_PO_H : IF_CUID_FILED { //采购订单抬头
  @title: '{i18n>PO_NO}' key PO_NO                               : String(10) not null; //采购订单编号
  @title: '{i18n>PO_DATE}' PO_DATE                               : Date; //発注日
  @title: '{i18n>SUPPLIER}' SUPPLIER                              : String(10); //供应商
   @title: '{i18n>approvedate}' approvedate                              : Date; //承認日
 @title: '{i18n>PO_BUKRS}' PO_BUKRS                              : String(4); //供应商
  @title: '{i18n>PO_ORG}' PO_ORG                                 : String(4); //供应商
  @title: '{i18n>PO_GROUP}' PO_GROUP                             : String(3); //供应商
  @title: '{i18n>PO_BSTYP}' PO_BSTYP                             : String(1); //PO Status
  @title: '{i18n>REMARK}' REMARK                                 : String(1000); //Remark(Header)

  TO_ITEMS                                                       : Association to many PCH_T02_PO_D //采购订单行
                                                                     ON  TO_ITEMS.PO_NO = PO_NO;

}

entity PCH_T02_PO_D : IF_CUID_FILED { //采购订单行
  @title: '{i18n>PO_NO}' key PO_NO                               : String(10) not null; //采购订单编号
  @title: '{i18n>D_NO}'  key D_NO                                : Integer;             //采购订单明细行号
  @title: '{i18n>PLANT_ID}' PLANT_ID                             : String(4);           //工厂
  @title: '{i18n>PO_TYPE}'  PO_TYPE                              : String(1);           //发注区分
  @title: '{i18n>MAT_ID}'   MAT_ID                               : String(40);          //物料编号
  @title: '{i18n>PO_D_TXZ01}' PO_D_TXZ01                         : String(80);          //物料名称
  @title: '{i18n>PO_PUR_QTY}' PO_PUR_QTY                         : Decimal(18,3);       //采购发注单位数量
  @title: '{i18n>PO_PUR_UNIT}' PO_PUR_UNIT                       : String(3);           //发注单位
  @title: '{i18n>CURRENCY}' CURRENCY                             : String(3);           //货币
  @title: '{i18n>UNIT_PRICE}' UNIT_PRICE                         : Decimal(18,3);       //价格单位
  @title: '{i18n>DEL_PRICE}' DEL_PRICE                           : Decimal(18,3);       //发注单价
  @title: '{i18n>DEL_AMOUNT}' DEL_AMOUNT                         : Decimal(18,3);       //发注金额
  @title: '{i18n>STORAGE_LOC}' STORAGE_LOC                       : String(4);           //库存地点
  @title: '{i18n>STORAGE_TXT}' STORAGE_TXT                       : String(40);          //保管场所文本
  @title: '{i18n>SUPPLIER_MAT}' SUPPLIER_MAT                     : String(40);          //供应商品番
  @title: '{i18n>CUSTOMER_MAT}' CUSTOMER_MAT                     : String(40);          //顾客品番
  @title: '{i18n>STATUS}' STATUS                                 : String(1);           //状态
  @title: '{i18n>PO_D_DATE}'  PO_D_DATE                          : Date;          //指定纳期
  @title: '{i18n>PO_D_BUKRS}' PO_D_BUKRS                         : String(4);           //公司代码
  @title: '{i18n>PO_D_RETPO}' PO_D_RETPO                         : String(1);           //退货标识
  @title: '{i18n>PO_D_ELIKZ}' PO_D_ELIKZ                         : String(1);           //交货已完成标识
  @title: '{i18n>PO_D_KNTTP}' PO_D_KNTTP                         : String(1);           //科目分配カテゴリ
  @title: '{i18n>PO_D_PSTYP}' PO_D_PSTYP                         : String(4);           //項目カテゴリ
  @title: '{i18n>MEMO}' MEMO                                     : String(1000);        //Remark(Header)
  TO_HEAD                                                        : Association to one PCH_T01_PO_H //采购订单抬头表
                                                                     ON  TO_HEAD.PO_NO = PO_NO;
  TO_MAT                                                         : Association to one MST.MST_T01_SAP_MAT //品目
                                                                     ON  TO_MAT.MAT_ID = MAT_ID;
}

entity PCH_T03_PO_C : IF_CUID_FILED { //采购订单确认表
  @title: '{i18n>PO_NO}' key PO_NO                               : String(10) not null; //采购订单编号
  @title: '{i18n>D_NO}'  key D_NO                                : Integer;             //采购订单明细行号
  @title: '{i18n>SEQ}'  key SEQ                                 : Integer;             //序号
  @title: '{i18n>DELIVERY_DATE}'      DELIVERY_DATE                       : Date;          //交货日期
  @title: '{i18n>QUANTITY}'      QUANTITY                            : Decimal(18,3);       //交货数量
  @title: '{i18n>STATUS}'      STATUS                              : String(1);           //状态
 @title: '{i18n>ExtNumber}'      ExtNumber                            : String(35);           //参照
 @title: '{i18n>RelevantQuantity}'      RelevantQuantity                     : Decimal(18,3);           //減少数量
}

entity PCH_T04_PAYMENT_H : IF_CUID_FILED { //付款申请表抬头表
  @title: '{i18n>INV_NO}'              key INV_NO                 : String(10) not null; //采购订单编号
  @title: '{i18n>GL_YEAR}'             key GL_YEAR                : Integer;             //采购订单明细行号
  @title: '{i18n>SUPPLIER}'                SUPPLIER               : String(10);          //采购订单明细行号
  @title: '{i18n>SUPPLIER_DESCRIPTION}'    SUPPLIER_DESCRIPTION   : String(100);         //采购订单明细行号
  @title: '{i18n>INV_DATE}'                INV_DATE               : Date;          //采购订单明细行号
  @title: '{i18n>INV_BASE_DATE}'           INV_BASE_DATE          : Date;          //采购订单明细行号
  @title: '{i18n>INV_POST_DATE}'           INV_POST_DATE          : Date;          //采购订单明细行号
  @title: '{i18n>SEND_FLAG}'               SEND_FLAG              : String(1);           //采购订单明细行号
  @title: '{i18n>EXCHANGE}'               EXCHANGE              : Decimal(18,3);           //换算レ-ト
  TO_ITEMS                                                        : Association to many PCH_T05_PAYMENT_D //付款申请表行表
                                                                    ON  TO_ITEMS.INV_NO = INV_NO AND TO_ITEMS.GL_YEAR = GL_YEAR;
}

entity PCH_T05_PAYMENT_D : IF_CUID_FILED { //付款申请表行表
  @title: '{i18n>INV_NO}'              key INV_NO                            : String(10) not null; //发票号
  @title: '{i18n>GL_YEAR}'             key GL_YEAR                           : Integer;             //发票年份
  @title: '{i18n>ITEM_NO}'             key ITEM_NO                           : Integer;             //发票明细
  @title: '{i18n>PO_NO}'                   PO_NO                             : String(10);          //采购订单号
 @title: '{i18n>PLANT_ID}'                 PLANT_ID                             : String(4);          //工厂
 @title: '{i18n>Company_Code}'             Company_Code                             : String(4);          //公司代码
  @title: '{i18n>GR_DATE}'                 GR_DATE                             : Date;          //入荷日
  @title: '{i18n>D_NO}'                    D_NO                              : Integer;             //采购订单明细行号
  @title: '{i18n>PO_TRACK_NO}'             PO_TRACK_NO                       : String(10);          //采购订单跟踪号
  @title: '{i18n>PR_BY}'                   PR_BY                             : String(20);          //采购订单明细行号
  @title: '{i18n>GL_ACCOUNT}'              GL_ACCOUNT                        : String(10);          //采购订单明细行号
  @title: '{i18n>COST_CENTER}'             COST_CENTER                       : String(10);          //采购订单明细行号
  @title: '{i18n>PURCHASE_GROUP}'          PURCHASE_GROUP                    : String(3);          //采购订单明细行号
  @title: '{i18n>PURCHASE_GROUP_DESC}'     PURCHASE_GROUP_DESC               : String(20);          //采购订单明细行号
  @title: '{i18n>CURRENCY}'                CURRENCY                          : String(10);          //采购订单明细行号
  @title: '{i18n>MAT_ID}'                  MAT_ID                            : String(40);          //采购订单明细行号
  @title: '{i18n>MAT_DESC}'                MAT_DESC                          : String(80);          //采购订单明细行号
  @title: '{i18n>QUANTITY}'                QUANTITY                          : Decimal(18,3);         //采购订单明细行号
  @title: '{i18n>UNIT}'                    UNIT                              : String(3);          //采购订单明细行号
  @title: '{i18n>TAX_CODE}'                TAX_CODE                          : String(3);          //采购订单明细行号
  @title: '{i18n>TAX_RATE}'                TAX_RATE                          : Decimal(18,3);          //采购订单明细行号
  @title: '{i18n>UNIT_PRICE}'              UNIT_PRICE                        : Decimal(18,3);       //采购订单明细行号
  @title: '{i18n>PRICE_AMOUNT}'            PRICE_AMOUNT                      : Decimal(18,3);       //采购订单明细行号
  @title: '{i18n>TAX_AMOUNT}'              TAX_AMOUNT                        : Decimal(18,3);       //采购订单明细行号
  @title: '{i18n>TOTAL_AMOUNT}'            TOTAL_AMOUNT                      : Decimal(18,3);       //采购订单明细行号
  @title: '{i18n>SHKZG}'                    SHKZG                             :String(1);         //借方/貨方フ5グ
  
  TO_HEAD                                                                    : Association to one PCH_T04_PAYMENT_H //付款申请表头表
                                                                    ON  TO_HEAD.INV_NO = INV_NO AND TO_HEAD.GL_YEAR = GL_YEAR;
}