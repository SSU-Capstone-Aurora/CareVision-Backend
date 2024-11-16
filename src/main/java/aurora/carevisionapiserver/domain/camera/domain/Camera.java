package aurora.carevisionapiserver.domain.camera.domain;

import jakarta.persistence.*;

import aurora.carevisionapiserver.domain.patient.domain.Patient;
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
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Builder
    public Camera(String id, Patient patient) {
        this.id = id;
        this.patient = patient;
    }

    public void registerPatient(Patient patient) {
        this.patient = patient;
    }
}
