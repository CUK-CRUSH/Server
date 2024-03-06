package crush.myList.domain.festival.mongo.document;

import crush.myList.global.entity.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "form_data")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Form extends BaseDocument {
    private String age;
    private String sex;
    private String phone;
    private String name;
    private String link;
    private String genre;
}
