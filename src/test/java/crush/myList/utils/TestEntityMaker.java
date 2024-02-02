package crush.myList.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import crush.myList.domain.image.entity.Image;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.entity.Role;
import crush.myList.domain.member.enums.RoleName;
import crush.myList.domain.music.entity.Music;
import crush.myList.domain.playlist.entity.Playlist;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class TestEntityMaker {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 새로운 Role 엔티티 생성 */
    public Role createRole(RoleName roleName) {
        return Role.builder()
                .name(roleName)
                .build();
    }

    /** 새로운 Member 엔티티 생성 */
    public Member createMember(Role role) {
        return Member.builder()
                .oauth2id("테스트 oauth2id")
                .username("테스트 닉네임")
                .name("테스트 이름")
                .introduction("테스트 소개")
                .role(role)
                .build();
    }

    public Member createDefaultMember() {
        return Member.builder()
                .id(1L)
                .oauth2id("테스트 oauth2id")
                .username("테스트 닉네임")
                .name("테스트 이름")
                .introduction("테스트 소개")
                .role(createRole(RoleName.USER))
                .build();
    }

    /** 새로운 Image 엔티티를 생성 */
    public Image createImage() {
        return Image.builder()
                .originalName("테스트 이미지.jpg")
                .uuid("테스트 이미지 uuid")
                .extension("webp")
                .name("테스트 이미지 이름")
                .url("테스트 이미지 url")
                .build();
    }

    /** 새로운 playlist 엔티티를 생성 */
    public Playlist createPlaylist(Member member) {
        return Playlist.builder()
                .member(member)
                .name("테스트 제목")
                .build();
    }

    /** 새로운 music 엔티티를 생성 */
    public Music createMusic(Playlist playlist) {
        return Music.builder()
                .playlist(playlist)
                .title("테스트 제목")
                .artist("테스트 아티스트")
                .url("테스트 url")
                .build();
    }

    /** 객체를 JSON 문자열로 변환 */
    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            System.out.println("toJson error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
