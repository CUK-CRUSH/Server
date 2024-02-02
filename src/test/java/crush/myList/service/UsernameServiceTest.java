package crush.myList.service;

import crush.myList.config.EnvBean;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.member.service.UsernameService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsernameService 테스트")
public class UsernameServiceTest {
    @InjectMocks
    private UsernameService usernameService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private EnvBean envBean;

    @DisplayName("닉네임 유효 테스트")
    @Test
    public void isValidUsernameTest() {
        // given
        final String goodUsername = "goodusername_.123";    // 3~30자, 소문자, 숫자, ., _
        final String badUsername1 = "badusername_!@#";  // 특수문자
        final String badUsername2 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";  // 31자
        final String badUsername3 = "this_is_fuckin.badname";  // 비속어
        final String badUsername4 = "Aaaaaa";   // 대문자

        // when
        Assertions.assertDoesNotThrow(() -> usernameService.checkCharacterRules(goodUsername));

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            usernameService.checkCharacterRules(badUsername1);
        });

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            usernameService.checkCharacterRules(badUsername2);
        });

//        Assertions.assertThrows(Exception.class, () -> {
//            usernameService.checkBadWords(badUsername3);
//        });

        Assertions.assertThrows(ResponseStatusException.class, () -> {
            usernameService.checkCharacterRules(badUsername4);
        });
    }
}
