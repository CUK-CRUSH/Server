package crush.myList.etc;

import crush.myList.config.OAuth2.handler.LoginSuccessHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
@DisplayName("LoginSuccessHandler 테스트")
public class LoginSuccessHandlerTest {
    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Test
    @DisplayName("로그인 성공 핸들러 테스트")
    public void createURITest() throws IOException {
        System.out.println("redirect URI: " + loginSuccessHandler.createURI("test", "test"));
    }

}
