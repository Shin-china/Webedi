package customer.service.sys;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
// import software.amazon.awssdk.services.s3.model.*;
import org.springframework.stereotype.Service;

import cds.gen.sys.T12Config;
import customer.bean.com.CommMsg;
import customer.bean.com.UmcConstants;
import customer.dao.sys.T12ConfigDao;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.regions.Region;

@Service
public class ObjectStoreService {
    public String S3_ACCESS_KEY_ID;
    public String S3_SECRET_ACCESS_KEY;
    public String S3_URL;
    public String S3_BUCKET;
    public String S3_REGION;
    public S3Client s3Client;
    // Get S3 Information
    @Autowired
    private T12ConfigDao Config;

    // Consu

    // Initialization S3 Client
    public void initS3Client() {

        // Get S3 Information
        List<T12Config> S3Config = Config.get("S3");
        for (T12Config config : S3Config) {
            switch (config.getConCode()) {
                case "S3_ACCESS_KEY":
                    S3_ACCESS_KEY_ID = config.getConValue();
                    break;
                case "S3_ACCESS_SECRET":
                    S3_SECRET_ACCESS_KEY = config.getConValue();
                    break;
                case "S3_HOST":
                    S3_URL = config.getConValue();
                    break;
                case "S3_BUCKET":
                    S3_BUCKET = config.getConValue();
                    break;
                case "S3_REGION":
                    S3_REGION = config.getConValue();
                    break;
                default:
                    break;
            }
        }

    }

    // Get S3 Client
    public synchronized S3Client getS3Client() {
        initS3Client();
        if (s3Client == null) {
            s3Client = S3Client.builder()
                    .credentialsProvider(
                            StaticCredentialsProvider
                                    .create(AwsBasicCredentials.create(S3_ACCESS_KEY_ID, S3_SECRET_ACCESS_KEY)))
                    .region(Region.of(S3_REGION))
                    .build();
        }
        return s3Client;
    }

    // Get S3 List
    public List<S3Object> getS3List() throws S3Exception {
        S3Client s3Client = getS3Client();
        ListObjectsRequest listObjects = ListObjectsRequest.builder().bucket(S3_BUCKET).build();
        ListObjectsResponse res = s3Client.listObjects(listObjects);
        return res.contents();
    }

    // Upload attachment
    public CommMsg uploadFile(String key, RequestBody fileBody) throws S3Exception {
        List<T12Config> getFolder = Config.get("OBJECT_STOTE_FLORD");
        for (T12Config folder : getFolder) {
            if (folder.getConCode().equals("OBJECT_STOTE_FLORD")) {
                key = folder.getConValue() + key;
            }
        }

        S3Client s3Client = getS3Client();
        CommMsg msg = new CommMsg();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(S3_BUCKET)
                .key(key)
                .build();
        PutObjectResponse por = s3Client.putObject(request, fileBody);
        if (por != null && por.sdkHttpResponse() != null) {
            if (por.sdkHttpResponse().isSuccessful()) {
                msg.setMsgType(UmcConstants.IF_STATUS_S);
                msg.setMsgTxt(key);
            } else {
                if (por.sdkHttpResponse().statusText().isPresent()) {
                    msg.setMsgTxt(por.sdkHttpResponse().statusText().get());
                }
            }
        } else {
            msg.setMsgTxt("S3 Have no return ");
        }

        return msg;
    }

    // 下载对象
    public ResponseBytes downLoadRes(String keyName) throws S3Exception {
        S3Client s3Client = getS3Client();
        GetObjectRequest objectRequest = GetObjectRequest.builder().key(keyName).bucket(S3_BUCKET).build();
        ResponseBytes<GetObjectResponse> por = s3Client.getObjectAsBytes(objectRequest);

        return por;

    }

    // 下载模板
    public ResponseBytes downTempRes(String keyName) throws S3Exception {
        S3Client s3Client = getS3Client();
        List<T12Config> getFolder = Config.get("OBJECT_STORE_TEMPLATE");
        for (T12Config folder : getFolder) {
            if (folder.getConCode().equals("OBJECT_STORE_TEMPLATE")) {
                keyName = folder.getConValue() + keyName;
            }
        }
        GetObjectRequest objectRequest = GetObjectRequest.builder().key(keyName).bucket(S3_BUCKET).build();
        ResponseBytes<GetObjectResponse> por = s3Client.getObjectAsBytes(objectRequest);

        return por;

    }
}