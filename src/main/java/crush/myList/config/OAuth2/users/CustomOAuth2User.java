package crush.myList.config.OAuth2.users;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface CustomOAuth2User extends OAuth2User {
    String getMemberId();
}
