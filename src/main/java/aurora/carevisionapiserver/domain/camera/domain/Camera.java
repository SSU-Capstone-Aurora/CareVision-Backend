package aurora.carevisionapiserver.domain.camera.domain;

import jakarta.persistence.*;

import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.common.domain.BaseEntity;
import lombok.AccessLevel;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;
}