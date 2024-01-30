package crush.myList.service;

import crush.myList.domain.member.service.UsernameService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class UsernameServiceTest {
    @Autowired
    private UsernameService usernameService;

    @DisplayName("닉네임 유효 테스트")
    @Test
    public void isValidUsernameTest() {
        // given
        final String goodUsername = "goodUsername_.123";    // 3~30자, 영문, 숫자, ., _
        final String badUsername1 = "badUsername_!@#";  // 특수문자
        final String badUsername2 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";  // 31자
        final String badUsername3 = "this_is_fuckin.badname";  // 비속어

        // when
        Assertions.assertDoesNotThrow(() -> usernameService.checkUsername(goodUsername));

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            usernameService.checkUsername(badUsername1);
        });

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            usernameService.checkUsername(badUsername2);
        });

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            usernameService.checkUsername(badUsername3);
        });
    }
}
