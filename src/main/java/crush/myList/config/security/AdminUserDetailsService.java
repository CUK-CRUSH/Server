package crush.myList.config.security;


import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public AdminMember loadUserByUsername(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        return AdminMember.builder()
                .username(member.getUsername())
                .password(member.getIntroduction())
                .role(member.getRole().getName())
                .build();
    }
}
