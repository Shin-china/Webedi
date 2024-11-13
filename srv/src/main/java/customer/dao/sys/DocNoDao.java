/**
 * 
 */
package customer.dao.sys;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.request.UserInfo;

import cds.gen.sys.Sys_;
import cds.gen.sys.T06DocNo;
import customer.dao.common.Dao;
import customer.tool.NumberTool;

/**
 * @author 13692
 *
 */
@Component
public class DocNoDao extends Dao {

	private final static String DOC_NO_PROJECT = "000"; // 見積案件No.
	private final static String DOC_NO_PROJECT2 = "00000"; // 見積案件No.

	/**
	 * 得到見積案件No.
	 * 
	 * @param SALES_ORGANIZATION 販売組織
	 * @param count              条数默认1条
	 * 
	 * @return 番号
	 */
	public String getPJNo(String SALES_ORGANIZATION, int count)
			throws Exception {

		String code = "PROJECT_REQ";
		// 是否一次取多条
		// 販売組織＝1100の場合、UQJ；それ以外はREQ
		if ("1100".equals(SALES_ORGANIZATION)) {
			code = "PROJECT_UQJ";
		}

		T06DocNo index = this.getIndex(code, count);
		// 取得发番编辑
		return 1 + NumberTool.format(index.getDocIndex().toString(), DOC_NO_PROJECT2);
	}

	/**
	 * 得到見積番号No.
	 * 
	 * @param SALES_ORGANIZATION 販売組織
	 * @param count              条数默认1条
	 * 
	 * @return 番号
	 */
	public String getPJNo(int count)
			throws Exception {

		String code = "PROJECT_NO";
		// 是否一次取多条
		// 販売組織＝1100の場合、UQJ；それ以外はREQ

		T06DocNo index = this.getIndexNotNy(code, count);
		// 取得发番编辑
		return getDocIndexNo(index, DOC_NO_PROJECT);
	}

	/**
	 * 获取编辑后的发番
	 * 
	 * @param index  单号取号表数据
	 * @param format 番号FORMAT
	 * @return 番号
	 */
	private String getDocIndexNo(T06DocNo index, String format) {
		String str = NumberTool.format(index.getDocIndex().toString(), format);
		return index.getDocPrefex() + index.getDocDate() + str;
	}

	/**
	 * 获取番号年月
	 * 
	 * @param menuCode 菜单KEY
	 * @param type     1-传票号码 2-现品票
	 * @param num      现品票多张发番
	 * @return 单号取号表数据
	 */
	private T06DocNo getIndex(String key, Integer num) throws Exception {
		// 获取单号取号表数据
		T06DocNo d003DocNo = getDataByKey(key);
		// 判断日期
		// 获取当前时间,强行转换为日本时区时间
		TimeZone timeZone = TimeZone.getTimeZone("GMT+09:00");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		sdf.setTimeZone(timeZone);
		String format = sdf.format(new Date());

		// 截取时间后4位 2407年月
		String subDate = format.replaceAll("-", "").substring(2);

		// 当前时间与上次采番的时间一致的场合
		if (subDate.equals(d003DocNo.getDocDate())) {
			if (num != 1) {
				// 加上多张现品票
				d003DocNo.setDocIndex(d003DocNo.getDocIndex().add(new BigDecimal(num - 1)));
			}
			// 更新表数据
			setD003DocNo(d003DocNo);
		} else {
			// 更新日期为当日
			d003DocNo.setDocDate(subDate);
			// 更新序号从零开始
			d003DocNo.setDocIndex(BigDecimal.ZERO);
			if (num != 1) {
				// 加上多张现品票
				d003DocNo.setDocIndex(d003DocNo.getDocIndex().add(new BigDecimal(num - 1)));
			}
			// 更新表数据
			setD003DocNo(d003DocNo);
		}

		return d003DocNo;
	}

	/**
	 * 获取番号无年月
	 * 
	 * @param menuCode 菜单KEY
	 * @param type     1-传票号码 2-现品票
	 * @param num      现品票多张发番
	 * @return 单号取号表数据
	 */
	private T06DocNo getIndexNotNy(String key, Integer num) throws Exception {
		// 获取单号取号表数据
		T06DocNo d003DocNo = getDataByKey(key);

		if (num != 1) {
			// 加上多张现品票
			d003DocNo.setDocIndex(d003DocNo.getDocIndex().add(new BigDecimal(num - 1)));
		}
		// 更新表数据
		setD003DocNo(d003DocNo);

		return d003DocNo;
	}

	private T06DocNo getDataByKey(String key) {
		// 检索DOC_KEY
		Optional<T06DocNo> selectResult = db.run(Select.from(Sys_.T06_DOC_NO).where(o -> o.DOC_KEY().eq(key)).lock())
				.first(T06DocNo.class);
		if (!selectResult.isPresent()) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "ERROR_GETDOCKEY");

		}
		return selectResult.get();
	}

	/**
	 * 更新进数据库
	 * 
	 * @param d003DocNo 菜单KEY
	 */
	private void setD003DocNo(T06DocNo d003DocNo) throws Exception {
		// 更新入库前+1
		d003DocNo.setDocIndex(getBigDecimalAdd(d003DocNo.getDocIndex()));
		// 更新序号
		Map<String, Object> data = new HashMap<>();
		data.put("DOC_INDEX", d003DocNo.getDocIndex());
		// 更新日期
		data.put("DOC_DATE", d003DocNo.getDocDate());
		Map<String, Object> keys = new HashMap<>();
		keys.put("DOC_KEY", d003DocNo.getDocKey());

		CqnUpdate update = Update.entity(Sys_.T06_DOC_NO, b -> b.matching(keys)).data(data);

		// 執行更改Sql
		db.run(update);

	}

}
