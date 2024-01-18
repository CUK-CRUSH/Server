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
    private String registrationId;
    private String memberId;
    private String oauth2Id;
    private Map<String, Object> attributes;
    private Collection<? extends GrantedAuthority> authorities;
    @Override
    public String getName() {
        return attributes.get("id").toString();
    }
    @Override
    public String getOAuth2Id() {
        return oauth2Id;
    }
}
