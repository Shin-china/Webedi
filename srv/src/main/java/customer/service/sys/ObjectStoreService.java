package customer.service.sys;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
// import software.amazon.awssdk.services.s3.model.*;
import org.springframework.stereotype.Service;

import cds.gen.sys.T12Config;
import customer.dao.sys.T12ConfigDao;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
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
                case "S3_URI":
                    S3_URL = config.getConValue();
                    break;
                case "S3_BUCKET":
                    S3_BUCKET = config.getConValue();
                    break;
                case "S3_REGION":
                    S3_REGION = config.getConValue();
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
                    // .endpointOverride(URI.create(S3_URL))
                    .serviceConfiguration(item -> item.pathStyleAccessEnabled(true).checksumValidationEnabled(false))
                    .region(Region.of(S3_REGION))
                    .build();
        }
        return s3Client;
    }

    // Get S3 List
    public List<S3Object> getS3List() throws S3Exception {
        ListObjectsRequest listObjects = ListObjectsRequest.builder().bucket(S3_BUCKET).build();
        ListObjectsResponse res = getS3Client().listObjects(listObjects);
        return res.contents();
    }
}