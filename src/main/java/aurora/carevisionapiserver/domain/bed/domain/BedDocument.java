package aurora.carevisionapiserver.domain.bed.domain;

import jakarta.persistence.Id;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Getter;

@Getter
@Document(indexName = "bed")
public class BedDocument {
    @Id private Long id;

    @Field(type = FieldType.Long)
    private Long bedId;

    @Field(type = FieldType.Long)
    private Long inpatientWardNumber;

    @Field(type = FieldType.Long)
    private Long patientRoomNumber;

    @Field(type = FieldType.Long)
    private Long bedNumber;
}
