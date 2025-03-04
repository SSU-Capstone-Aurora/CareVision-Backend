package aurora.carevisionapiserver.domain.nurse.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
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
    public Slice<Nurse> findActiveNursesByAdmin(Admin admin, Long lastIdx, int size) {
        QNurse nurse = QNurse.nurse;

        List<Nurse> nurses =
                queryFactory
                        .select(nurse)
                        .from(nurse)
                        .where(isNurseActivate(nurse).and(isEqualToAdminDepartment(admin, nurse)))
                        .orderBy(nurse.id.asc())
                        .limit(size + 1)
                        .fetch();
        boolean hasNext = nurses.size() > size;
        if (hasNext) {
            nurses = nurses.subList(0, size);
        }

        return new SliceImpl<>(nurses, Pageable.unpaged(), hasNext);
    }

    private static BooleanPath isNurseActivate(QNurse nurse) {
        return nurse.isActivated;
    }

    private static BooleanExpression isEqualToAdminDepartment(Admin admin, QNurse nurse) {
        return nurse.department.hospital.name.eq(admin.getDepartment().getHospital().getName());
    }

    @Override
    public List<Nurse> findInactiveNursesByAdmin(Admin admin) {
        QNurse nurse = QNurse.nurse;
        return queryFactory
                .selectFrom(nurse)
                .where(
                        nurse.department
                                .hospital
                                .name
                                .eq(admin.getDepartment().getHospital().getName())
                                .and(nurse.isActivated.isFalse()))
                .orderBy(nurse.requestedAt.desc())
                .fetch();
    }

    @Override
    public long countInactiveNursesByAdmin(Admin admin) {
        QNurse nurse = QNurse.nurse;
        return Optional.ofNullable(
                        queryFactory
                                .select(nurse.count())
                                .from(nurse)
                                .where(
                                        nurse.department
                                                .hospital
                                                .name
                                                .eq(admin.getDepartment().getHospital().getName())
                                                .and(nurse.isActivated.isFalse()))
                                .fetchOne())
                .orElse(0L);
    }
}
