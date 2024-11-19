package aurora.carevisionapiserver.domain.patient.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

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
    public List<Patient> findPatientByAdmin(Admin admin) {
        QPatient patient = QPatient.patient;

        return queryFactory
                .selectFrom(patient)
                .where(
                        patient.department.hospital.name.eq(
                                admin.getDepartment().getHospital().getName()))
                .fetch();
    }

    @Override
    public List<Patient> findUnlinkedPatientsByNurse(Nurse nurse) {
        QPatient patient = QPatient.patient;

        return queryFactory
                .selectFrom(patient)
                .where(
                        patient.department
                                .hospital
                                .eq(nurse.getDepartment().getHospital())
                                .and(patient.nurse.isNull()))
                .fetch();
    }
}
