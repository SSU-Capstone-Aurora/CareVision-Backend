package aurora.carevisionapiserver.domain.patient.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Document(indexName = "patient")
public class PatientDocument {

    @Id private String id;

    @Field(type = FieldType.Text, analyzer = "my_ngram_analyzer")
    private String name;

    @Field(type = FieldType.Long)
    private Long patientId;

    @Field(type = FieldType.Text)
    private String code;

    @Field(type = FieldType.Long)
    private Long bedId;
}
