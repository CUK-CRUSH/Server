package crush.myList.domain.playlist.service;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.image.entity.Image;
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
import java.util.Objects;

@Service
@Slf4j(topic = "PlaylistService")
@Transactional
@RequiredArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final MemberRepository memberRepository;
    private final MusicRepository musicRepository;

    private final ImageService imageService;

    public List<PlaylistDto.Response> getPlaylists(String username) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.")
        );

        List<Playlist> playlistEntities = playlistRepository.findAllByMember(member);
        return convertToDtoList(playlistEntities);
    }

    public PlaylistDto.Response addPlaylist(SecurityMember memberDetails, PlaylistDto.PostRequest postRequest) {
        Member member = memberRepository.findByUsername(memberDetails.getUsername()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.")
        );

        Image image = null;
        MultipartFile imageFile = postRequest.getTitleImage();

        if (imageFile != null && !imageFile.isEmpty()) {
            image = imageService.saveImageToGcs_Image(postRequest.getTitleImage());
        }

        Playlist playlist = Playlist.builder()
                .name(postRequest.getPlaylistName())
                .member(member)
                .image(image)
                .build();
        playlistRepository.save(playlist);

        return convertToDto(playlist);
    }

    public PlaylistDto.Response updatePlaylist(SecurityMember memberDetails, Long playlistId, PlaylistDto.PatchRequest patchRequest) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.")
        );

        if (!Objects.equals(playlist.getMember().getUsername(), memberDetails.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "플레이리스트에 접근할 수 없습니다.");
        }

        if (patchRequest.getPlaylistName() != null && !patchRequest.getPlaylistName().isEmpty()) {
            playlist.setName(patchRequest.getPlaylistName());
        }

        MultipartFile imageFile = patchRequest.getTitleImage();

        if (imageFile != null && !imageFile.isEmpty()) {
            if (playlist.getImage() != null) {
                imageService.deleteImageToGcs(playlist.getImage().getId());
            }
            Image newImage = imageService.saveImageToGcs_Image(imageFile);

            playlist.setImage(newImage);
        }

        return convertToDto(playlist);
    }

    public void deletePlaylist(SecurityMember memberDetails, Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.")
        );

        if (!Objects.equals(playlist.getMember().getUsername(), memberDetails.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "플레이리스트에 접근할 수 없습니다.");
        }

        // 이미지, 음악, 플레이리스트 순으로 삭제
        Image image = playlist.getImage();
        if (image != null) {
            imageService.deleteImageToGcs(image.getId());
        }
        musicRepository.deleteAllByPlaylist(playlist);
        playlistRepository.delete(playlist);
    }

    public void deletePlaylistImage(SecurityMember memberDetails, Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.")
        );

        if (!Objects.equals(playlist.getMember().getUsername(), memberDetails.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "플레이리스트에 접근할 수 없습니다.");
        }

        Image image = playlist.getImage();
        if (image != null) {
            imageService.deleteImageToGcs(image.getId());
        }
        playlist.setImage(null);
    }

    /* Convert Playlist Entity List to Playlist Dto List */
    private List<PlaylistDto.Response> convertToDtoList(List<Playlist> playlistEntities) {
        return playlistEntities.stream()
                .map(this::convertToDto)
                .toList();
    }

    private PlaylistDto.Response convertToDto(Playlist playlist) {
        return PlaylistDto.Response.builder()
                .id(playlist.getId())
                .playlistName(playlist.getName())
                .thumbnailUrl(playlist.getImage() != null ? playlist.getImage().getUrl() : null)
                // counts musics in playlist
                .numberOfMusics((long) musicRepository.findAllByPlaylist(playlist).size())
                .build();
    }
}
