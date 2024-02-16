package crush.myList.controller;

import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.entity.Role;
import crush.myList.domain.member.enums.RoleName;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.member.repository.RoleRepository;
import crush.myList.domain.music.entity.Music;
import crush.myList.domain.music.Repository.MusicRepository;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.entity.PlaylistLike;
import crush.myList.domain.playlist.repository.PlaylistLikeRepository;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

@Component
public class TestUtil {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private PlaylistLikeRepository playlistLikeRepository;
    @Autowired
    private MusicRepository musicRepository;

    /**
     * 테스트용 멤버 생성
     * @return Member
     */
    public Member createTestMember(String username) {
        Role role = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다."));
        Member member = Member.builder()
                .oauth2id("test:" + username)
                .username(username)
                .name(username)
                .role(role)
                .build();
        memberRepository.save(member);

        return member;
    }

    /**
     * 테스트용 플레이리스트 생성
     * @return Playlist
     */
    public Playlist createTestPlaylist(Member member) {
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
    public Music createTestMusic(Playlist playlist) {
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
    public MockMultipartFile createTestImage(String imageName) throws IOException {
        URL resource = getClass().getResource("/img/test.png");
        FileInputStream file = new FileInputStream(resource.getFile());

        return new MockMultipartFile(
                imageName, //name
                "testImages.jpg", //originalFilename
                "image/jpeg",
                file
        );
    }

    /**
     * 테스트용 플레이리스트 좋아요 생성
     * @return PlaylistLike
     */
    public PlaylistLike createTestPlaylistLike(Member member, Playlist playlist) {
        PlaylistLike playlistLike = PlaylistLike.builder()
                .member(member)
                .playlist(playlist)
                .build();
        return playlistLikeRepository.save(playlistLike);
    }

    public void deletePlaylist(Long id) {
        if (playlistRepository.existsById(id)) {
            playlistRepository.deleteById(id);
        }
    }
}
