package crush.myList.domain.playlist.service;

import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.playlist.dto.PlaylistDto;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j(topic = "PlaylistService")
@Transactional
@RequiredArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final MemberRepository memberRepository;

    public List<PlaylistDto.Res> getPlaylists(String username) {
        Optional<Member> member = memberRepository.findByUsername(username);

        if (!member.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.");
        }

        List<Playlist> playlistEntities = playlistRepository.findAllByMember(member.get());
        List<PlaylistDto.Res> playlistResDtos = convertToDtoList(playlistEntities);
        return playlistResDtos;
    }

    public PlaylistDto.Res addPlaylist(String username, PlaylistDto.Req request) {
        Optional<Member> member = memberRepository.findByUsername(username);

        if (!member.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.");
        }

        System.out.println(request.getPlaylistName());

        Playlist playlist = Playlist.builder()
                .name(request.getPlaylistName())
                .member(member.get())
                .build();

        playlistRepository.save(playlist);
        return PlaylistDto.Res.builder()
                .playlistName(playlist.getName())
                .numberOfMusics(0L)
                .thumbnailUrl("")
                .build();
    }

    /* Convert Playlist Entity List to Playlist Dto List */
    private List<PlaylistDto.Res> convertToDtoList(List<Playlist> playlistEntities) {
        List<PlaylistDto.Res> playlistResDtos = playlistEntities.stream()
                .map(m -> PlaylistDto.Res.builder()
                        .playlistName(m.getName())
                        .thumbnailUrl("")   // 미구현
                        .numberOfMusics(0L)  // 미구현
                        .build())
                .toList();
        return playlistResDtos;
    }
}
