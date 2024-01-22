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
    public void setupMember() {
        Member kwangstar = Member.builder()
                .oauth2id("test:111111111111")
                .username("kwangstar")
                .name("kwangstar")
                .build();
        Member yunseong = Member.builder()
                .oauth2id("test:222222222222")
                .username("yunseong")
                .name("yunseong")
                .build();
        Member donghyun = Member.builder()
                .oauth2id("test:333333333333")
                .username("donghyun")
                .name("donghyun")
                .build();

        Member test = Member.builder()
                .oauth2id("test:444444444444")
                .username("test")
                .name("test")
                .build();

        memberRepository.save(kwangstar);
        memberRepository.save(yunseong);
        memberRepository.save(donghyun);
        memberRepository.save(test);
    }

    public void setupPlaylist() {
        Member kwangstar = memberRepository.findByUsername("kwangstar").get();
        Member yunseong = memberRepository.findByUsername("yunseong").get();
        Member donghyun = memberRepository.findByUsername("donghyun").get();
        Member test = memberRepository.findByUsername("test").get();

        // kwangstar
        for (int i=1; i<=10; i++) {
            Playlist playlist = Playlist.builder()
                    .member(kwangstar)
                    .name("kwangstar playlist " + i)
                    .build();
            playlistRepository.save(playlist);
        }
        // yunseong
        for (int i=1; i<=10; i++) {
            Playlist playlist = Playlist.builder()
                    .member(yunseong)
                    .name("yunseong playlist " + i)
                    .build();
            playlistRepository.save(playlist);
        }
        // donghyun
        for (int i=1; i<=10; i++) {
            Playlist playlist = Playlist.builder()
                    .member(donghyun)
                    .name("donghyun playlist " + i)
                    .build();
            playlistRepository.save(playlist);
        }
        // test
        for (int i=1; i<=10; i++) {
            Playlist playlist = Playlist.builder()
                    .member(test)
                    .name("test playlist " + i)
                    .build();
            playlistRepository.save(playlist);
        }
    }

    @PostConstruct
    public void setupDummyData() {
        setupMember();
        setupPlaylist();
    }
}
