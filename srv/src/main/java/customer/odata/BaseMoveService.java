package customer.odata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Base64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cds.gen.sys.T11IfManager;
import customer.bean.com.UmcConstants;

/**
 * 移动实际 转记功能 共通类
 * 
 * @author OYSK
 * @date 2021年1月19日
 */
@Component
public class BaseMoveService {
	private static final Logger logger = LoggerFactory.getLogger(BaseMoveService.class);

	/**
	 * 转记 方法
	 * 
	 * @date 2021年1月19日
	 * @param ids
	 *               移动实际表 id
	 * @param iFCode
	 *               获取接口配置表 URl
	 * @return
	 * @throws Exception
	 */
	public String postMove(T11IfManager info, Object body, HashMap<String, String> headMap) throws Exception {

		logger.info("post paramater=============================");
		// logger.info(body);
		// 获取接口url
		String url = info.getUrl() + info.getServicepath();

		// 调用MMSS服务 转记过账接口

		/** start 调用SCP服务接口 */
		Map<String, Object> map = null;
		map = getMapParam(body);
		// // 获取token值
		String tokenUrl = info.getExpand();
		String userDetail = info.getSapuser();
		String token = getToken(tokenUrl, userDetail);

		String authorization = "Bearer " + token;
		Map<String, String> result = HttpClientUtil.sendPost(url, authorization, map); // 调用SCP服务接口
		/** end 调用SCP服务接口 */
		if (result.get(UmcConstants.ERROR) != null) { // 调用接口报错 抛出提示
			logger.error("调用SCP服务接口报错");
		}
		String obj = result.get(UmcConstants.SUCCESS);

		return obj;
	}

	// 获取token值
	public String getToken(String url, String userDetail) throws Exception {
		String authorization = "Basic " + Base64Util.encode(userDetail);
		Map<String, Object> retMap = new HashMap<String, Object>();

		Map<String, String> map = HttpClientUtil.sendPost(url, authorization,
				retMap);
		if (map.get(UmcConstants.ERROR) != null) {
			return map.get(UmcConstants.ERROR);
		}

		JSONObject json = JSON.parseObject(map.get(UmcConstants.SUCCESS));
		if (json.get("access_token") == null) {
			return map.get(UmcConstants.SUCCESS);
		}
		return json.get("access_token").toString();
	}

	private Map<String, Object> getMapParam(Object menu) throws Exception {
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("pch06", menu);
		return retMap;
	}
}
