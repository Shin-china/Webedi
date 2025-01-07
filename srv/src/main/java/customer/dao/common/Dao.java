package customer.dao.common;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sap.cds.services.environment.CdsProperties.Persistence;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.request.UserInfo;

import customer.comm.tool.DateTools;
import customer.comm.tool.StringTool;

import org.apache.commons.lang3.StringUtils;

@Repository
public class Dao {
    @Autowired
    protected PersistenceService db;

    @Autowired
    protected UserInfo user;

    // 得到时间时间戳
    public Instant getNow() {
        return DateTools.getInstantNow();
    }

    // 得到当前的用户ID。为空的时候 插入 "-"
    public String getUserId() {

        if (this.user == null || StringUtils.isBlank(this.user.getName())) {
            return "anonymous";
        } else {
            return this.user.getName();
        }

    }

    /**
     * 将传入数值加1
     * 
     * @param docIndex 菜单KEY
     * @return 数值
     */
    protected BigDecimal getBigDecimalAdd(BigDecimal docIndex) {
        return docIndex.add(BigDecimal.ONE);
    }
	/**
	 * 明细数据处理共用参数
	 * 
	 * @return
	 */
	public Map<String, Object> setUpdateDaMap(String delFlag, String upFlag) {
		Map<String, Object> paraMap = new HashMap<>();
		paraMap = getUpdateDataMap(paraMap);
		if (!StringTool.isNull(delFlag)) {
			paraMap.put("DEL_FLAG", delFlag);
		}
		if (!StringTool.isNull(upFlag)) {
			paraMap.put("UP_FLAG", Integer.toString(Integer.parseInt(upFlag) + 1));
		}
		return paraMap;

	}

	// 初始化屬性
	protected Map<String, Object> getUpdateDataMap(Map<String, Object> paraMap) {
		paraMap.put("UP_BY", this.getUserId());
		paraMap.put("UP_TIME", this.getNow());

		return paraMap;
	}
}
