package aurora.carevisionapiserver.domain.camera.domain;

import jakarta.persistence.*;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.global.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "camera")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Camera extends BaseEntity {
    @Id
    @Column(name = "camera_id")
    private String id;

    private String ip;
    private String password;

    @OneToOne(mappedBy = "camera", cascade = CascadeType.ALL, orphanRemoval = true)
    private Bed bed;

    @Builder
    public Camera(String id, String ip, String password, Bed bed) {
        this.id = id;
        this.ip = ip;
        this.password = password;
        this.bed = bed;
    }
}
