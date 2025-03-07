package aurora.carevisionapiserver.domain.camera.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import aurora.carevisionapiserver.domain.camera.domain.QVideo;
import aurora.carevisionapiserver.domain.camera.domain.Video;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomVideoRepositoryImpl implements CustomVideoRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Video> findByPatient(Patient patient, Long lastIdx, int size) {
        QVideo video = QVideo.video;

        List<Video> videos =
                queryFactory
                        .select(video)
                        .from(video)
                        .where(getVideoConditions(video, patient, lastIdx))
                        .orderBy(video.id.asc())
                        .limit(size + 1)
                        .fetch();

        boolean hasNext = videos.size() > size;
        if (hasNext) {
            videos = videos.subList(0, size);
        }

        return new SliceImpl<>(videos, Pageable.unpaged(), hasNext);
    }

    private BooleanExpression getVideoConditions(QVideo video, Patient patient, Long lastIdx) {
        return isPatientMatchingOrNull(video, patient).and(isGreaterThanLastIdx(video, lastIdx));
    }

    private static BooleanExpression isGreaterThanLastIdx(QVideo video, Long lastIdx) {
        return video.id.gt(lastIdx);
    }

    private static BooleanExpression isPatientMatchingOrNull(QVideo video, Patient patient) {
        return patient != null ? video.patient.eq(patient) : video.patient.isNull();
    }
}
