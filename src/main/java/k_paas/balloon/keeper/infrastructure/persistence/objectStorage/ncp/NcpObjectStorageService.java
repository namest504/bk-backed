package k_paas.balloon.keeper.infrastructure.persistence.objectStorage.ncp;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NcpObjectStorageService {

    private final AmazonS3Client amazonS3Client;
    private final NcpProperty ncpProperty;

    /**
     * NCP Object Storage 버킷의 지정된 경로에 객체를 저장
     * 경로가 존재하지 않는 경우 폴더를 생성
     *
     * @param localFileName NCP Object Storage 버킷에 업로드할 로컬 파일의 이름
     * @param path 객체가 저장될 버킷 내의 경로
     * @return NCP Object Storage 버킷에 있는 객체의 전체 이름
     */
    public String putObject(String localFileName, String path) {
        String folderName = path;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(0L);
        objectMetadata.setContentType("application/x-directory");
        PutObjectRequest putObjectRequest = new PutObjectRequest(ncpProperty.bucketName(), folderName, new ByteArrayInputStream(new byte[0]), objectMetadata);


        try {
            amazonS3Client.putObject(putObjectRequest);
            System.out.format("Folder %s has been created.\n", folderName);
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }

        String objectName = folderName + localFileName;

        try {
            File file = new File(localFileName);
            validateFileSize(file);
            amazonS3Client.putObject(ncpProperty.bucketName(), objectName, file);
            log.info("Object {} has been created.", objectName);
        } catch (AmazonS3Exception e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }

        return objectName;
    }

    /**
     * 지정된 파일의 크기를 확인
     *
     * @param file 크기를 검증할 파일
     */
    private void validateFileSize(File file) {
        long fileSizeInBytes = file.length();
        double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);

        if (fileSizeInBytes == 0) {
            throw new IllegalArgumentException("File size is 0 bytes. Cannot upload empty file.");
        }

        log.info("File size: {:.2f} MB", fileSizeInMB);
    }

    /**
     * NCP Object Storage 버킷내에 지정된 경로 내에서 최신 객체의 경로를 검색
     *
     * @param path 최신 객체를 검색하기 위한 S3 버킷의 접두사 경로
     * @return 지정된 경로 내에서 발견된 최신 객체의 경로. 객체가 발견되지 않은 경우 null
     */
    public String getLatestObjectPath(String path) {
        try {
            ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                    .withBucketName(ncpProperty.bucketName())
                    .withPrefix(path);

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
