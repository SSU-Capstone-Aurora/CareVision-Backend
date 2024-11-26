package aurora.carevisionapiserver.global.auth.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import aurora.carevisionapiserver.global.exception.S3Exception;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String getRecentImage(Long patientId) {
        ListObjectsV2Request request =
                new ListObjectsV2Request().withBucketName(bucket).withPrefix(patientId.toString());

        ListObjectsV2Result result = amazonS3.listObjectsV2(request);
        List<S3ObjectSummary> objectSummaries = result.getObjectSummaries();

        if (objectSummaries.isEmpty()) {
            throw new S3Exception(ErrorStatus.EMPTY_S3_IMAGE);
        }

        S3ObjectSummary mostRecentObject =
                Collections.max(
                        objectSummaries, Comparator.comparing(S3ObjectSummary::getLastModified));

        return mostRecentObject.getKey();
    }
}
