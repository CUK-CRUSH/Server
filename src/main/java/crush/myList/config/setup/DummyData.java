package crush.myList.config.setup;

import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.entity.Role;
import crush.myList.domain.member.enums.RoleName;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.member.repository.RoleRepository;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;

@Component
@Transactional
@RequiredArgsConstructor
public class DummyData {
    private final MemberRepository memberRepository;
    private final PlaylistRepository playlistRepository;
    private final RoleRepository roleRepository;

    // 더미 사용자가 존재하면 업데이트, 없으면 저장
    public void saveOrUpdateMember(Member member) {
        memberRepository.findByUsername(member.getUsername())
                .ifPresentOrElse(
                        m -> {
                            m.setOauth2id(member.getOauth2id());
                            m.setUsername(member.getUsername());
                            m.setIntroduction(member.getIntroduction());
                            m.setName(member.getName());
                            m.setRole(member.getRole());
                        },
                        () -> memberRepository.save(member)
                );
    }
    public void setupMember() {
        Role role = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("역할을 찾을 수 없습니다."));
        saveOrUpdateMember(Member.builder()
                .oauth2id("test:111111111111")
                .username("kwangstar")
                .name("kwangstar")
                .role(role)
                .build());
        saveOrUpdateMember(Member.builder()
                .oauth2id("test:222222222222")
                .username("yunseong")
                .name("yunseong")
                .role(role)
                .build());
        saveOrUpdateMember(Member.builder()
                .oauth2id("test:333333333333")
                .username("donghyun")
                .name("donghyun")
                .role(role)
                .build());
        saveOrUpdateMember(Member.builder()
                .oauth2id("test:444444444444")
                .username("tester")
                .name("tester")
                .role(role)
                .build());
    }

    public void saveOrUpdatePlaylist(Playlist playlist) {
        playlistRepository.findById(playlist.getId())
                .ifPresentOrElse(
                        p -> {
                            p.setName(playlist.getName());
                            p.setMember(playlist.getMember());
                        },
                        () -> playlistRepository.save(playlist)
                );
    }

    public void setupPlaylist() {
        Member kwangstar = memberRepository.findByUsername("kwangstar").get();
        Member yunseong = memberRepository.findByUsername("yunseong").get();
        Member donghyun = memberRepository.findByUsername("donghyun").get();

        // kwangstar
        for (int i=1; i<=10; i++) {
            saveOrUpdatePlaylist(Playlist.builder()
                    .id((long) i)
                    .member(kwangstar)
                    .name("kwangstar playlist " + i)
                    .build());
        }
        // yunseong
        for (int i=11; i<=20; i++) {
            saveOrUpdatePlaylist(Playlist.builder()
                    .id((long) i)
                    .member(yunseong)
                    .name("yunseong playlist " + i)
                    .build());
        }
        // donghyun
        for (int i=21; i<=30; i++) {
            saveOrUpdatePlaylist(Playlist.builder()
                    .id((long) i)
                    .member(donghyun)
                    .name("donghyun playlist " + i)
                    .build());
        }
    }

    @PostConstruct
    public void setupDummyData() {
        try {
            setupMember();
            setupPlaylist();
        } catch (Exception e) {
            System.out.println("더미 데이터 초기화 실패.");
        }
    }
}
