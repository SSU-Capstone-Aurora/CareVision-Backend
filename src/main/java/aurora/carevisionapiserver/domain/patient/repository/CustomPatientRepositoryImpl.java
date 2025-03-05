package aurora.carevisionapiserver.domain.patient.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.domain.patient.domain.QPatient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomPatientRepositoryImpl implements CustomPatientRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Patient> findPatientByAdmin(Admin admin, Long lastIdx, int size) {
        QPatient patient = QPatient.patient;

        List<Patient> patients =
                queryFactory
                        .selectFrom(patient)
                        .where(
                                isEqualToHospital(patient, admin)
                                        .and(isGreaterThan(patient, lastIdx)))
                        .fetch();

        boolean hasNext = patients.size() > size;
        if (hasNext) {
            patients = patients.subList(0, size);
        }
        return new SliceImpl<>(patients, Pageable.unpaged(), hasNext);
    }

    private static BooleanExpression isEqualToHospital(QPatient patient, Admin admin) {
        return patient.department.hospital.name.eq(admin.getDepartment().getHospital().getName());
    }

    @Override
    public Slice<Patient> findPatientByNurse(Nurse nurse, Long lastIdx, int size) {
        QPatient patient = QPatient.patient;

        List<Patient> patients =
                queryFactory
                        .select(patient)
                        .from(patient)
                        .where(isGreaterThan(patient, lastIdx).and(isEqTo(patient, nurse)))
                        .orderBy(patient.id.asc())
                        .limit(size + 1)
                        .fetch();

        boolean hasNext = patients.size() > size;
        if (hasNext) {
            patients = patients.subList(0, size);
        }

        return new SliceImpl<>(patients, Pageable.unpaged(), hasNext);
    }

    private static BooleanExpression isEqTo(QPatient patient, Nurse nurse) {
        return nurse != null ? patient.nurse.eq(nurse) : patient.nurse.isNull();
    }

    private static BooleanExpression isGreaterThan(QPatient patient, Long lastIdx) {
        return patient.id.gt(lastIdx);
    }
}
