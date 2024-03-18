package crush.myList.domain.music.mongo.document;

import crush.myList.global.entity.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "music")
@CompoundIndex(name = "playlistId_order_idx", def = "{'playlistId' : 1, 'order': 1}", unique = true)
public class Music extends BaseDocument {
    @Field(name = "playlist_id")
    private Long playlistId;
    @Field(name = "order")
    private Integer order;
    @Field(name = "title")
    private String title;
    @Field(name = "artist")
    private String artist;
    @Field(name = "url")
    private String url;
}
