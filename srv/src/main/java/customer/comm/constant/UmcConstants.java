/**
 * 
 */
package customer.comm.constant;

/**
 * @author h-xiong
 *
 */
public class UmcConstants {

	public static String SYS_NAME = "UWEB"; // 系统名称
	public static String SYS_NAME_SAP = "SAP"; // SAP系统名称
	public static String SYSTEM_CLASS_PATH; // 系统资源路径（类路径） C:/Sap_Workspace/usapbe/srv/target/classes/

	public static String BTP_FILE_SEPR = "/"; // 系统名称

	public final static String YES = "Y"; // 是
	public final static String NO = "N"; // 否

	public static Integer MAX_STRING_SIZE = 4000; // 通用字符串最大长度 存储DB的。 超过的 用对象存储。

	public final static String DELETE_YES = "Y"; // 已经删除
	public final static String DELETE_NO = "N"; // 未删除

	public final static String OK = "OK";
	public final static String NG = "NG";

	public final static String MAT_CROSS_PLANT_STATUS_DEL = "05"; // 品目 工厂级别删除标记

	public final static String POST_STATUS_W = "W"; // 転記待
	public final static String POST_STATUS_P = "P"; // 転記済
	public final static String POST_STATUS_H = "H"; // 一部転記済
	public final static String POST_STATUS_C = "C"; // 转寄取消

	public final static int INV_DECIMAL_DIGIT = 3; // 在库小数位
	public static final int HANA_STRING_MAX_LENGTH = 4000;// string 4000 字符。
	public final static int TABLE_DARFTS_DATA_VALID_DATE = -30;// MIN 草稿数据有效期30分钟

	public static final String anonymous = "anonymous"; // 匿名用户
	public static final String USER_PWD_DEFFAULT = "123456"; // 默认用户密码

	public static final String BP_CUSTOMER = "CUST"; // BP类型
	public static final String BP_SUPPLIER = "SUPP"; // BP类型

	public static final String H_CODE_UPN_STATUS = "UPN_STATUS"; // upn状态凡用
	public static final String H_CODE_UPN_NUM = "UPN_NUM_01"; // upn发行 开头凡用
	public static final String H_CODE_DOCUMENT_NUM = "DOCUMENT_NUM_01"; // 传票发行 开头凡用
	public static final String H_CODE_PO_TYPE = "PO_TYPE"; // PO类型
	public static final String S4_UNIT_TEC_2_USER = "S4_UNIT_TEC_2_USER"; // S4单位转换（技术单位→用户单位）
	public static final String H_CODE_MAT_TYPE_FIN_PRD = "MAT_TYPE_FIN_PRD"; // 製品出荷品目タイプ

	public static final String PHY_FACTORY = "PHY_FACTORY"; // 物理工厂字典

	public static final String UPN_STATUS_00 = "00"; // upn发行初始状态
	public static final String UPN_STATUS_GR_10 = "10"; // 棚入状态
	public static final String UPN_STATUS_GR_12 = "12"; // 棚入戻入
	public static final String UPN_STATUS_GR_16 = "16"; // 特採棚入

	public static final String UPN_STATUS_GI_20 = "20"; // 棚出
	public static final String UPN_STATUS_GI_26 = "26"; // 特採棚出
	public static final String UPN_STATUS_GI_27 = "27"; // DN棚出
	public static final String UPN_STATUS_UMES = "-"; // UMES upn状态
	public static final String UPN_STATUS_NG = "99"; // USAP在庫管理対象外

	public final static String UPN_POST_STATUS_NON = "-"; // -
	public final static String UPN_POST_STATUS_W = "W"; // 転記待
	public final static String UPN_POST_STATUS_P = "P"; // 転記済

	public static final Integer UPN_MAX_NUMBER = 100; // upn单次发行最大张数

	public static final String[] PO_GR_TYPE = { "101", "501" }; // 受入UPN , 発注レスUPN

	public static final String INV_T09_MOVE_TYPE = "311";// 保管场所内移动；

	public static final String[] RES_TYPE = { "", "RES", "MR", "541" }; // 出库类型

	public static final String[] INS_STATUS = { "", "1", "2", "3", "4" }; // 特采申请书 状态

	public static final String[] INS_CONFIRM = { "", "N", "Y" }; // 特采申请书 承认状态

	public static final String DOC_D_STATUS_1 = "1"; // 文书状态 未承認
	public static final String DOC_D_STATUS_2 = "2"; // 文书状态 未公開
	public static final String DOC_D_STATUS_3 = "3"; // 文书状态 公開済

	public static final String MENU_PDA = "PDA"; // 菜单画面中 代表HT的画面

	public static final String IF_STATUS_D = "D"; // 接口处理中
	public static final String IF_STATUS_S = "S"; // 接口处理成功
	public static final String IF_STATUS_E = "E"; // 接口处理失败
	public static final String IF_STATUS_P_S = "P_S"; // 接口处理部分成功

	public static final String MSG_TYPE_E = "E"; //
	public static final String MSG_TYPE_S = "S"; //

	public final static String NULL_STRING = "-"; // 当为null的时候 db存储的值
	public final static String STK_UNRESTRICTED = "01"; // 非限制库存

	public final static String IPC_TASK_STATUS_D = "D"; // 棚卸ステータス 棚卸進行中
	public final static String IPC_TASK_STATUS_F = "F"; // 棚卸ステータス 棚卸完了

	public final static String OPE_TYPE_00 = "00"; // 发行;
	public final static String OPE_TYPE_10 = "10"; // 棚入;
	public final static String OPE_TYPE_16 = "16"; // 特採棚入;
	public final static String OPE_TYPE_12 = "12"; // 戻入;
	public final static String OPE_TYPE_RES = "RES"; // 棚出;
	public final static String OPE_TYPE_MR = "MR"; // 棚出;
	public final static String OPE_TYPE_541 = "541"; // 標準外注棚出;
	public final static String OPE_TYPE_RPO = "RPO"; // 仕入先返品出庫棚出;
	public final static String OPE_TYPE_26 = "26"; // 特採棚出;
	public final static String OPE_TYPE_DN = "DN"; // DN棚出;
	public final static String OPE_TYPE_SL = "SL"; // 分割;
	public final static String OPE_TYPE_MOVE_IN = "M_IN"; // 移动入
	public final static String OPE_TYPE_MOVE_OUT = "M_OUT";// 移动出
	public final static String OPE_TYPE_IPC = "IPC";// 盘点处理
	public final static String OPE_TYPE_P_101 = "P_101"; // 入庫(転記);
	public final static String OPE_TYPE_P_RES = "P_RES"; // 出庫予定(転記);
	public final static String OPE_TYPE_P_MR = "P_MR"; // MR出庫(転記);
	public final static String OPE_TYPE_P_541 = "P_541"; // 標準外注(転記);

	public final static String OPE_TYPE_P_RPO = "P_RPO"; // 仕入先返品(転記);

	public final static String OPE_TYPE_P_542 = "P_542"; // UPN外注先戻入(転記)
	public final static String OPE_TYPE_P_311 = "P_311"; // 保管场所间移动(転記);
	public final static String OPE_TYPE_P_309 = "P_309"; // 特采(転記);
	public final static String OPE_TYPE_P_RET = "P_RET"; // UPN製造現場戻入(転記)
	public final static String OPE_TYPE_P_DN = "P_DN"; // 標準外注(転記);
	public final static String OPE_TYPE_UMES_INSERT = "UMES1"; // umes发行;
	public final static String OPE_TYPE_UMES_CHANGE = "UMES2"; // umes变更;

	public final static String DOC_OPE_CODE_10 = "10";// 10承认
	public final static String DOC_OPE_CODE_11 = "11";// 11承认取消
	public final static String DOC_OPE_CODE_20 = "20";// 20公开
	public final static String DOC_OPE_CODE_21 = "21";// 21公开取消
	public final static String ADMIT_Y = "1";// 承认按钮
	public final static String ADMIT_N = "2";// 承认取消按钮
	public final static String RELEASE_Y = "3";// 公开按钮
	public final static String RELEASE_N = "4";// 公开取消按钮
	public final static String SORT_MAT = "0";
	public final static String SORT_SHELF = "1";

}
