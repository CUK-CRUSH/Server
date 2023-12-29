package crush.myList.config.OAuth.users;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUser implements CustomOAuth2User {
    private String registrationId;
    private String memberId;
    private String oauth2Id;
    private Map<String, Object> attributes;
    private Collection<? extends GrantedAuthority> authorities;
    @Override
    public String getName() {
        return attributes.get("name").toString();
    }
    @Override
    public String getOAuth2Id() {
        return oauth2Id;
    }
}
