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
import java.util.List;
import java.util.Optional;

@Component
@Transactional
@RequiredArgsConstructor
public class DummyData {
    private final MemberRepository memberRepository;
    private final PlaylistRepository playlistRepository;
    private final RoleRepository roleRepository;

    // 역할이 없으면 생성
    public void createRoleIfNotFound(RoleName roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            Role newRole = Role.builder()
                    .name(roleName)
                    .build();
            roleRepository.save(newRole);
        }
    }

    public void setUpRole() {
        RoleName[] roleNames = RoleName.values();
        for (RoleName roleName : roleNames) {
            createRoleIfNotFound(roleName);
        }
    }

    // 더미 사용자가 없으면 저장
    public void createMemberIfNotFound(Member member) {
        Optional<Member> findMember = memberRepository.findByOauth2id(member.getOauth2id());
        if (findMember.isEmpty()) {
            memberRepository.save(member);
        }
    }
    public void setupMember() {
        Role role = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("역할을 찾을 수 없습니다."));
        createMemberIfNotFound(Member.builder()
                .oauth2id("google:104480974923648794533")
                .username("kwangstar")
                .name("장광진")
                .role(role)
                .build());
        createMemberIfNotFound(Member.builder()
                .oauth2id("google:105094405557103557935")
                .username("yunstar")
                .name("이윤성")
                .role(role)
                .build());
    }

    // 더미 플레이리스트가 없으면 저장
    public void createPlaylistIfNotFound(Playlist playlist) {
        Long cnt = playlistRepository.countByMember(playlist.getMember());
        if (cnt < 3) {
            playlistRepository.save(playlist);
        }
    }

    public void setupPlaylist() {
        Member kwangstar = memberRepository.findByUsername("kwangstar").get();
        Member yunseong = memberRepository.findByUsername("yunstar").get();

        for (int i=1; i<=3; i++) {
            // kwangstar 플레이리스트
            createPlaylistIfNotFound(Playlist.builder()
                    .member(kwangstar)
                    .name("kwangstar playlist " + i)
                    .build());
            // yunstar 플레이리스트
            createPlaylistIfNotFound(Playlist.builder()
                    .member(yunseong)
                    .name("yunstar playlist " + i)
                    .build());
        }
    }

    @PostConstruct
    public void setupDummyData() {
        try {
            setUpRole();
            setupMember();
            setupPlaylist();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("더미 데이터 초기화 실패.");
        }
    }
}
