package aurora.carevisionapiserver.domain.nurse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.domain.nurse.domain.QNurse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomNurseRepositoryImpl implements CustomNurseRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Nurse> findActiveNursesByAdmin(Admin admin) {
        QNurse nurse = QNurse.nurse;
        return queryFactory
                .selectFrom(nurse)
                .where(
                        nurse.hospital
                                .name
                                .eq(admin.getHospital().getName())
                                .and(nurse.isActivated.isTrue()))
                .orderBy(nurse.registeredAt.desc())
                .fetch();
    }

    @Override
    public List<Nurse> findInActiveNursesByAdmin(Admin admin) {
        QNurse nurse = QNurse.nurse;
        return queryFactory
                .selectFrom(nurse)
                .where(
                        nurse.hospital
                                .name
                                .eq(admin.getHospital().getName())
                                .and(nurse.isActivated.isFalse()))
                .orderBy(nurse.registeredAt.desc())
                .fetch();
    }

    @Override
    public Optional<Nurse> findActiveNurseById(Long nurseId) {
        QNurse nurse = QNurse.nurse;
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(nurse)
                        .where(nurse.id.eq(nurseId), nurse.isActivated.isTrue())
                        .fetchOne());
    }

    @Override
    public Optional<Nurse> findInActiveNurseById(Long nurseId) {
        QNurse nurse = QNurse.nurse;
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(nurse)
                        .where(
                                nurse.id.eq(nurseId),
                                nurse.isActivated.isFalse())
                        .fetchOne());
    }
}
