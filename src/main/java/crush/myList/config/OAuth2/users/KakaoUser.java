package crush.myList.config.OAuth2.users;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUser implements CustomOAuth2User {
    private String registrationId;  // kakao
    private String memberId;  // DB에 저장된 id
    private String oauth2Id;  // kakaoId
    private Map<String, Object> attributes;  // kakao 정보
    private Collection<? extends GrantedAuthority> authorities;  // 권한
    @Override
    public String getName() {
        return attributes.get("id").toString();
    }
}
