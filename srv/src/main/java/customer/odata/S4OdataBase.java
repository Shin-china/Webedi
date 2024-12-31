package customer.odata;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpClientFactory;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.Header;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientFactory;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;

import cds.gen.sys.T11IfManager;
import customer.comm.base.MyToken;
import customer.comm.tool.Eenvironment;
import customer.comm.tool.HttpTools;

public class S4OdataBase {

    private static final Logger logger = LoggerFactory.getLogger(S4OdataBase.class);
    public static String desName = "XXXXX";

    public static HttpClient getClent(T11IfManager info) {
        HttpClient client = null;

        if (Eenvironment.isWindows()) {
            DefaultHttpDestination destination = DefaultHttpDestination.builder(info.getUrl())
                    .header(new Header("X-Destination-Name", desName)).build();
            HttpClientFactory factory = DefaultHttpClientFactory.builder().timeoutMilliseconds(600000).build();
            client = factory.createHttpClient(destination);
        } else {
            // Destination destination = DestinationAccessor.getDestination(desName);
            DefaultHttpDestination destination = DefaultHttpDestination.builder(info.getUrl())
                    .header(new Header("X-Destination-Name", desName)).build();

            HttpClientFactory factory = DefaultHttpClientFactory.builder().timeoutMilliseconds(600000).build();

            client = factory.createHttpClient(destination);
            // client = HttpClientAccessor.getHttpClient(destination);
        }
        return client;
    }

    private static String getHttpToken(T11IfManager info) throws UnsupportedOperationException, IOException {

        logger.info("if info=============================" + info.getServicepath());

        HttpClient client = getClent(info);
        // 这是get方法
        ODataRequestRead requestRead = new ODataRequestRead(info.getServicepath(), "", null, ODataProtocol.V2);

        requestRead.addHeader("Authorization", HttpTools.getBaiscAuthorization(info));
        requestRead.addHeader("x-csrf-token", "fetch");
        requestRead.addHeader("sap-language", info.getLangCode());
        ODataRequestResultGeneric resultGene = requestRead.execute(client);
        org.apache.http.Header sss = resultGene.getHttpResponse().getFirstHeader("x-csrf-token");
        return sss.getValue();
    }

    public static MyToken getToken(T11IfManager info, String userId) throws Exception {
        MyToken token = HttpTools.tokenMap.get(userId);
        if (token == null || token.isExpire()) {
            token = new MyToken();

            HttpClient client = getClent(info);
            // 这是get方法
            ODataRequestRead requestRead = new ODataRequestRead(info.getServicepath(), "", null, ODataProtocol.V2);

            requestRead.addHeader("Authorization", HttpTools.getBaiscAuthorization(info));
            requestRead.addHeader("x-csrf-token", "fetch");
            requestRead.addHeader("sap-language", info.getLangCode());
            ODataRequestResultGeneric resultGene = requestRead.execute(client);
            HttpResponse response = resultGene.getHttpResponse();
            org.apache.http.Header sss = resultGene.getHttpResponse().getFirstHeader("x-csrf-token");

            logger.info(sss.getValue());

            token.createToken(client, sss.getValue(), 0); // 默认10分钟
            HttpTools.tokenMap.put(userId, token);
        }
        return token;
    }
}
