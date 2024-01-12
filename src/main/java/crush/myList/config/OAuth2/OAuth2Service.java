package crush.myList.config.OAuth2;

import crush.myList.config.OAuth2.users.FaceBookUser;
import crush.myList.config.OAuth2.users.GoogleUser;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.UUID;

@Slf4j(topic = "OAuth2UserService")
@RequiredArgsConstructor
@Transactional
@Service
public class OAuth2Service extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

//        oAuth2User.getAttributes().forEach((k, v) -> {
//            log.info("key: {}, value: {}", k, v);
//        });
//
//        log.info("registrationId: {}", registrationId);
//        log.info("name: {}", oAuth2User.getName());

        return saveOrUpdate(oAuth2User, registrationId);
    }

    private OAuth2User saveOrUpdate(OAuth2User oAuth2User, String registrationId) {
        switch (registrationId) {
            case "google":
                return saveOrUpdateGoogleUser(oAuth2User);
            case "facebook":
                return saveOrUpdateFacebookUser(oAuth2User);
            default:
                Assert.isTrue(false, "지원하지 않는 소셜 로그인 입니다.");
                return null;
        }
    }

    private OAuth2User saveOrUpdateGoogleUser(OAuth2User oAuth2User) {
        String oauth2Id = oAuth2User.getName();

        Member member = memberRepository.findByOauth2id(oauth2Id).orElse(null);
        if (member == null) {
            member = Member.builder()
                    .name(oAuth2User.getAttribute("name"))
                    .oauth2id(oauth2Id)
                    .build();
            memberRepository.save(member);
        }

        return GoogleUser.builder()
                .registrationId("google")
                .memberId(String.valueOf(member.getId()))
                .oauth2Id(oauth2Id)
                .attributes(oAuth2User.getAttributes())
                .authorities(oAuth2User.getAuthorities())
                .build();
    }

    private OAuth2User saveOrUpdateFacebookUser(OAuth2User oAuth2User) {
        String oauth2Id = oAuth2User.getName();

        Member member = memberRepository.findByOauth2id(oauth2Id).orElse(null);
        if (member == null) {
            member = Member.builder()
                    .name(oAuth2User.getAttribute("name"))
                    .oauth2id(oauth2Id)
                    .build();
            memberRepository.save(member);
        }

        return FaceBookUser.builder()
                .registrationId("facebook")
                .memberId(String.valueOf(member.getId()))
                .oauth2Id(oauth2Id)
                .attributes(oAuth2User.getAttributes())
                .authorities(oAuth2User.getAuthorities())
                .build();
    }
}
