package aurora.carevisionapiserver.domain.hospital.domain;

import java.util.List;

import jakarta.persistence.*;

import aurora.carevisionapiserver.domain.admin.domain.Admin;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.global.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "hospital")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hospital extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Department department;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Admin> admins;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Nurse> nurses;
}
