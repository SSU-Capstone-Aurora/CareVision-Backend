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
public class CustomCameraRepositoryImpl implements CustomCameraRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Camera> findAllCamerasSortedByBed(long departmentId) {
        QBed bed = QBed.bed;
        QCamera camera = QCamera.camera;
        return queryFactory
                .selectFrom(camera)
                .leftJoin(bed)
                .on(camera.bed.id.eq(bed.id))
                .where(camera.bed.department.id.eq(departmentId))
                .orderBy(
                        bed.inpatientWardNumber.asc(),
                        bed.patientRoomNumber.asc(),
                        bed.bedNumber.asc())
                .fetch();
    }

    @Override
    public List<Camera> findCamerasUnlinkedToPatientSortedByBed(long departmentId) {
        QBed bed = QBed.bed;
        QCamera camera = QCamera.camera;
        return queryFactory
                .selectFrom(camera)
                .leftJoin(bed)
                .on(camera.bed.id.eq(bed.id))
                .where(camera.bed.department.id.eq(departmentId), camera.bed.patient.id.isNull())
                .orderBy(
                        bed.inpatientWardNumber.asc(),
                        bed.patientRoomNumber.asc(),
                        bed.bedNumber.asc())
                .fetch();
    }
}
