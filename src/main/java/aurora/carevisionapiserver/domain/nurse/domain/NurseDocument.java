package aurora.carevisionapiserver.domain.nurse.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import aurora.carevisionapiserver.global.common.domain.Identifiable;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Document(indexName = "nurse")
public class NurseDocument implements Identifiable {

    @Id private String id;

    @Field(type = FieldType.Long)
    private Long nurseId;

    @Field(type = FieldType.Text, analyzer = "my_ngram_analyzer")
    private String name;

    @Field(type = FieldType.Text)
    private String username;

    @Field(type = FieldType.Long)
    private Long departmentId;

    @Field(type = FieldType.Boolean)
    private Boolean isActivated;

    @Field(
            type = FieldType.Date,
            format = {},
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis")
    private LocalDateTime createdAt;

    @Override
    public Long getId() {
        return nurseId;
    }
}
