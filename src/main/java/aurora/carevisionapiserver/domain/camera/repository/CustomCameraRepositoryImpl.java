package aurora.carevisionapiserver.domain.camera.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import aurora.carevisionapiserver.domain.bed.domain.QBed;
import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.domain.QCamera;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.QDepartment;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomCameraRepositoryImpl implements CustomCameraRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Camera> findAllCamerasSortedByBed(Department department, Long lastIdx, int size) {
        QCamera camera = QCamera.camera;
        QBed bed = QBed.bed;

        List<Camera> cameras =
                queryFactory
                        .select(camera)
                        .from(camera)
                        .leftJoin(bed)
                        .on(bed.camera.eq(camera))
                        .leftJoin(QDepartment.department)
                        .on(bed.department.eq(department))
                        .where(extractNumericPart(lastIdx, camera))
                        .orderBy(
                                bed.inpatientWardNumber.asc(),
                                bed.patientRoomNumber.asc(),
                                bed.bedNumber.asc())
                        .limit(size + 1)
                        .fetch();

        boolean hasNext = cameras.size() > size;
        if (hasNext) {
            cameras = cameras.subList(0, size);
        }

        return new SliceImpl<>(cameras, Pageable.unpaged(), hasNext);
    }

    private static BooleanExpression extractNumericPart(Long lastIdx, QCamera camera) {
        return Expressions.numberTemplate(
                        Long.class, "regexp_replace({0}, '^[^0-9]+', '')", camera.id)
                .gt(lastIdx);
    }

    private static BooleanExpression isEqualToDepartment(Long departmentId, QCamera camera) {
        return camera.bed.department.id.eq(departmentId);
    }

    @Override
    public List<Camera> findCamerasUnlinkedToPatientSortedByBed(long departmentId) {
        QBed bed = QBed.bed;
        QCamera camera = QCamera.camera;
        return queryFactory
                .selectFrom(camera)
                .leftJoin(bed)
                .on(camera.bed.id.eq(bed.id))
                .where(isEqualToDepartment(departmentId, camera), camera.bed.patient.id.isNull())
                .orderBy(
                        bed.inpatientWardNumber.asc(),
                        bed.patientRoomNumber.asc(),
                        bed.bedNumber.asc())
                .fetch();
    }
}
