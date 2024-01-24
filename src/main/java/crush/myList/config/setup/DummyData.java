package crush.myList.config.setup;

import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
@Transactional
@RequiredArgsConstructor
public class DummyData {
    private final MemberRepository memberRepository;
    private final PlaylistRepository playlistRepository;

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
        saveOrUpdateMember(Member.builder()
                .oauth2id("test:111111111111")
                .username("kwangstar")
                .name("kwangstar")
                .build());
        saveOrUpdateMember(Member.builder()
                .oauth2id("test:222222222222")
                .username("yunseong")
                .name("yunseong")
                .build());
        saveOrUpdateMember(Member.builder()
                .oauth2id("test:333333333333")
                .username("donghyun")
                .name("donghyun")
                .build());
        saveOrUpdateMember(Member.builder()
                .oauth2id("test:444444444444")
                .username("tester")
                .name("tester")
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
