package aurora.carevisionapiserver.domain.nurse.domain;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.auth.domain.Role;
import aurora.carevisionapiserver.global.auth.domain.User;
import aurora.carevisionapiserver.global.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "nurse")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE nurse SET deleted_at = NOW() WHERE nurse_id = ?")
public class Nurse extends BaseEntity implements User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nurse_id")
    private Long id;

    private String name;

    private String username;

    private String password;

    private LocalDateTime registeredAt;

    private LocalDateTime requestedAt;

    private boolean isActivated;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "nurse", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Patient> patients;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Builder
    public Nurse(
            Long id,
            String name,
            String username,
            String password,
            LocalDateTime registeredAt,
            LocalDateTime requestedAt,
            boolean isActivated,
            Role role,
            Department department,
            List<Patient> patients) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.registeredAt = registeredAt;
        this.requestedAt = requestedAt;
        this.isActivated = isActivated;
        this.role = role;
        this.department = department;
        this.patients = patients;
    }

    public void activateNurse() {
        this.isActivated = true;
    }

    public void updateRequestedAt() {
        this.requestedAt = LocalDateTime.now();
    }
}
