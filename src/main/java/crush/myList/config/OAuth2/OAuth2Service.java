package crush.myList.config.OAuth2;

import crush.myList.config.OAuth2.users.FaceBookUser;
import crush.myList.config.OAuth2.users.GoogleUser;
import crush.myList.config.OAuth2.users.KakaoUser;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.entity.Role;
import crush.myList.domain.member.enums.RoleName;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.member.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j(topic = "OAuth2UserService")
@RequiredArgsConstructor
@Transactional
@Service
public class OAuth2Service extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

//        oAuth2User.getAttributes().forEach((k, v) -> log.info("key: {}, value: {}", k, v));
//
//        log.info("registrationId: {}", registrationId);
//        log.info("name: {}", oAuth2User.getName());

        return saveOrUpdate(oAuth2User, registrationId);
    }

    private Member findOrSaveMember(OAuth2User oAuth2User, String registrationId, String name) {
        String oauth2Id = registrationId + ":" + oAuth2User.getName();
        // 임시 유저로 역할 설정
        Role role = roleRepository.findByName(RoleName.TEMPORARY)
                .orElseThrow(() -> new OAuth2AuthenticationException("존재하지 않는 권한입니다."));
        return memberRepository.findByOauth2id(oauth2Id)
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .oauth2id(oauth2Id)
                        .name(name)
                        .role(role)
                        .build()));
    }

    private OAuth2User saveOrUpdate(OAuth2User oAuth2User, String registrationId) {
        return switch (registrationId) {
            case "google" -> saveOrUpdateGoogleUser(oAuth2User);
            case "facebook" -> saveOrUpdateFacebookUser(oAuth2User);
            case "kakao" -> saveOrUpdateKakaoUser(oAuth2User);
            default -> throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 공급자입니다.");
        };
    }

    private OAuth2User saveOrUpdateGoogleUser(OAuth2User oAuth2User) {
        Member member = findOrSaveMember(oAuth2User, "google", oAuth2User.getAttribute("name"));

        return GoogleUser.builder()
                .registrationId("google")
                .memberId(String.valueOf(member.getId()))
                .oauth2Id(member.getOauth2id())
                .attributes(oAuth2User.getAttributes())
                .authorities(oAuth2User.getAuthorities())
                .build();
    }

    private OAuth2User saveOrUpdateFacebookUser(OAuth2User oAuth2User) {
        Member member = findOrSaveMember(oAuth2User, "facebook", oAuth2User.getAttribute("name"));

        return FaceBookUser.builder()
                .registrationId("facebook")
                .memberId(String.valueOf(member.getId()))
                .oauth2Id(member.getOauth2id())
                .attributes(oAuth2User.getAttributes())
                .authorities(oAuth2User.getAuthorities())
                .build();
    }

    private OAuth2User saveOrUpdateKakaoUser(OAuth2User oAuth2User) {
        Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        Member member = findOrSaveMember(oAuth2User, "kakao", profile.get("nickname").toString());

        return KakaoUser.builder()
                .registrationId("kakao")
                .memberId(String.valueOf(member.getId()))
                .oauth2Id(member.getOauth2id())
                .attributes(oAuth2User.getAttributes())
                .authorities(oAuth2User.getAuthorities())
                .build();
    }
}
