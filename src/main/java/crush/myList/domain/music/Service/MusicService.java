package crush.myList.domain.music.Service;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.music.Dto.MusicDto;
import crush.myList.domain.music.Entity.Music;
import crush.myList.domain.music.Repository.MusicRepository;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import crush.myList.global.enums.LimitConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public String musicUrlFilter(String url) {
        if (url == null || url.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL을 입력해주세요.");
        }
        // youtube url 형식인지 정규 표현식으로 확인
        if (!url.matches("^https://(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)[a-zA-Z0-9_-]{11}([?&].*)?$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 URL입니다.");
        }
        return url;
    }

    public List<MusicDto.Result> getMusics(Long playlistId, int page) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.")
        );
        Pageable pageable = PageRequest.of(page, LimitConstants.PLAYLIST_PAGE_SIZE.getLimit(), Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<Music> musics = musicRepository.findAllByPlaylist(playlist, pageable);
        return convertToDtoList(musics);
    }

    public MusicDto.Result addMusic(SecurityMember memberDetails, Long playlistId, MusicDto.PostRequest postRequest) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.")
        );

        if (!Objects.equals(playlist.getMember().getUsername(), memberDetails.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "플레이리스트에 접근할 수 없습니다.");
        }

        if (musicRepository.countByPlaylist(playlist) >= LimitConstants.MUSIC_LIMIT.getLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("플레이리스트에는 %d개의 음악만 추가할 수 있습니다.", LimitConstants.MUSIC_LIMIT.getLimit()));
        }

        Music music = Music.builder()
                .title(postRequest.getTitle())
                .artist(postRequest.getArtist())
                .url(musicUrlFilter(postRequest.getUrl()))
                .playlist(playlist)
                .build();
        musicRepository.save(music);

        return convertToDto(music);
    }

    public MusicDto.Result updateMusic(SecurityMember memberDetails, Long musicId, MusicDto.PatchRequest patchRequest) {
        Music music = musicRepository.findById(musicId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "음악을 찾을 수 없습니다.")
        );

        if (!Objects.equals(music.getPlaylist().getMember().getUsername(), memberDetails.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "플레이리스트에 접근할 수 없습니다.");
        }

        String title = patchRequest.getTitle();
        if (title != null && !title.isBlank()) {
            music.setTitle(title);
        }

        String artist = patchRequest.getArtist();
        if (artist != null && !artist.isBlank()) {
            music.setArtist(artist);
        }

        String url = musicUrlFilter(patchRequest.getUrl());
        if (url != null && !url.isBlank()) {
            music.setUrl(url);
        }

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

    private List<MusicDto.Result> convertToDtoList(Page<Music> musicEntities) {
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
