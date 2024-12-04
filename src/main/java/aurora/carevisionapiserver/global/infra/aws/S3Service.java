package aurora.carevisionapiserver.global.infra.aws;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import aurora.carevisionapiserver.global.exception.S3Exception;
import aurora.carevisionapiserver.global.response.code.status.ErrorStatus;
import aurora.carevisionapiserver.global.util.UriFormatter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class S3Service {
    private static final String STREAMING_THUMBNAIL_PATH = "thumbnail/";
    private static final String SAVED_VIDEO_THUMBNAIL_PATH = "thumbnail-for-saved-video/";
    private static final String VIDEO_PATH = "video/";
    private static final int VIDEOS_PER_PATIENT = 17;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final UriFormatter uriFormatter;

    private String extractS3Key(String uri) {
        try {
            URL url = new URL(uri);
            String path = url.getPath();
            return path.substring(path.indexOf('/', 1) + 1);
        } catch (MalformedURLException e) {
            throw new S3Exception(ErrorStatus.INVALID_S3_LINK);
        }
    }

    public String getRecentImage(Long patientId) {
        List<S3ObjectSummary> objectSummaries =
                getS3ObjectsByPrefix(patientId, STREAMING_THUMBNAIL_PATH);

        S3ObjectSummary mostRecentObject =
                Collections.max(
                        objectSummaries, Comparator.comparing(S3ObjectSummary::getLastModified));

        return uriFormatter.getThumbnailUrl(bucket, mostRecentObject.getKey());
    }

    public String getSavedVideoThumbnail(Long patientId, Long videoId) {
        int fileNumber = (int) ((videoId - 1) % VIDEOS_PER_PATIENT) + 1;
        String key = SAVED_VIDEO_THUMBNAIL_PATH + patientId + "/" + fileNumber + ".jpg";
        if (!amazonS3.doesObjectExist(bucket, key)) {
            throw new S3Exception(ErrorStatus.EMPTY_S3_IMAGE);
        }
        return uriFormatter.getThumbnailUrl(bucket, key);
    }

    public void deleteOldThumbnail(Long patientId, int daysOld) {
        List<S3ObjectSummary> objectSummaries =
                getS3ObjectsByPrefix(patientId, STREAMING_THUMBNAIL_PATH);

        Date thresholdDate =
                new Date(System.currentTimeMillis() - (daysOld * 24L * 60 * 60 * 1000));

        for (S3ObjectSummary summary : objectSummaries) {
            if (summary.getSize() != 1 && summary.getLastModified().before(thresholdDate)) {
                String objectKey = summary.getKey();
                amazonS3.deleteObject(bucket, objectKey);
            }
        }
    }

    private List<S3ObjectSummary> getS3ObjectsByPrefix(Long patientId, String basePath) {
        ListObjectsV2Request request =
                new ListObjectsV2Request()
                        .withBucketName(bucket)
                        .withPrefix(basePath + patientId.toString());

        ListObjectsV2Result result = amazonS3.listObjectsV2(request);
        List<S3ObjectSummary> objectSummaries = result.getObjectSummaries();

        if (objectSummaries.isEmpty()) {
            throw new S3Exception(ErrorStatus.EMPTY_S3_IMAGE);
        }
        return objectSummaries;
    }

    public String getVideoDuration(String link) {
        String key = extractS3Key(link);
        Map<String, String> metadata = getObjectMetadata(VIDEO_PATH + key);
        return metadata.get("duration");
    }

    public Map<String, String> getObjectMetadata(String key) {
        ObjectMetadata metadata = amazonS3.getObjectMetadata(bucket, key);
        return metadata.getUserMetadata();
    }
}
