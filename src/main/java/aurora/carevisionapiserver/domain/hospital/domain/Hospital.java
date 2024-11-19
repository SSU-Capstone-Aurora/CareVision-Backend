package aurora.carevisionapiserver.domain.hospital.domain;

import java.util.List;

import jakarta.persistence.*;

import aurora.carevisionapiserver.global.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
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

    private String ykiho;

    private String name;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Department> departments;

    @Builder
    public Hospital(Long id, String name, String ykiho) {
        this.id = id;
        this.name = name;
        this.ykiho = ykiho;
    }
}
