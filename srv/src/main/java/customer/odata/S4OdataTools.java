package customer.odata;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.Header;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestCreate;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

import org.apache.http.client.HttpClient;
import org.apache.logging.log4j.util.Base64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cds.gen.sys.T11IfManager;
import customer.tool.Eenvironment;
import customer.tool.StringTool;

public class S4OdataTools {

    private static final Logger logger = LoggerFactory.getLogger(S4OdataTools.class);

    public static String desName = "XXXXX";

    public static String get(T11IfManager info, Integer currentPage, HashMap<String, String> addParaMap,
            HashMap<String, String> headMap) throws UnsupportedOperationException, IOException {

        return S4OdataTools.get(info, currentPage, addParaMap, headMap, null);
    }

    public static String get(T11IfManager info, Integer currentPage, HashMap<String, String> addParaMap,
            HashMap<String, String> headMap, String body) throws UnsupportedOperationException, IOException {
        String a = null;
        InputStream httpResponseContent = null;
        ODataRequestResultGeneric resultGene = null;
        try {
            resultGene = S4OdataTools.getBase(info, currentPage, addParaMap, headMap, body);

            httpResponseContent = resultGene.getHttpResponse().getEntity().getContent();

            a = StringTool.InputStream2String(httpResponseContent);

        } catch (Exception e) {
            if (httpResponseContent != null) {
                httpResponseContent.close();
            }

            if (resultGene != null) {
                resultGene = null;
            }

            e.printStackTrace();
            throw e;

        }

        return a;
    }

    @SuppressWarnings("null")
    public static ODataRequestResultGeneric getBase(T11IfManager info, Integer currentPage,
            HashMap<String, String> addParaMap, HashMap<String, String> headMap, String body)
            throws UnsupportedOperationException, IOException {

        logger.info("if info=============================" + info.getServicepath());

        HttpClient client = null;

        if (Eenvironment.isWindows()) {
            logger.info("当前Windows环境" + desName);
            DefaultHttpDestination destination = DefaultHttpDestination.builder(info.getUrl())
                    .header(new Header("X-Destination-Name", desName)).build();
            client = HttpClientAccessor.getHttpClient(destination);
        } else {
            logger.info("当前Linux环境" + desName);
            Destination destination = DestinationAccessor.getDestination(desName);
            client = HttpClientAccessor.getHttpClient(destination);
        }

        // 这是get方法
        ODataRequestRead requestRead = new ODataRequestRead(info.getServicepath(), info.getEntityname(), null,
                ODataProtocol.V2);

        String userInfo = Base64Util.encode(info.getSapuser());// 用户名,密码
        requestRead.addHeader("Authorization", "Basic " + userInfo);
        requestRead.addHeader("x-csrf-token", "fetch");
        requestRead.addHeader("Accept-Language", info.getLangCode());
        requestRead.addHeader("Content-Type", "application/json");
        requestRead.addHeader("Accept", "application/json");

        if (headMap != null) {
            for (String key : headMap.keySet()) {
                requestRead.addHeader(key, headMap.get(key)); // 设定追加的head
            }
        }

        if (!StringTool.isNull(info.getExpand())) {
            requestRead.addQueryParameter("$expand", info.getExpand()); // 展开表结构
        }

        if (!StringTool.isNull(info.getFilter())) {
            requestRead.addQueryParameter("$filter", info.getFilter()); // 过滤条件
        }
        if (!StringTool.isNull(info.getFormat())) {
            requestRead.addQueryParameter("$format", info.getFormat()); // 返回结果的格式 一般为 json
        }
        if (!StringTool.isNull(info.getOrderBy())) {
            requestRead.addQueryParameter("$orderby", info.getOrderBy()); // 排序条件
        }

        if (info.getPageRecord() != null && info.getPageRecord() > 0 && currentPage != null) { // 每页记录数大于0 的时候
            requestRead.addQueryParameter("$top", info.getPageRecord().toString());
            Integer skip = info.getPageRecord() * currentPage; // 从第0页开始
            requestRead.addQueryParameter("$skip", skip.toString()); // 0的时候 从第一条记录取值
        }

        if (addParaMap != null) {
            for (String key : addParaMap.keySet()) {
                requestRead.addQueryParameter(key, addParaMap.get(key)); // 设定追加的参数
            }
        }

        logger.info("queryParameter:" + JSON.toJSONString(requestRead.getQueryString()));

        if (body != null) {

        }

        return requestRead.execute(client);

    }

    @SuppressWarnings("null")
    public static String post(T11IfManager info, String body, HashMap<String, String> headMap)
            throws UnsupportedOperationException, IOException {

        logger.info("post paramater=============================");
        logger.info(body);

        HttpClient client = null;

        if (Eenvironment.isWindows()) {
            DefaultHttpDestination destination = DefaultHttpDestination.builder(info.getUrl())
                    .header(new Header("X-Destination-Name", desName)).build();
            client = HttpClientAccessor.getHttpClient(destination);
        } else {
            Destination destination = DestinationAccessor.getDestination(desName);
            client = HttpClientAccessor.getHttpClient(destination);
        }

        // 这是get方法
        ODataRequestCreate requestcreate = new ODataRequestCreate(info.getServicepath(), info.getEntityname(), body,
                ODataProtocol.V2);
        String userInfo = Base64Util.encode(info.getSapuser());// 用户名,密码
        requestcreate.addHeader("Authorization", "Basic " + userInfo);
        requestcreate.addHeader("Accept-Language", info.getLangCode());
        requestcreate.addHeader("Content-Type", "application/json");
        requestcreate.addHeader("Accept", "application/json");

        if (headMap != null) {
            for (String key : headMap.keySet()) {
                requestcreate.addHeader(key, headMap.get(key)); // 设定追加的head
            }
        }

        ODataRequestResultGeneric resultGene = requestcreate.execute(client);

        final InputStream httpResponseContent = resultGene.getHttpResponse().getEntity().getContent();
        String a = StringTool.InputStream2String(httpResponseContent);
        logger.info("post return=============================");
        logger.info(a);

        httpResponseContent.close();
        return a;

    }

    // 设定 开启 V2/V4的计数
    public static HashMap<String, String> setCountPara(boolean isV4, HashMap<String, String> addParaMap) {
        if (addParaMap == null)
            addParaMap = new HashMap<String, String>();
        if (isV4) {
            addParaMap.put("$count", "true"); // 开启记录计数 V4
        } else {
            addParaMap.put("$inlinecount", "allpages"); // 开启记录计数 V2
        }
        return addParaMap;
    }

    // 得到 V4的计数结果 (V2自动能拿到)
    public static Integer getCount(String a) {
        JSONObject jsonObject = JSON.parseObject(a);
        return jsonObject.getInteger("@odata.count");
    }

}
