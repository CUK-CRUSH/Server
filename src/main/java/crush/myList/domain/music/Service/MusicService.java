package crush.myList.domain.music.Service;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.music.Dto.MusicDto;
import crush.myList.domain.music.Entity.Music;
import crush.myList.domain.music.Repository.MusicRepository;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j(topic = "MusicService")
@Transactional
@RequiredArgsConstructor
public class MusicService {
    private final PlaylistRepository playlistRepository;
    private final MusicRepository musicRepository;

    public List<MusicDto.Result> getMusics(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.")
        );

        List<Music> musics = musicRepository.findAllByPlaylist(playlist);
        return convertToDtoList(musics);
    }

    public MusicDto.Result addMusic(SecurityMember memberDetails, Long playlistId, MusicDto.Request request) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.")
        );

        if (!Objects.equals(playlist.getMember().getUsername(), memberDetails.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "플레이리스트에 접근할 수 없습니다.");
        }

        Music music = Music.builder()
                .title(request.getTitle())
                .artist(request.getArtist())
                .url(request.getUrl())
                .playlist(playlist)
                .build();
        musicRepository.save(music);

        return convertToDto(music);
    }

    public void deleteMusic(SecurityMember memberDetails, Long musicId) {
        Music music = musicRepository.findById(musicId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "음악을 찾을 수 없습니다.")
        );

        if (!Objects.equals(music.getPlaylist().getMember().getUsername(), memberDetails.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "플레이리스트에 접근할 수 없습니다.");
        }

        musicRepository.delete(music);
    }

    private List<MusicDto.Result> convertToDtoList(List<Music> musicEntities) {
        return musicEntities.stream()
                .map(this::convertToDto)
                .toList();
    }

    private MusicDto.Result convertToDto(Music music) {
        return MusicDto.Result.builder()
                .id(music.getId())
                .title(music.getTitle())
                .artist(music.getArtist())
                .url(music.getUrl())
                .build();
    }
}
