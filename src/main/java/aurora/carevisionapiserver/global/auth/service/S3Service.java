package aurora.carevisionapiserver.global.auth.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import aurora.carevisionapiserver.global.exception.S3Exception;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.util.UriFormatter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class S3Service {
    private static final String THUMBNAIL_PATH = "thumbnail/";

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final UriFormatter uriFormatter;

    public String getRecentImage(Long patientId) {
        List<S3ObjectSummary> objectSummaries = getS3ObjectsByPrefix(patientId);

        S3ObjectSummary mostRecentObject =
                Collections.max(
                        objectSummaries, Comparator.comparing(S3ObjectSummary::getLastModified));

        return uriFormatter.getThumbnailUrl(bucket, mostRecentObject.getKey());
    }

    public void deleteOldThumbnail(Long patientId, int daysOld) {
        List<S3ObjectSummary> objectSummaries = getS3ObjectsByPrefix(patientId);

        Date thresholdDate =
                new Date(System.currentTimeMillis() - (daysOld * 24L * 60 * 60 * 1000));

        if (objectSummaries.size() <= 1) {
            return;
        }
        for (S3ObjectSummary summary : objectSummaries) {
            if (summary.getLastModified().before(thresholdDate)) {
                String objectKey = summary.getKey();
                amazonS3.deleteObject(bucket, objectKey);
            }
        }
    }

    private List<S3ObjectSummary> getS3ObjectsByPrefix(Long patientId) {
        ListObjectsV2Request request =
                new ListObjectsV2Request()
                        .withBucketName(bucket)
                        .withPrefix(THUMBNAIL_PATH + patientId.toString());

        ListObjectsV2Result result = amazonS3.listObjectsV2(request);
        List<S3ObjectSummary> objectSummaries = result.getObjectSummaries();

        if (objectSummaries.isEmpty()) {
            throw new S3Exception(ErrorStatus.EMPTY_S3_IMAGE);
        }
        return objectSummaries;
    }
}
