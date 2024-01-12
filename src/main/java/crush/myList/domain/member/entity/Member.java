package crush.myList.domain.member.entity;

import crush.myList.domain.image.entity.Image;
import crush.myList.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "oauth2id", nullable = false, unique = true, length = 50)
    private String oauth2id;

    @Column(name = "username", unique = true, length = 50)
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
}
