package crush.myList.service;

import crush.myList.domain.image.service.ImageService;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.entity.Role;
import crush.myList.domain.member.enums.RoleName;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.member.service.MemberService;
import crush.myList.utils.TestEntityMaker;
import crush.myList.utils.repository.MemberTestRepository;
import crush.myList.utils.repository.RoleTestRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.mockito.BDDMockito.given;

@Transactional
@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 테스트")
public class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;
    @Mock
    private static MemberRepository memberRepository;
    @Mock
    private ImageService imageService;

    private final static TestEntityMaker testEntityMaker = new TestEntityMaker();

    @Test
    @DisplayName("사용자 닉네임 변경")
    void changeUsernameTest() {
        // given
        RoleTestRepository roleTestRepository = new RoleTestRepository();
        MemberTestRepository memberTestRepository = new MemberTestRepository();
        Role role = roleTestRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("역할을 찾을 수 없습니다."));

        Member member = testEntityMaker.createUserMember(role);
        memberTestRepository.save(member);

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        // when
        // then
    }
}
