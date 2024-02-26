package crush.myList.domain.music.mongo.document;

import crush.myList.global.entity.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "music")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Music extends BaseDocument {
    @Field(name = "playlist_id")
    private Long playlistId;
    @Field(name = "title")
    private String title;
    @Field(name = "artist")
    private String artist;
    @Field(name = "url")
    private String url;
}
