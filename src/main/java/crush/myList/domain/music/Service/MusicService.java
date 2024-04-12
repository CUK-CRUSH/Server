package crush.myList.domain.music.service;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.music.dto.MusicDto;
import crush.myList.domain.music.mongo.document.Music;
import crush.myList.domain.music.mongo.repository.MusicRepository;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import crush.myList.global.enums.LimitConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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

    // order로 정렬하여 페이징 처리하여 음악 조회
    public List<MusicDto.Result> getMusics(Long playlistId, int page) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.")
        );
        Pageable pageable = PageRequest.of(page, LimitConstants.PLAYLIST_PAGE_SIZE.getLimit(), Sort.by(Sort.Direction.ASC, "order"));

        Page<Music> musics = musicRepository.findAllByPlaylistId(playlist.getId(), pageable);
        return convertToDtoList(musics);
    }

//    public MusicDto.Result addMusic(SecurityMember memberDetails, Long playlistId, MusicDto.PostRequest postRequest) {
//        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() ->
//                new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.")
//        );
//
//        if (!Objects.equals(playlist.getMember().getUsername(), memberDetails.getUsername())) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "플레이리스트에 접근할 수 없습니다.");
//        }
//
//        if (musicRepository.countByPlaylistId(playlist.getId()) >= LimitConstants.MUSIC_LIMIT.getLimit()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("플레이리스트에는 %d개의 음악만 추가할 수 있습니다.", LimitConstants.MUSIC_LIMIT.getLimit()));
//        }
//
//        Music music = convertToEntity(postRequest, playlist);
//        musicRepository.save(music);
//
//        return convertToDto(music);
//    }

    public List<MusicDto.Result> addMusic(SecurityMember memberDetails, Long playlistId, List<MusicDto.PostRequest> postRequests) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.")
        );

        if (!Objects.equals(playlist.getMember().getUsername(), memberDetails.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "플레이리스트에 접근할 수 없습니다.");
        }

        if (musicRepository.countByPlaylistId(playlist.getId()) + postRequests.size() > LimitConstants.MUSIC_LIMIT.getLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "플레이리스트에 추가할 수 있는 음악의 개수를 초과했습니다.");
        }

        List<Music> musics = convertToEntityList(postRequests, playlist);
        musicRepository.saveAll(musics);

        return convertToDtoList(musics);
    }

//    public MusicDto.Result updateMusic(SecurityMember memberDetails, String musicId, MusicDto.PatchRequest patchRequest) {
//        Music music = musicRepository.findById(musicId).orElseThrow(() ->
//                new ResponseStatusException(HttpStatus.NOT_FOUND, "음악을 찾을 수 없습니다.")
//        );
//
//        Playlist playlist = playlistRepository.findById(music.getPlaylistId()).orElseThrow(() ->
//                new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.")
//        );
//
//        if (!Objects.equals(playlist.getMember().getUsername(), memberDetails.getUsername())) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "플레이리스트에 접근할 수 없습니다.");
//        }
//
//        String title = patchRequest.getTitle();
//        if (title != null && !title.isBlank()) {
//            music.setTitle(title);
//        }
//
//        String artist = patchRequest.getArtist();
//        if (artist != null && !artist.isBlank()) {
//            music.setArtist(artist);
//        }
//
//        String url = musicUrlFilter(patchRequest.getUrl());
//        if (url != null && !url.isBlank()) {
//            music.setUrl(url);
//        }
//
//        // updateDate를 업데이트하기 위해 save 메소드 사용
//        musicRepository.save(music);
//
//        return convertToDto(music);
//    }

    // 음악 여려개 한번에 수정하기
    public List<MusicDto.Result> updateMusics(SecurityMember member, Long playlistId, List<MusicDto.PatchRequestV1> patchRequests) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.")
        );

        if (!Objects.equals(playlist.getMember().getUsername(), member.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "플레이리스트에 접근할 수 없습니다.");
        }

        List<Music> musics = new ArrayList<>();

        for (MusicDto.PatchRequestV1 patchRequest : patchRequests) {
            Music music = musicRepository.findById(patchRequest.getMusicId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "음악을 찾을 수 없습니다.")
            );
            musics.add(music);

            if (!Objects.equals(music.getPlaylistId(), playlist.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "플레이리스트에 속한 음악이 아닙니다.");
            }

            if (patchRequest.getMusicOrder() != null) {
                music.setOrder(patchRequest.getMusicOrder());
            }

            if (patchRequest.getTitle() != null && !patchRequest.getTitle().isBlank()) {
                music.setTitle(patchRequest.getTitle());
            }

            if (patchRequest.getArtist() != null && !patchRequest.getArtist().isBlank()) {
                music.setArtist(patchRequest.getArtist());
            }

            if (patchRequest.getUrl() != null && !patchRequest.getUrl().isBlank()) {
                music.setUrl(musicUrlFilter(patchRequest.getUrl()));
            }
        }

        musicRepository.saveAll(musics);

        return convertToDtoList(musics);
    }

    public String deleteMusic(SecurityMember memberDetails, Long playlistId, List<MusicDto.DeleteRequest> deleteRequest) {
        List<Music> musics = new ArrayList<>();
        for (MusicDto.DeleteRequest request : deleteRequest) {
            Music music = musicRepository.findById(request.getMusicId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "음악을 찾을 수 없습니다.")
            );
            if (!music.getPlaylistId().equals(playlistId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "플레이리스트에 속한 음악이 아닙니다.");
            }
            Playlist playlist = playlistRepository.findById(music.getPlaylistId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.")
            );
            if (!Objects.equals(playlist.getMember().getUsername(), memberDetails.getUsername())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "플레이리스트에 접근할 수 없습니다.");
            }
            musics.add(music);
        }
        musicRepository.deleteAll(musics);

        return "음악 삭제 성공";
    }

    private List<Music> convertToEntityList(List<MusicDto.PostRequest> postRequests, Playlist playlist) {
        return postRequests.stream()
                .map(request -> convertToEntity(request, playlist))
                .toList();
    }

    private Music convertToEntity(MusicDto.PostRequest postRequest, Playlist playlist) {
        // youtube url 형식인지 정규 표현식으로 확인
        String url = musicUrlFilter(postRequest.getUrl());
        return Music.builder()
                .order(postRequest.getOrder())
                .title(postRequest.getTitle())
                .artist(postRequest.getArtist())
                .url(url)
                .playlistId(playlist.getId())
                .build();
    }

    private List<MusicDto.Result> convertToDtoList(Page<Music> musicEntities) {
        return musicEntities.stream()
                .map(this::convertToDto)
                .toList();
    }

    private List<MusicDto.Result> convertToDtoList(List<Music> musicEntities) {
        return musicEntities.stream()
                .map(this::convertToDto)
                .toList();
    }

    private MusicDto.Result convertToDto(Music music) {
        return MusicDto.Result.builder()
                .id(music.getId())
                .order(music.getOrder())
                .title(music.getTitle())
                .artist(music.getArtist())
                .url(music.getUrl())
                .build();
    }
}
