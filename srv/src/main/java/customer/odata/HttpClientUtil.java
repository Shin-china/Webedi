package customer.odata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import customer.bean.com.UmcConstants;

@SuppressWarnings("all")
public class HttpClientUtil {

	/**
	 * http Post请求
	 * 
	 * @date 2021年1月27日
	 * @param url
	 *                      请求路径
	 * @param authorization
	 *                      认证参数
	 * @param map
	 *                      请求参数
	 * @return
	 */
	public static Map<String, String> sendPost(String url, String authorization, Map map)
			throws IOException {
		Map<String, String> retMap = new HashMap<String, String>();
		// 返回body
		String body = null;
		// 获取连接客户端工具
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = null;
		// 创建一个HttpPost请求
		HttpPost post = new HttpPost(url);
		// 设置header信息
		/** header中通用属性 */
		post.setHeader("Accept", "*/*");
		post.setHeader("Accept-Encoding", "gzip, deflate");
		post.setHeader("Cache-Control", "no-cache");
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Content-Type", "application/json;charset=UTF-8");
		/** 业务参数 */
		post.setHeader("Authorization", authorization);

		// 设置参数
		StringEntity paramEntity = new StringEntity(JSON.toJSONString(map), "UTF-8");
		paramEntity.setContentEncoding("UTF-8");
		paramEntity.setContentType("application/json");
		post.setEntity(paramEntity);

		// 执行post请求操作，并拿到结果
		httpResponse = httpClient.execute(post);

		// 获取结果实体
		HttpEntity entity = httpResponse.getEntity();
		// if (entity != null) {
		// 按指定编码转换结果实体为String类型
		body = EntityUtils.toString(entity, "UTF-8");
		// }
		int code = httpResponse.getStatusLine().getStatusCode();
		if (code != 200) {
			retMap.put(UmcConstants.ERROR, "失敗:" + body);
			return retMap;
		}

		httpResponse.close();
		httpClient.close();
		retMap.put(UmcConstants.SUCCESS, body);
		return retMap;
	}
}
