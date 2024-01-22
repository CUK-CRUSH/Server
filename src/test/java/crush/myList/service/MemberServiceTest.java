package crush.myList.service;

import crush.myList.domain.image.service.ImageService;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 테스트")
public class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ImageService imageService;

    @Test
    @DisplayName("닉네임 중복 확인 테스트")
    void checkUsernameTest() {
        // given

        // when

        // then
    }
}
