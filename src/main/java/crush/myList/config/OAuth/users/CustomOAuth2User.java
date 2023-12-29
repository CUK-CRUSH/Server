package crush.myList.config.OAuth.users;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface CustomOAuth2User extends OAuth2User {
    String getOAuth2Id();
}
