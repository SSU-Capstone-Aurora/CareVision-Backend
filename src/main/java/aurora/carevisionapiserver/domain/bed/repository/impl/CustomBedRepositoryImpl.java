package aurora.carevisionapiserver.domain.bed.repository.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.bed.domain.QBed;
import aurora.carevisionapiserver.domain.bed.repository.CustomBedRepository;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.QDepartment;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomBedRepositoryImpl implements CustomBedRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Bed> findNextBeds(Department department, Bed lastBed, int size) {
        QBed bed = QBed.bed;
        return queryFactory
                .selectFrom(bed)
                .leftJoin(QDepartment.department)
                .on(isEqualToDepartment(bed, department))
                .where(findBedsGreaterThan(bed, lastBed))
                .orderBy(
                        bed.inpatientWardNumber.asc(),
                        bed.patientRoomNumber.asc(),
                        bed.bedNumber.asc())
                .limit(size + 1)
                .fetch();
    }

    private static BooleanExpression isEqualToDepartment(QBed bed, Department department) {
        return department != null ? bed.department.eq(department) : bed.department.isNull();
    }

    private static BooleanExpression findBedsGreaterThan(QBed bed, Bed lastBed) {
        return bed.id
                .ne(lastBed.getId())
                .and(
                        bed.inpatientWardNumber
                                .gt(lastBed.getInpatientWardNumber())
                                .or(
                                        bed.inpatientWardNumber
                                                .eq(lastBed.getInpatientWardNumber())
                                                .and(
                                                        bed.patientRoomNumber.gt(
                                                                lastBed.getPatientRoomNumber())))
                                .or(
                                        bed.inpatientWardNumber
                                                .eq(lastBed.getInpatientWardNumber())
                                                .and(
                                                        bed.patientRoomNumber.eq(
                                                                lastBed.getPatientRoomNumber()))
                                                .and(bed.bedNumber.gt(lastBed.getBedNumber()))));
    }
}
