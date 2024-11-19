package aurora.carevisionapiserver.domain.admin.domain;

import jakarta.persistence.*;

import aurora.carevisionapiserver.domain.admin.dto.response.AdminResponse.AdminLoginResponse;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.hospital.domain.Hospital;
import aurora.carevisionapiserver.global.auth.domain.Role;
import aurora.carevisionapiserver.global.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "admin")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Builder
    public Admin(
            Long id,
            String username,
            String password,
            Role role,
            Hospital hospital,
            Department department) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.department = department;
    }

    public static AdminLoginResponse toNurseLoginResponse(String accessToken) {
        return AdminLoginResponse.builder().accessToken(accessToken).build();
    }
}
