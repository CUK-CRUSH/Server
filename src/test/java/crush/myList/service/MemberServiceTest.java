package crush.myList.service;

import crush.myList.domain.image.entity.Image;
import crush.myList.domain.image.service.ImageService;
import crush.myList.domain.member.dto.EditProfileReq;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.entity.Role;
import crush.myList.domain.member.enums.RoleName;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.member.repository.RoleRepository;
import crush.myList.domain.member.service.MemberService;
import crush.myList.domain.member.service.UsernameService;
import crush.myList.utils.TestEntityMaker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static java.lang.invoke.MethodHandles.throwException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.mockito.BDDMockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 테스트")
public class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;
    @Mock
    private UsernameService usernameService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ImageService imageService;
    private final static TestEntityMaker testEntityMaker = new TestEntityMaker();

    @Test
    @DisplayName("임시 사용자 닉네임 변경")
    void changeTempNameTest() {
        // given
        // 회원가입 되어있는 임시 사용자
        Role tempRole = testEntityMaker.createRole(RoleName.TEMPORARY);
        Role userRole = testEntityMaker.createRole(RoleName.USER);
        Member member = testEntityMaker.createMember(tempRole);
        member.setId(1L);

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(roleRepository.findByName(RoleName.USER)).willReturn(Optional.of(userRole));

        // when
        // 닉네임 변경
        memberService.changeUsername(member.getId(), "변경된 닉네임");

        // then
        // 닉네임이 변경되었는지 확인
        assertThat(member.getUsername()).isEqualTo("변경된 닉네임");
    }

    @Test
    @DisplayName("일반 사용자 닉네임 변경")
    void changeUserNameTest() {
        // given
        // 회원가입 되어있는 일반 사용자
        Member member = testEntityMaker.createDefaultMember();

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        // when
        // 닉네임 변경
        memberService.changeUsername(member.getId(), "변경된 닉네임");

        // then
        // 닉네임이 변경되었는지 확인
        assertThat(member.getUsername()).isEqualTo("변경된 닉네임");
    }

    @Test
    @DisplayName("임시 사용자 닉네임 변경 실패 - 역할을 찾을 수 없음")
    void changeTempNameFailTest2() {
        // given
        // 회원가입 되어있는 임시 사용자
        Role tempRole = testEntityMaker.createRole(RoleName.TEMPORARY);
        Member member = testEntityMaker.createMember(tempRole);
        member.setId(1L);

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(roleRepository.findByName(RoleName.USER)).willReturn(Optional.empty());

        // when
        // 닉네임 변경
        Throwable throwable = catchThrowable(() -> memberService.changeUsername(member.getId(), "변경된 닉네임"));

        // then
        // 임시 사용자의 역할을 찾을 수 없어서 예외가 발생하는지 확인
        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
        assertThat(throwable).hasMessage("500 INTERNAL_SERVER_ERROR \"역할을 찾을 수 없습니다.\"");
    }

    @Test
    @DisplayName("사용자 닉네임 변경 실패 - 존재하지 않는 회원")
    void changeNameFailTest1() {
        // given
        // id가 존재하지 않는 사용자
        Member member = testEntityMaker.createDefaultMember();

        given(memberRepository.findById(member.getId())).willReturn(Optional.empty());

        // when
        // 닉네임 변경
        Throwable throwable = catchThrowable(() -> memberService.changeUsername(member.getId(), "변경된 닉네임"));

        // then
        // 임시 사용자의 역할을 찾을 수 없어서 예외가 발생하는지 확인
        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
        assertThat(throwable).hasMessage("404 NOT_FOUND \"존재하지 않는 회원입니다.\"");
    }

    @Test
    @DisplayName("사용자 닉네임 변경 실패 - 닉네임 유효성 검사 실패")
    void changeNameFailTest2() {
        // given
        // 회원가입 되어있는 일반 사용자
        Member member = testEntityMaker.createDefaultMember();

        willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임은 3~30자의 영문, 숫자, ., _만 사용할 수 있습니다.")).given(usernameService).checkUsername("변경된 닉네임");

        // when
        // 닉네임 변경
        Throwable throwable = catchThrowable(() -> memberService.changeUsername(member.getId(), "변경된 닉네임"));

        // then
        // 닉네임 유효성 검사 실패로 예외가 발생하는지 확인
        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
        assertThat(throwable).hasMessage("400 BAD_REQUEST \"닉네임은 3~30자의 영문, 숫자, ., _만 사용할 수 있습니다.\"");
    }

    @Test
    @DisplayName("사용자 정보 수정 - 이미지 변경")
    void updateImageTest() {
        // given
        // 사용자 정보
        Member member = testEntityMaker.createDefaultMember();
        Image profileImage = testEntityMaker.createImage();
        Image backgroundImage = testEntityMaker.createImage();

        MultipartFile profileImageFile = Mockito.mock(MultipartFile.class);
        MultipartFile backgroundImageFile = Mockito.mock(MultipartFile.class);
        EditProfileReq editProfileReq = EditProfileReq.builder()
                .profileImage(profileImageFile)
                .backgroundImage(backgroundImageFile)
                .deleteProfileImage(true)
                .deleteBackgroundImage(true)
                .build();

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(imageService.saveImageToGcs_Image(profileImageFile)).willReturn(profileImage);
        profileImage.setId(1L);
        given(imageService.saveImageToGcs_Image(backgroundImageFile)).willReturn(backgroundImage);
        backgroundImage.setId(2L);

        // when
        // 사용자 정보 수정
        memberService.updateInfo(editProfileReq, member.getId());

        // then
        // 이미지가 변경되었는지 확인
        assertThat(member.getProfileImage()).isNotNull();
        assertThat(member.getBackgroundImage()).isNotNull();
    }
}
