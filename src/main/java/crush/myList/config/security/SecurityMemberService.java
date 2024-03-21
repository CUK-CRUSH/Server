package crush.myList.config.security;

import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class SecurityMemberService {
    private final MemberRepository memberRepository;
    public UserDetails loadUserByMemberId(String memberId) throws UsernameNotFoundException {
        Member member = memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        return SecurityMember.builder()
                .id(member.getId())
                .oauth2id(member.getOauth2id())
                .username(member.getUsername())
                .name(member.getName())
                .role(member.getRole().getName())
                .build();
    }
}
