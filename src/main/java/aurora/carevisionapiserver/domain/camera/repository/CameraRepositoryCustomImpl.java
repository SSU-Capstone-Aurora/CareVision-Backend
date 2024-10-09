package aurora.carevisionapiserver.domain.camera.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import aurora.carevisionapiserver.domain.bed.domain.QBed;
import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.domain.QCamera;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CameraRepositoryCustomImpl implements CameraRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Camera> sortByBedInfo() {
        QBed bed = QBed.bed;
        QCamera camera = QCamera.camera;
        return queryFactory
                .selectFrom(camera)
                .leftJoin(bed)
                .on(camera.patient.id.eq(bed.patient.id))
                .orderBy(
                        bed.inpatientWardNumber.asc(),
                        bed.patientRoomNumber.asc(),
                        bed.bedNumber.asc())
                .fetch();
    }
}
