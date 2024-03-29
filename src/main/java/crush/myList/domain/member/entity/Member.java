package crush.myList.domain.member.entity;

import crush.myList.domain.image.entity.Image;
import crush.myList.domain.playlist.entity.GuestBook;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.entity.PlaylistLike;
import crush.myList.domain.ranking.entity.MemberRanking;
import crush.myList.domain.viewcounting.entity.View;
import crush.myList.domain.viewcounting.entity.ViewEntity;
import crush.myList.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity implements ViewEntity {
    @Column(name= "oauth2id", nullable = false, unique = true, length = 50)
    private String oauth2id;

    @Column(name = "username", unique = true, length = 30)
    private String username;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "introduction")
    private String introduction;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_image_id")
    private Image profileImage;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "background_image_id")
    private Image backgroundImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Playlist> playlists = new ArrayList<>();
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistLike> likes = new ArrayList<>();
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuestBook> guestBooks = new ArrayList<>();
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private MemberRanking ranking;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "view_id")
    private View view;
}
