package aurora.carevisionapiserver.domain.patient.domain;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import aurora.carevisionapiserver.domain.bed.domain.Bed;
import aurora.carevisionapiserver.domain.camera.domain.Camera;
import aurora.carevisionapiserver.domain.camera.domain.Video;
import aurora.carevisionapiserver.domain.nurse.domain.Nurse;
import aurora.carevisionapiserver.global.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "patient")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE patient SET deleted_at = NOW() WHERE patient_id = ?")
public class Patient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long id;

    private String name;

    private String code;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nurse_id")
    private Nurse nurse;

    @OneToOne(mappedBy = "patient", fetch = FetchType.LAZY)
    private Camera camera;

    @OneToOne(mappedBy = "patient", fetch = FetchType.LAZY)
    private Bed bed;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Video> videos;

    @Builder
    public Patient(
            Long id,
            String name,
            String code,
            Nurse nurse,
            Camera camera,
            Bed bed,
            List<Video> videos) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.nurse = nurse;
        this.camera = camera;
        this.bed = bed;
        this.videos = videos;
    }
}
