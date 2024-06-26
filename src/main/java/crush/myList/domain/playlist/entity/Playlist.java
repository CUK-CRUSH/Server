package crush.myList.domain.playlist.entity;

import crush.myList.domain.image.entity.Image;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.ranking.entity.PlaylistRanking;
import crush.myList.domain.viewcounting.entity.View;
import crush.myList.domain.viewcounting.entity.ViewEntity;
import crush.myList.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "playlist")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Playlist extends BaseEntity implements ViewEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "name", nullable = false, columnDefinition = "varchar(30)")
    private String name;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "title_image")
    private Image image;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "view_id")
    private View view;


    @OneToOne(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private PlaylistRanking ranking;
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistLike> likes = new ArrayList<>();
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuestBook> guestBooks = new ArrayList<>();
}
