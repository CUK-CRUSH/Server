package crush.myList.domain.member.service;

import crush.myList.domain.image.dto.ImageDto;
import crush.myList.domain.image.entity.Image;
import crush.myList.domain.image.repository.ImageRepository;
import crush.myList.domain.image.service.ImageService;
import crush.myList.domain.member.dto.EditProfileReq;
import crush.myList.domain.member.dto.EditProfileRes;
import crush.myList.domain.member.dto.MemberDto;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j(topic = "MemberService")
public class MemberService {
    private final MemberRepository memberRepository;
    // service
    private final ImageService imageService;

    /** 사용자 닉네임 유효성 검사 */
    public void checkUsername(String username) {
        if (memberRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"이미 존재하는 닉네임입니다.");
        }
        // todo: 닉네임 정규식 검사
    }

    /** 사용자 닉네임 변경 */
    public void changeUsername(Long id, String username) {
        checkUsername(username);

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다."));
        member.setUsername(username);
    }
    /** 사용자 이미지 변경 */
    public void updateImage(EditProfileReq editProfileReq, Member member) throws ResponseStatusException {
        MultipartFile profileImageFile = editProfileReq.getProfileImage();
        MultipartFile backgroundImageFile = editProfileReq.getBackgroundImage();
        try {
            // 이미지가 있으면 기존 이미지 삭제 후 새로 저장
            if (profileImageFile != null && !profileImageFile.isEmpty()) {
                if (member.getProfileImage() != null) {
                    imageService.deleteImageToGcs(member.getProfileImage());
                }
                Image profileImage = imageService.saveImageToGcs_Image(profileImageFile);
                member.setProfileImage(profileImage);
            }
            if (backgroundImageFile != null && !backgroundImageFile.isEmpty()) {
                if (member.getBackgroundImage() != null) {
                    imageService.deleteImageToGcs(member.getBackgroundImage());
                }
                Image backgroundImage = imageService.saveImageToGcs_Image(backgroundImageFile);
                member.setBackgroundImage(backgroundImage);
            }
        } catch (RuntimeException e) {
            log.error("MemberController.updateInfo: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장에 실패했습니다.");
        }
    }

    /** 사용자 정보 수정 */
    public EditProfileRes updateInfo(EditProfileReq editProfileReq, Long memberId) throws ResponseStatusException {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "존재하지 않는 회원입니다."));

        // username 수정
        if (editProfileReq.getUsername() != null) {
            checkUsername(editProfileReq.getUsername());
            findMember.setUsername(editProfileReq.getUsername());
        }
        // introduction 수정
        if (editProfileReq.getIntroduction() != null) {
            findMember.setIntroduction(editProfileReq.getIntroduction());
        }
        // image 수정
        updateImage(editProfileReq, findMember);

        return EditProfileRes.builder()
                .id(findMember.getId())
                .oauth2id(findMember.getOauth2id())
                .username(findMember.getUsername())
                .name(findMember.getName())
                .introduction(findMember.getIntroduction())
                .build();
    }

    /** Member -> MemberDto 변환 */
    public MemberDto convertDto(Member member) {
        Image profileImage = member.getProfileImage();
        Image backgroundImage = member.getBackgroundImage();
        return MemberDto.builder()
                .id(member.getId())
                .oauth2id(member.getOauth2id())
                .username(member.getUsername())
                .name(member.getName())
                .introduction(member.getIntroduction())
                .profileImageUrl(profileImage == null ? null : profileImage.getUrl())
                .backgroundImageUrl(backgroundImage == null ? null : backgroundImage.getUrl())
                .build();
    }

    /** memberId를 기준으로 사용자 조회 */
    public MemberDto getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."));
        return convertDto(member);
    }
}
