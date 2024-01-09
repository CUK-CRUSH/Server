package crush.myList.domain.member.entity;

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

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "introduction")
    private String introduction;
}
