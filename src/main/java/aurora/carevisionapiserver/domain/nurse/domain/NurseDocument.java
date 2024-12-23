package aurora.carevisionapiserver.domain.nurse.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Document(indexName = "nurse")
public class NurseDocument {

    @Id private String id;

    @Field(type = FieldType.Text, analyzer = "my_ngram_analyzer")
    private String name;

    @Field(type = FieldType.Text)
    private String username;
}
