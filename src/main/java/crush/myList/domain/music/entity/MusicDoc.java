package crush.myList.domain.music.entity;

import crush.myList.global.entity.BaseDoc;
import crush.myList.global.entity.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "music")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicDoc extends BaseDoc {
    private String title;
}
