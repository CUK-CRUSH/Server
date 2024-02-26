package crush.myList.domain.music.entity;

import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "music")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MusicEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @Column(name = "title", nullable = false, columnDefinition = "varchar(30)")
    private String title;

    @Column(name = "artist", nullable = false, columnDefinition = "varchar(30)")
    private String artist;

    @Column(name = "url", nullable = false, columnDefinition = "varchar(255)")
    private String url;
}
