package k_paas.balloon.keeper.infrastructure.persistence.objectStorage.ncp;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NcpObjectStorageService {

    private static final String bucketName = "contest73-bucket";
    private static final String folderName = "climate/";

    private final AmazonS3Client amazonS3Client;

    public NcpObjectStorageService(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public String putObject(String localFileName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(0L);
        objectMetadata.setContentType("application/x-directory");
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName, new ByteArrayInputStream(new byte[0]), objectMetadata);

        try {
            amazonS3Client.putObject(putObjectRequest);
            System.out.format("Folder %s has been created.\n", folderName);
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }

        String objectName = folderName + "climate_data_" + LocalTime.now(ZoneId.of("Asia/Seoul")) + ".csv";

        try {
            amazonS3Client.putObject(bucketName, objectName, new File(localFileName));
            log.info("Object {} has been created.", objectName);
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }

        return objectName;
    }

    public String getLatestObjectPath() {
        try {
            ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                    .withBucketName(bucketName)
                    .withPrefix(folderName);

            ListObjectsV2Result result;
            String latestObjectKey = null;
            long latestLastModified = 0;

            do {
                result = amazonS3Client.listObjectsV2(listObjectsRequest);
                List<S3ObjectSummary> objects = result.getObjectSummaries();

                for (S3ObjectSummary os : objects) {
                    if (os.getLastModified().getTime() > latestLastModified) {
                        latestLastModified = os.getLastModified().getTime();
                        latestObjectKey = os.getKey();
                    }
                }

                listObjectsRequest.setContinuationToken(result.getNextContinuationToken());
            } while (result.isTruncated());

            if (latestObjectKey != null) {
                log.info("Latest uploaded file path: {}", latestObjectKey);
                return latestObjectKey;
            } else {
                log.info("No objects found in the folder.");
                return null;
            }
        } catch (SdkClientException e) {
            log.error("Error retrieving latest object: ", e);
            return null;
        }
    }
}
