package crush.myList.domain.festival.mongo.document;

import crush.myList.global.entity.BaseDocument;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "form_data")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Form extends BaseDocument {
    @NotBlank
    private Integer age;
    @NotBlank
    private String sex;
    @NotBlank
    private String phone;
    @NotBlank
    private String name;
    @NotBlank
    @Indexed(unique = true)
    private String link;
    @NotBlank
    private String genre;
}
