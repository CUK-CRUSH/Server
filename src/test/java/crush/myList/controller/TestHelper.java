package crush.myList.controller;

import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.entity.Role;
import crush.myList.domain.member.enums.RoleName;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.member.repository.RoleRepository;
import crush.myList.domain.music.Entity.Music;
import crush.myList.domain.music.Repository.MusicRepository;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

@SpringBootTest
@Transactional
public class TestHelper {
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected PlaylistRepository playlistRepository;
    @Autowired
    protected MusicRepository musicRepository;

    /**
     * 테스트용 멤버 생성
     * @return Member
     */
    protected Member createTestMember() {
        Role role = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다."));
        Member member = Member.builder()
                .oauth2id("test:1")
                .username("test")
                .name("test1")
                .role(role)
                .build();
        memberRepository.save(member);

        return member;
    }

    /**
     * 테스트용 플레이리스트 생성
     * @return Playlist
     */
    protected Playlist createTestPlaylist(Member member) {
        Playlist playlist = Playlist.builder()
                .name("testPlaylistName")
                .member(member)
                .build();
        playlistRepository.save(playlist);

        return playlist;
    }

    /**
     * 테스트용 음악 생성
     * @return Music
     */
    protected Music createTestMusic(Playlist playlist) {
        Music music = Music.builder()
                .title("굿굿")
                .artist("후후")
                .url("https://youtube.com/watch?v=test")
                .playlist(playlist)
                .build();
        musicRepository.save(music);

        return music;
    }

    /**
     * 테스트용 이미지 생성
     * @return MockMultipartFile
     */
    protected MockMultipartFile createTestImage(String imageName) throws IOException {
        URL resource = getClass().getResource("/img/test.png");
        FileInputStream file = new FileInputStream(resource.getFile());

        return new MockMultipartFile(
                imageName, //name
                "testImages.jpg", //originalFilename
                "image/jpeg",
                file
        );
    }
}
