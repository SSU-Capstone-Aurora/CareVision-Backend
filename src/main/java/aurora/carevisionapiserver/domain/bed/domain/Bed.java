package aurora.carevisionapiserver.domain.bed.domain;

import jakarta.persistence.*;

import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.hospital.domain.Department;
import aurora.carevisionapiserver.domain.patient.domain.Patient;
import aurora.carevisionapiserver.global.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "bed")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bed_id")
    private Long id;

    private Long inpatientWardNumber;
    private Long patientRoomNumber;
    private Long bedNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camera_id")
    private Camera camera;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Builder
    public Bed(
            Long id,
            Long inpatientWardNumber,
            Long patientRoomNumber,
            Long bedNumber,
            Patient patient,
            Camera camera,
            Department department) {
        this.id = id;
        this.inpatientWardNumber = inpatientWardNumber;
        this.patientRoomNumber = patientRoomNumber;
        this.bedNumber = bedNumber;
        this.patient = patient;
        this.camera = camera;
        this.department = department;
    }

    public void registerPatient(Patient patient) {
        this.patient = patient;
    }

    public void registerCamera(Camera camera) {
        this.camera = camera;
    }
}
