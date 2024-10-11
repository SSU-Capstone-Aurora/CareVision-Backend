package aurora.carevisionapiserver.domain.nurse.domain;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
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
@SQLDelete(sql = "UPDATE nurse SET deleted_at = NOW() WHERE id = ?")
public class Nurse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nurse_id")
    private Long id;

    private String name;

    private String username;

    private String password;

    private LocalDateTime registeredAt;

    private boolean isActivated;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "nurse", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Patient> patients;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    @Builder
    public Nurse(
            Long id, String name, String username, LocalDateTime registeredAt, Hospital hospital) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.registeredAt = registeredAt;
        this.hospital = hospital;
    }
}
