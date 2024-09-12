package customer.sap;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;

import cds.gen.sys.T11IfManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.util.Base64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 * @author: leon.chen
 * @date: 2020/10/21
 * 
 */
public class RestHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestHttpClient.class);
    private static CloseableHttpClient httpClient;

    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static SSLConnectionSocketFactory sslsf = null;
    private static PoolingHttpClientConnectionManager cm = null;
    private static SSLContextBuilder builder = null;

    private RestHttpClient() {
    }

    static {

        // 注册访问协议相关的Socket工厂
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", SSLConnectionSocketFactory.getSocketFactory()).build();

        // HttpConnectionFactory:配置写请求/解析响应处理器
        HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connectionFactory = new ManagedHttpClientConnectionFactory(
                DefaultHttpRequestWriterFactory.INSTANCE, DefaultHttpResponseParserFactory.INSTANCE);

        // DNS解析器
        DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;
        // 创建连接池管理器
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(socketFactoryRegistry,
                connectionFactory, dnsResolver);
        // 设置默认的socket参数
        manager.setDefaultSocketConfig(SocketConfig.custom().setTcpNoDelay(true).build());
        // 设置最大连接数。高于这个值时，新连接请求，需要阻塞，排队等待
        manager.setMaxTotal(1000);
        // 路由是对MaxTotal的细分。
        // 每个路由实际最大连接数默认值是由DefaultMaxPerRoute控制。
        // MaxPerRoute设置的过小，无法支持大并发：ConnectionPoolTimeoutException:Timeout waiting for
        // connection from pool
        // 每个路由的最大连接
        manager.setDefaultMaxPerRoute(200);
        // 在从连接池获取连接时，连接不活跃多长时间后需要进行一次验证，默认为2s
        manager.setValidateAfterInactivity(5 * 1000);

        // 配置默认的请求参数
        // 连接超时设置为2s
        // 等待数据超时设置为5s
        // 从连接池获取连接的等待超时时间设置为2s
        // .setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.0.2",
        // 1234))) //设置代理
        RequestConfig requestConfig = RequestConfig.custom()
                // 连接超时设置为2s
                .setConnectTimeout(5 * 1000)
                // 等待数据超时设置为5s
                .setSocketTimeout(5 * 60 * 1000)
                // 从连接池获取连接的等待超时时间设置为2s
                .setConnectionRequestTimeout(2 * 1000)
                // .setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.0.2",
                // 1234))) //设置代理
                .build();

        httpClient = HttpClients.custom().setConnectionManager(manager)
                // 连接池不是共享模式，这个共享是指与其它httpClient是否共享
                .setConnectionManagerShared(false)
                // 定期回收空闲连接
                .evictIdleConnections(60, TimeUnit.SECONDS)
                // 回收过期连接
                .evictExpiredConnections()
                // 连接存活时间，如果不设置，则根据长连接信息决定
                .setConnectionTimeToLive(60, TimeUnit.SECONDS)
                // 设置默认的请求参数
                .setDefaultRequestConfig(requestConfig)
                // 连接重用策略，即是否能keepAlive
                .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
                // 长连接配置，即获取长连接生产多长时间
                .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
                // 设置重试次数，默认为3次；当前是禁用掉
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();
    }

    public static Client getClient(String url) {
        return new Client(url);
    }

    private static String transformUrl(String url, Map<String, String> pathParams, Map<String, Object> queryParams) {
        if (pathParams != null && !pathParams.isEmpty()) {
            for (Map.Entry<String, String> entry : pathParams.entrySet()) {
                try {
                    url = url.replaceAll("\\{" + entry.getKey() + "}",
                            URLEncoder.encode(entry.getValue().trim(), "UTF-8").replaceAll("\\+", "%20"));
                } catch (UnsupportedEncodingException ignored) {
                }
            }
        }
        if (queryParams != null && !queryParams.isEmpty()) {
            if (url.contains("?")) {
                url += "&" + toUrlParams(queryParams);
            } else {
                url += "?" + toUrlParams(queryParams);
            }
        }
        return url;
    }

    private static String toUrlParams(Map<String, Object> paras) {
        if (paras != null && !paras.isEmpty()) {
            StringBuilder urlParams = new StringBuilder();
            for (Map.Entry<String, Object> entry : paras.entrySet()) {
                try {
                    urlParams
                            .append(entry.getKey()).append("=").append(URLEncoder
                                    .encode(String.valueOf(entry.getValue()).trim(), "UTF-8").replaceAll("\\+", "%20"))
                            .append("&");
                } catch (UnsupportedEncodingException ignored) {
                }
            }
            if (urlParams.length() > 0) {
                return urlParams.substring(0, urlParams.length() - 1);
            }
        }
        return null;
    }

    public static class Client {
        private String url;
        private RequestMethod method;
        private List<Header> headers;
        private Map<String, String> pathParams;
        private Map<String, Object> queryParams;
        private Map<String, Object> postParams;
        private String body;
        private boolean json = false;
        private boolean file = false;
        private MultipartFile[] files;

        public Client(String url) {
            this.url = url;
            this.headers = new ArrayList<>();
            this.pathParams = new HashMap<>();
            this.queryParams = new HashMap<>();
            this.postParams = new HashMap<>();
            this.files = new MultipartFile[0];
        }

        public Client get() {
            this.method = RequestMethod.GET;
            return this;
        }

        public Client post() {
            this.method = RequestMethod.POST;
            return this;
        }

        public Client put() {
            this.method = RequestMethod.PUT;
            return this;
        }

        public Client delete() {
            this.method = RequestMethod.DELETE;
            return this;
        }

        public Client json() {
            json = true;
            return this;
        }

        public Client isFile() {
            file = true;
            return this;
        }

        public Client method(String method) {
            this.method = RequestMethod.valueOf(method);
            return this;
        }

        public Client addPathParams(Map<String, String> pathParams) {
            this.pathParams = pathParams;
            return this;
        }

        public Client addPathParam(String key, String value) {
            pathParams.put(key, value);
            return this;
        }

        public Client addMultipartFiles(MultipartFile[] files) {
            this.files = files;
            return this;
        }

        public Client body(Object body) {
            try {
                this.json = true;
                // this.body = new ObjectMapper().writeValueAsString(body);
                this.body = new Gson().toJson(body);

                LOGGER.info("过账参数:" + this.body);
            } catch (Exception e) {
                LOGGER.error("", e);
                throw new IllegalArgumentException("非标准json对象");
            }
            return this;
        }

        public Client addQueryParams(Map<String, Object> queryParams) {
            this.queryParams = queryParams;
            return this;
        }

        public Client addQueryParam(String key, String value) {
            queryParams.put(key, value);
            return this;
        }

        public Client addPostParams(Map<String, Object> postParams) {
            this.postParams = postParams;
            return this;
        }

        public Client addPostParam(String key, String value) {
            postParams.put(key, value);
            return this;
        }

        public Client addHeaders(Map<String, String> headers) {
            headers.forEach((name, value) -> this.headers.add(new BasicHeader(name, value)));
            return this;
        }

        public Client addHeader(String key, String value) {
            this.headers.add(new BasicHeader(key, value));
            return this;
        }

    }

    public static String sendHttpPost(T11IfManager info, String jsonString) throws Exception {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        String userInfo = Base64Util.encode(info.getSapuser());// 用户名,密码
        // String userInfo = Base64Util
        // .encode();

        HttpPost httpPost = new HttpPost("https://my409764-api.s4hana.cloud.sap/sap/bc/http/sap/z_http_podata_001test");
        StringEntity s = new StringEntity(jsonString, "utf-8");
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("x-csrf-token", "fetch");
        httpPost.addHeader("Accept-Language", "ja");
        httpPost.addHeader("Authorization", "Basic " + userInfo);
        httpPost.setEntity(s);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        LOGGER.info("访问CPI接口状态{}", response.getStatusLine().getStatusCode());
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            String responseContent = EntityUtils.toString(entity, "UTF-8");
            response.close();
            httpClient.close();
            return responseContent;
        }
        return null;
    }
}