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
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.");
        });

        List<Playlist> playlistEntities = playlistRepository.findAllByMember(member);
        return convertToDtoList(playlistEntities);
    }

    public PlaylistDto.Result addPlaylist(String username, PlaylistDto.PostRequest request, MultipartFile titleImage) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.");
        });

        ImageDto imageDto = imageService.saveImageToGcs(titleImage);
        Image image = imageRepository.findById(imageDto.getId()).orElseThrow(() -> {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "이미지 저장에 실패했습니다.");
        });

        Playlist playlist = Playlist.builder()
                .name(request.getPlaylistName())
                .member(member)
                .image(image)
                .build();
        playlistRepository.save(playlist);

        return convertToDto(playlist);
    }

    public PlaylistDto.Result updatePlaylist(String username, PlaylistDto.PutRequest request, MultipartFile image) {
        Playlist playlist = playlistRepository.findById(request.getId()).orElseThrow(() -> {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.");
        });

        playlist.setName(request.getPlaylistName());

        if (!image.isEmpty()) {
            imageService.deleteImageToGcs(playlist.getImage().getId());
            ImageDto imageDto = imageService.saveImageToGcs(image);
            Image newImage = imageRepository.findById(imageDto.getId()).orElseThrow(() -> {
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "이미지 저장에 실패했습니다.");
            });

            playlist.setImage(newImage);
        }

        return convertToDto(playlist);
    }

    public void deletePlaylist(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() -> {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.");
        });

        imageService.deleteImageToGcs(playlist.getImage().getId());
        playlistRepository.delete(playlist);
    }

    /* Convert Playlist Entity List to Playlist Dto List */
    private List<PlaylistDto.Result> convertToDtoList(List<Playlist> playlistEntities) {
        return playlistEntities.stream()
                .map(this::convertToDto)
                .toList();
    }

    private PlaylistDto.Result convertToDto(Playlist playlist) {
        return PlaylistDto.Result.builder()
                .id(playlist.getId())
                .playlistName(playlist.getName())
                .thumbnailUrl(playlist.getImage().getUrl())
                // counts musics in playlist
                .numberOfMusics((long) musicRepository.findAllByPlaylist(playlist).size())
                .build();
    }
}
