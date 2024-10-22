package k_paas.balloon.keeper.infrastructure.persistence.objectStorage.ncp;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

@Slf4j
@Component
public class NcpObjectStorageService {

    private static final String bucketName = "contest73-bucket";

    private final AmazonS3Client amazonS3Client;

    public NcpObjectStorageService(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public String putObject(String localFileName, String path) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(0L);
        objectMetadata.setContentType("application/x-directory");
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderName, new ByteArrayInputStream(new byte[0]), objectMetadata);

        String folderName = path;

        try {
            amazonS3Client.putObject(putObjectRequest);
            System.out.format("Folder %s has been created.\n", folderName);
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }

        String objectName = folderName + localFileName;
//                "climate_data_" + LocalTime.now(ZoneId.of("Asia/Seoul")) + ".csv";

        try {
            File file = new File(localFileName);
            validateFileSize(file);
            amazonS3Client.putObject(bucketName, objectName, file);
            log.info("Object {} has been created.", objectName);
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }

        return objectName;
    }

    private void validateFileSize(File file) {
        long fileSizeInBytes = file.length();
        double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);

        if (fileSizeInBytes == 0) {
            throw new IllegalArgumentException("File size is 0 bytes. Cannot upload empty file.");
        }

        log.info("File size: {:.2f} MB", fileSizeInMB);
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
