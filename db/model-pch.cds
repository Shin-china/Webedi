namespace PCH;

using {cuid} from '@sap/cds/common';
using {COMM.IF_CUID_FILED as IF_CUID_FILED} from './model-common';
using {MST} from './model-mst';

entity T01_PO_H : IF_CUID_FILED { //采购订单抬头
  @title: '{i18n>PO_NO}' key PO_NO                                    : String(10) not null; //采购订单编号
                             @title: '{i18n>PO_DATE}' PO_DATE         : Date; //発注日
                             @title: '{i18n>SUPPLIER}' SUPPLIER       : String(10); //供应商
                             @title: '{i18n>approvedate}' approvedate : Date; //承認日
                             @title: '{i18n>PO_BUKRS}' PO_BUKRS       : String(4); //供应商
                             @title: '{i18n>PO_ORG}' PO_ORG           : String(4); //供应商
                             @title: '{i18n>PO_GROUP}' PO_GROUP       : String(3); //供应商
                             @title: '{i18n>PO_BSTYP}' PO_BSTYP       : String(1); //PO Status
                             @title: '{i18n>REMARK}' REMARK           : String(1000); //Remark(Header)
                             @title: '{i18n>POCDBY}' POCDBY           : String(12); //自社参照

                             TO_ITEMS                                 : Association to many T02_PO_D //采购订单行
                                                                          on TO_ITEMS.PO_NO = PO_NO;

}

entity T02_PO_D : IF_CUID_FILED { //采购订单行
  @title: '{i18n>PO_NO}' key PO_NO                                      : String(10) not null; //采购订单编号
  @title: '{i18n>D_NO}' key  D_NO                                       : Integer; //采购订单明细行号
                             @title: '{i18n>PLANT_ID}' PLANT_ID         : String(4); //工厂
                             @title: '{i18n>PO_TYPE}' PO_TYPE           : String(1); //发注区分
                             @title: '{i18n>MAT_ID}' MAT_ID             : String(40); //物料编号
                             @title: '{i18n>PO_D_TXZ01}' PO_D_TXZ01     : String(80); //物料名称
                             @title: '{i18n>PO_PUR_QTY}' PO_PUR_QTY     : Decimal(18, 3); //采购发注单位数量
                             @title: '{i18n>PO_PUR_UNIT}' PO_PUR_UNIT   : String(3); //发注单位
                             @title: '{i18n>CURRENCY}' CURRENCY         : String(3); //货币
                             @title: '{i18n>UNIT_PRICE}' UNIT_PRICE     : Decimal(18, 5); //价格单位
                             @title: '{i18n>DEL_PRICE}' DEL_PRICE       : Decimal(18, 3); //发注单价
                             @title: '{i18n>DEL_AMOUNT}' DEL_AMOUNT     : Decimal(18, 3); //发注金额
                             @title: '{i18n>STORAGE_LOC}' STORAGE_LOC   : String(4); //库存地点
                             @title: '{i18n>STORAGE_TXT}' STORAGE_TXT   : String(40); //保管场所文本
                             @title: '{i18n>SUPPLIER_MAT}' SUPPLIER_MAT : String(40); //供应商品番
                             @title: '{i18n>CUSTOMER_MAT}' CUSTOMER_MAT : String(40); //顾客品番
                             @title: '{i18n>STATUS}' STATUS             : String(1); //状态
                             @title: '{i18n>PO_D_DATE}' PO_D_DATE       : Date; //指定纳期
                             @title: '{i18n>PO_D_BUKRS}' PO_D_BUKRS     : String(4); //公司代码
                             @title: '{i18n>PO_D_RETPO}' PO_D_RETPO     : String(1); //退货标识
                             @title: '{i18n>PO_D_ELIKZ}' PO_D_ELIKZ     : String(1); //交货已完成标识
                             @title: '{i18n>PO_D_KNTTP}' PO_D_KNTTP     : String(1); //科目分配カテゴリ
                             @title: '{i18n>PO_D_PSTYP}' PO_D_PSTYP     : String(4); //項目カテゴリ
                             @title: '{i18n>MEMO}' MEMO                 : String(1000); //Remark(Header)
                             @title: '{i18n>INT_NUMBER}' INT_NUMBER     : String(18); //海外番号
                             @title: '{i18n>PR_BY}' PR_BY               : String(50); //購買依頼者
                             TO_HEAD                                    : Association to one T01_PO_H //采购订单抬头表
                                                                            on TO_HEAD.PO_NO = PO_NO;
                             TO_MAT                                     : Association to one MST.T01_SAP_MAT //品目
                                                                            on TO_MAT.MAT_ID = MAT_ID;
}

entity T03_PO_C : IF_CUID_FILED { //采购订单确认表
  @title: '{i18n>PO_NO}' key PO_NO                                              : String(10) not null; //采购订单编号
  @title: '{i18n>D_NO}' key  D_NO                                               : Integer; //采购订单明细行号
  @title: '{i18n>SEQ}' key   SEQ                                                : Integer; //序号
                             @title: '{i18n>DELIVERY_DATE}' DELIVERY_DATE       : Date; //交货日期
                             @title: '{i18n>QUANTITY}' QUANTITY                 : Decimal(18, 3); //交货数量
                             @title: '{i18n>STATUS}' STATUS                     : String(1); //状态
                             @title: '{i18n>ExtNumber}' ExtNumber               : String(35); //参照
                             @title: '{i18n>RelevantQuantity}' RelevantQuantity : Decimal(18, 3); //減少数量
                             TO_PCH_T01                                         : Association to one T01_PO_H //采购订单抬头表
                                                                                    on TO_PCH_T01.PO_NO = PO_NO;
                             TO_PCH_T02                                         : Association to one T02_PO_D //采购订单抬头表
                                                                                    on  TO_PCH_T02.PO_NO = PO_NO
                                                                                    and TO_PCH_T02.D_NO  = D_NO

}

entity T04_PAYMENT_H : IF_CUID_FILED { //付款申请表抬头表
  @title: '{i18n>INV_NO}' key  INV_NO                                                     : String(10) not null; //采购订单编号
  @title: '{i18n>GL_YEAR}' key GL_YEAR                                                    : Integer; //采购订单明细行号
                               @title: '{i18n>SUPPLIER}' SUPPLIER                         : String(10); //采购订单明细行号
                               @title: '{i18n>SUPPLIER_DESCRIPTION}' SUPPLIER_DESCRIPTION : String(100); //采购订单明细行号
                               @title: '{i18n>INV_DATE}' INV_DATE                         : Date; //采购订单明细行号
                               @title: '{i18n>INV_BASE_DATE}' INV_BASE_DATE               : Date; //采购订单明细行号
                               @title: '{i18n>INV_POST_DATE}' INV_POST_DATE               : Date; //采购订单明细行号
                               @title: '{i18n>SEND_FLAG}' SEND_FLAG                       : String(1); //采购订单明细行号
                               @title: '{i18n>EXCHANGE}' EXCHANGE                         : Decimal(18, 3); //换算レ-ト
                               @title: '{i18n>CE_DOC}' CE_DOC                             : String(10); //差額伝票番号
                               TO_ITEMS                                                   : Association to many T05_PAYMENT_D //付款申请表行表
                                                                                              on  TO_ITEMS.INV_NO  = INV_NO
                                                                                              and TO_ITEMS.GL_YEAR = GL_YEAR;
}

entity T05_PAYMENT_D : IF_CUID_FILED { //付款申请表行表
  @title: '{i18n>INV_NO}' key  INV_NO                                                   : String(10) not null; //发票号
  @title: '{i18n>GL_YEAR}' key GL_YEAR                                                  : Integer; //发票年份
  @title: '{i18n>ITEM_NO}' key ITEM_NO                                                  : Integer; //发票明细
                               @title: '{i18n>PO_NO}' PO_NO                             : String(10); //采购订单号
                               @title: '{i18n>PLANT_ID}' PLANT_ID                       : String(4); //工厂
                               @title: '{i18n>Company_Code}' Company_Code               : String(4); //公司代码
                               @title: '{i18n>GR_DATE}' GR_DATE                         : Date; //入荷日
                               @title: '{i18n>D_NO}' D_NO                               : Integer; //采购订单明细行号
                               @title: '{i18n>PO_TRACK_NO}' PO_TRACK_NO                 : String(10); //采购订单跟踪号
                               @title: '{i18n>PR_BY}' PR_BY                             : String(20); //采购订单明细行号
                               @title: '{i18n>GL_ACCOUNT}' GL_ACCOUNT                   : String(10); //采购订单明细行号
                               @title: '{i18n>COST_CENTER}' COST_CENTER                 : String(10); //采购订单明细行号
                               @title: '{i18n>PURCHASE_GROUP}' PURCHASE_GROUP           : String(3); //采购订单明细行号
                               @title: '{i18n>PURCHASE_GROUP_DESC}' PURCHASE_GROUP_DESC : String(20); //采购订单明细行号
                               @title: '{i18n>CURRENCY}' CURRENCY                       : String(10); //采购订单明细行号
                               @title: '{i18n>MAT_ID}' MAT_ID                           : String(40); //采购订单明细行号
                               @title: '{i18n>MAT_DESC}' MAT_DESC                       : String(80); //采购订单明细行号
                               @title: '{i18n>QUANTITY}' QUANTITY                       : Decimal(18, 3); //采购订单明细行号
                               @title: '{i18n>UNIT}' UNIT                               : String(3); //采购订单明细行号
                               @title: '{i18n>TAX_CODE}' TAX_CODE                       : String(3); //采购订单明细行号
                               @title: '{i18n>TAX_RATE}' TAX_RATE                       : Decimal(18, 3); //采购订单明细行号
                               @title: '{i18n>UNIT_PRICE}' UNIT_PRICE                   : Decimal(18, 5); //采购订单明细行号
                               @title: '{i18n>PRICE_AMOUNT}' PRICE_AMOUNT               : Decimal(18, 3); //采购订单明细行号
                               @title: '{i18n>TAX_AMOUNT}' TAX_AMOUNT                   : Decimal(18, 3); //采购订单明细行号
                               @title: '{i18n>TOTAL_AMOUNT}' TOTAL_AMOUNT               : Decimal(18, 3); //采购订单明细行号
                               @title: '{i18n>SHKZG}' SHKZG                             : String(1); //借方/貨方フ5グ

                               TO_HEAD                                                  : Association to one T04_PAYMENT_H //付款申请表头表
                                                                                            on  TO_HEAD.INV_NO  = INV_NO
                                                                                            and TO_HEAD.GL_YEAR = GL_YEAR;
}


entity T06_QUOTATION_H : cuid, IF_CUID_FILED { //
  @title: '{i18n>SALES_NUMBER}' SALES_NUMBER     : String(50); //販売見積番号
  @title: '{i18n>VALIDATE_START}' VALIDATE_START : Date; //見積有効開始日
  @title: '{i18n>VALIDATE_END}' VALIDATE_END     : Date; //見積有効終了日

  @title: '{i18n>CUSTOMER}' CUSTOMER             : String(50); //客先
  @title: '{i18n>QUO_NUMBER}' QUO_NUMBER         : String(50); //購買見積番号
  @title: '{i18n>QUO_VERSION}' QUO_VERSION       : String(5); //販売見積バージョン
  @title: '{i18n>STATUS}' STATUS                 : String(50); //ステータス
  @title: '{i18n>MACHINE_TYPE}' MACHINE_TYPE     : String(50); //機種
  @title: '{i18n>Item}' Item                     : String(50); //アイテム
  @title: '{i18n>QUANTITY}' QUANTITY             : Decimal(18, 3); //数量
  @title: '{i18n>TIME}' TIME                     : Date; //時期
  @title: '{i18n>LOCATION}' LOCATION             : String(50); //量産場所
  @title: '{i18n>PLANT_ID}' PLANT_ID             : String(4); //工厂
  //  @title: '{i18n>TOTAL_JPY}' TOTAL_JPY           : Decimal(18, 3); //合計金額（日本円）
  // @title: '{i18n>TOTAL_USD}' TOTAL_USD           : Decimal(18, 3); //合計金額（米ドル）
  // @title: '{i18n>TOTAL_CNY}' TOTAL_CNY           : Decimal(18, 3); //合計金額（中国元）
  // @title: '{i18n>TOTAL_HKD}' TOTAL_HKD           : Decimal(18, 3); //合計金額（香港ドル）
  // @title: '{i18n>TOTAL_THB}' TOTAL_THB           : Decimal(18, 3); //合計金額（タイバーツ）

  @title: '{i18n>CD_DATE}' CD_DATE               : Date; //创建日
  @title: '{i18n>CD_DATE_TIME}' CD_DATE_TIME     : String(10); //创建日时

  TO_ITEMS                                       : Composition of many T07_QUOTATION_D
                                                     on  TO_ITEMS.SALES_NUMBER = SALES_NUMBER
                                                     and TO_ITEMS.QUO_NUMBER   = QUO_NUMBER
                                                     and TO_ITEMS.QUO_VERSION  = QUO_VERSION;

}

entity T07_QUOTATION_D : cuid, IF_CUID_FILED { //
  @title: '{i18n>QUO_NUMBER}' QUO_NUMBER                 : String(50); //購買見積番号
  @title: '{i18n>QUO_ITEM}' QUO_ITEM                     : Integer; //管理No

  @title: '{i18n>QUO_ITEM}' SALES_NUMBER                 : String(20); //販売見積番号
  @title: '{i18n>QUO_ITEM}' QUO_VERSION                  : String(5); //販売見積バージョン
  @title: '{i18n>QUO_ITEM}' SALES_D_NO                   : String(5); //販売見積案件明細
  @title: '{i18n>QUO_ITEM}' SAP_MAT_ID                   : String(40); //SAP 品目（製品）
  @title: '{i18n>QUO_ITEM}' DEVELOP_MAT                  : String(40); //開発品番
  @title: '{i18n>PLANT_ID}' PLANT_ID                     : String(4); //プラント


  @title: '{i18n>NO}' NO                                 : Integer; //No.
  @title: '{i18n>REFRENCE_NO}' REFRENCE_NO               : String(50); //併記有無リファレンスNo
  @title: '{i18n>MATERIAL_NUMBER}' MATERIAL_NUMBER       : String(40); //SAP品番（任意）
  @title: '{i18n>CUST_MATERIAL}' CUST_MATERIAL           : String(40); //顧客品番
  @title: '{i18n>MANUFACT_MATERIAL}' MANUFACT_MATERIAL   : String(40); //メーカー品番
  @title: '{i18n>Attachment}' Attachment                 : String(50); //カスタム品図面 仕様添付
  @title: '{i18n>Material}' Material                     : String(40); //品名
  @title: '{i18n>MAKER}' MAKER                           : String(15); //メーカ
  @title: '{i18n>UWEB_USER}' UWEB_USER                   : String(50); //仕入先連絡先（WEB EDIの担当）（必須）
  @title: '{i18n>PERSON_NO1}' BP_NUMBER                  : Integer; //SAP BP（任意）
  @title: '{i18n>INITIAL_OBJ}' INITIAL_OBJ               : String(1); //イ二シ儿費用対象
  @title: '{i18n>QTY}' QTY                               : Decimal(18, 3); //数量
  @title: '{i18n>SMI_CM_MM}' SMI_CM_MM                   : String(150); //SAP品番（任意）+顧客品番+メーカー品番

  @title: '{i18n>PERSON_NO1}' PERSON_NO1                 : Integer; //員数1
  @title: '{i18n>PERSON_NO2}' PERSON_NO2                 : Integer; //員数2
  @title: '{i18n>PERSON_NO3}' PERSON_NO3                 : Integer; //員数3
  @title: '{i18n>PERSON_NO4}' PERSON_NO4                 : Integer; //員数4
  @title: '{i18n>PERSON_NO5}' PERSON_NO5                 : Integer; //員数5
  @title: '{i18n>YLP}' YLP                               : String(50); //依頼品判定
  @title: '{i18n>MANUL}' MANUL                           : String(50); //正式メーカ品番
  @title: '{i18n>MANUFACT_CODE}' MANUFACT_CODE           : String(50); //Manfact. Code name
  @title: '{i18n>CUSTOMER_MMODEL}' CUSTOMER_MMODEL       : String(50); //客先型番
  @title: '{i18n>MID_QF}' MID_QF                         : String(50); //中区分
  @title: '{i18n>SMALL_QF}' SMALL_QF                     : String(50); //小区分
  @title: '{i18n>OTHER_QF}' OTHER_QF                     : String(50); //その他区分

  @title: '{i18n>CURRENCY}' CURRENCY                     : String(3); //通貨
  @title: '{i18n>PRICE}' PRICE                           : Decimal(18, 3); //単価
  @title: '{i18n>PRICE_CONTROL}' PRICE_CONTROL           : String(1); //Date of pricing control(価格有効日：発注時or納入時)
  @title: '{i18n>LEAD_TIME}' LEAD_TIME                   : Date; //LT（日数）
  @title: '{i18n>MOQ}' MOQ                               : String(50); //MOQ
  @title: '{i18n>UNIT}' UNIT                             : String(50); //Base Unit of Measure(単位：pc or kgなど)
  @title: '{i18n>SPQ}' SPQ                               : String(50); //SPQ(Rounding：MOQの次の発注単位)
  @title: '{i18n>KBXT}' KBXT                             : String(50); //梱包形態
  @title: '{i18n>PRODUCT_WEIGHT}' PRODUCT_WEIGHT         : String(10); //製品重量（g）
  @title: '{i18n>ORIGINAL_COU}' ORIGINAL_COU             : String(2); //原産国
  @title: '{i18n>EOL}' EOL                               : String(50); //EOL予定
  @title: '{i18n>ISBOI}' ISBOI                           : Boolean; //投資促進制度（BOI or Non BOI）
  @title: '{i18n>Incoterms}' Incoterms                   : String(10); //Incoterms 1（インコタームズ）
  @title: '{i18n>Incoterms_Text}' Incoterms_Text         : String(40); //Incoterms 1（納入場所）
  @title: '{i18n>MEMO1}' MEMO1                           : String(200); //備考１
  @title: '{i18n>MEMO2}' MEMO2                           : String(200); //備考２
  @title: '{i18n>MEMO3}' MEMO3                           : String(200); //備考３
  @title: '{i18n>SL}' SL                                 : String(50); //商流
  @title: '{i18n>TZ}' TZ                                 : String(50); //同値
  @title: '{i18n>RMATERIAL}' RMATERIAL                   : String(40); //代替品番
  @title: '{i18n>RMATERIAL_CURRENCY}' RMATERIAL_CURRENCY : String(3); //代替品番の通貨
  @title: '{i18n>RMATERIAL_PRICE}' RMATERIAL_PRICE       : Decimal(18, 3); //代替品番の単価
  @title: '{i18n>RMATERIAL_LT}' RMATERIAL_LT             : String(50); //代替品番のLT（日数）
  @title: '{i18n>RMATERIAL_MOQ}' RMATERIAL_MOQ           : String(50); //代替品番のMOQ
  @title: '{i18n>RMATERIAL_KBXT}' RMATERIAL_KBXT         : String(50); //代替品番の梱包形態
  @title: '{i18n>UMC_SELECTION}' UMC_SELECTION           : String(50); //UMC購買選択
  @title: '{i18n>UMC_COMMENT_1}' UMC_COMMENT_1           : String(200); //UMC購買コメント１
  @title: '{i18n>UMC_COMMENT_2}' UMC_COMMENT_2           : String(200); //UMC購買コメント２
  @title: '{i18n>FINAL_CHOICE}' FINAL_CHOICE             : String(50); //最終決定
  @title: '{i18n>STATUS}' STATUS                         : String(50); //ステータス
  @title: '{i18n>CD_DATE}' CD_DATE                       : Date; //创建日
  @title: '{i18n>CD_DATE_TIME}' CD_DATE_TIME             : String(10); //创建日时

  @title: '{i18n>TO_HEAD}'
  TO_HEAD                                                : Association to one T06_QUOTATION_H
                                                             on  TO_HEAD.SALES_NUMBER = SALES_NUMBER
                                                             and TO_HEAD.QUO_NUMBER   = QUO_NUMBER
                                                             and TO_HEAD.QUO_VERSION  = QUO_VERSION;


}

entity T08_UPLOAD : cuid, IF_CUID_FILED { //
  @title: '{i18n>PO_NO}' PO_NO               : String(10) not null; //采购订单编号
  @title: '{i18n>D_NO}' D_NO                 : Integer; //采购订单明细行号
  @title: '{i18n>PO_NO_DNO}' PO_NO_DNO       : String(50); //購買伝票＋明細
  @title: '{i18n>TYPE}' TYPE                 : String(1); //種類
  @title: '{i18n>MAT_ID}' MAT_ID             : String(40); //品目
  @title: '{i18n>MAT_NAME}' MAT_NAME         : String(40); //物料名称
  @title: '{i18n>QUANTITY}' QUANTITY         : Decimal(18, 3); //発注数
  @title: '{i18n>PLANT_ID}' PLANT_ID         : String(4); //工厂
  @title: '{i18n>STORAGE_LOC}' LOCATION_ID   : String(4); //库存地点
  @title: '{i18n>INPUT_DATE}' INPUT_DATE     : Date; //納入日付
  @title: '{i18n>INPUT_QTY}' INPUT_QTY       : Decimal(18, 3); //納入数
  @title: '{i18n>ExtNumber}' ExtNumber       : String(35); //参照
  @title: '{i18n>CD_DATE}' CD_DATE           : Date; //创建日
  @title: '{i18n>CD_DATE_TIME}' CD_DATE_TIME : String(10); //创建日时


}
entity T09_FORCAST : IF_CUID_FILED { //
  @title: '{i18n>PR_NUMBER}' key PR_NUMBER                                              : String(10) not null; //購買依頼番号
  @title: '{i18n>D_NO}' key      D_NO                                                   : Integer; //購買依頼明細番号
                                 @title: '{i18n>PUR_GROUP}' PUR_GROUP                   : String(3); //購買 Group
                                 @title: '{i18n>PUR_GROUP_NAME}' PUR_GROUP_NAME         : String(20); //購買 Group名
                                 @title: '{i18n>SUPPLIER}' SUPPLIER                     : String(10); //仕入先
                                 @title: '{i18n>NAME1}' NAME1                           : String(20); //名称 1
                                 @title: '{i18n>MATERIAL}' MATERIAL                     : String(40); //品目
                                 @title: '{i18n>MATERIAL_TEXT}' MATERIAL_TEXT           : String(80); //品目テキスト
                                 @title: '{i18n>SUPPLIER_MATERIAL}' SUPPLIER_MATERIAL   : String(40); //仕入先品目コード
                                 @title: '{i18n>DELIVARY_DAYS}' DELIVARY_DAYS           : Integer; //納入予定日数
                                 @title: '{i18n>MIN_DELIVERY_QTY}' MIN_DELIVERY_QTY     : Decimal(18, 3); //最低発注数量
                                 @title: '{i18n>MANUF_CODE}' MANUF_CODE                 : String(20); //製造者製品コード
                                 @title: '{i18n>ARRANGE_START_DATE}' ARRANGE_START_DATE : Date; //手配開始日
                                 @title: '{i18n>ARRANGE_END_DATE}' ARRANGE_END_DATE     : Date; //手配終了日
                                 @title: '{i18n>ARRANGE_QTY}' ARRANGE_QTY               : Decimal(18, 3); //手配数量合計
                                 @title: '{i18n>PLANT}' PLANT                           : String(4); //プラント
                                 @title: '{i18n>SUPPLIER_TEL}' SUPPLIER_TEL             : String(16); //仕入先電話番号

                                 @title: '{i18n>STATUS}' STATUS                         : String(1); //ステータス

}
//发送履历表
entity T10_EMAIL_SEND_LOG : IF_CUID_FILED { //
  @title: '{i18n>PO_NO}' key PO_NO                                  : String(10) not null; //采购订单编号
  @title: '{i18n>D_NO}' key  D_NO                                   : Integer; //采购订单明细行号
                             @title: '{i18n>QUANTITY}' QUANTITY     : Decimal(18, 3); //発注数
                             @title: '{i18n>INPUT_DATE}' INPUT_DATE : Date; //納入日付
                             @title: '{i18n>DEL_PRICE}' DEL_PRICE   : Decimal(18, 3); //发注单价
                             @title: '{i18n>PO_TYPE}' PO_TYPE       : String(1); //发注区分
                             @title: '{i18n>TYPE}' TYPE             : String(1); //是否送信为Y则发送

                             @title: '{i18n>CD_DATE}' CD_DATE       : Date; //创建日

}
