package crush.myList.domain.admin.mongo.document;

import crush.myList.domain.member.enums.RoleName;
import crush.myList.global.entity.BaseDocument;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "admin")
public class Admin extends BaseDocument {
    @Indexed(unique = true)
    private String username;
    private String password;
    private String name;
    private RoleName role;
}
