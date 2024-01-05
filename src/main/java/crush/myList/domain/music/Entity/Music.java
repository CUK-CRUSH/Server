package crush.myList.domain.music.Entity;

import crush.myList.domain.member.entity.Member;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "music")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Music extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

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
