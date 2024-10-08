package aurora.carevisionapiserver.domain.bed.domain;

import jakarta.persistence.*;

import aurora.carevisionapiserver.global.common.domain.BaseEntity;
import lombok.AccessLevel;
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
}
