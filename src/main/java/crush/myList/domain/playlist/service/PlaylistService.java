package crush.myList.domain.playlist.service;

import crush.myList.domain.image.dto.ImageDto;
import crush.myList.domain.image.entity.Image;
import crush.myList.domain.image.repository.ImageRepository;
import crush.myList.domain.image.service.ImageService;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.music.Repository.MusicRepository;
import crush.myList.domain.playlist.dto.PlaylistDto;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
    private final ImageRepository imageRepository;
    private final MusicRepository musicRepository;

    private final ImageService imageService;

    public List<PlaylistDto.Result> getPlaylists(String username) {
        Optional<Member> member = memberRepository.findByUsername(username);

        if (member.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.");
        }

        List<Playlist> playlistEntities = playlistRepository.findAllByMember(member.get());
        return convertToDtoList(playlistEntities);
    }

    public PlaylistDto.Result addPlaylist(String username, PlaylistDto.PostRequest request, MultipartFile titleImage) {
        Optional<Member> member = memberRepository.findByUsername(username);

        if (member.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.");
        }

        ImageDto imageDto = imageService.saveImageToGcs(titleImage);
        Optional<Image> image = imageRepository.findById(imageDto.getId());

        if (image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "이미지 저장에 실패했습니다.");
        }

        Playlist playlist = Playlist.builder()
                .name(request.getPlaylistName())
                .member(member.get())
                .image(image.get())
                .build();

        playlistRepository.save(playlist);
        return PlaylistDto.Result.builder()
                .playlistName(playlist.getName())
                .numberOfMusics(0L)
                .thumbnailUrl(playlist.getImage().getUrl())
                .build();
    }

    /* Convert Playlist Entity List to Playlist Dto List */
    private List<PlaylistDto.Result> convertToDtoList(List<Playlist> playlistEntities) {
        return playlistEntities.stream()
                .map(m -> PlaylistDto.Result.builder()
                        .id(m.getId())
                        .playlistName(m.getName())
                        .thumbnailUrl(m.getImage().getUrl())
                        // counts musics in playlist 'm'
                        .numberOfMusics((long) musicRepository.findAllByPlaylist(m).size())
                        .build())
                .toList();
    }
}
