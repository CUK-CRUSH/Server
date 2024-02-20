package crush.myList.domain.playlist.service;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.playlist.dto.LikeMember;
import crush.myList.domain.playlist.dto.PlaylistDto;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.entity.PlaylistLike;
import crush.myList.domain.playlist.repository.PlaylistLikeRepository;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j(topic = "LikeService")
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final MemberRepository memberRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistLikeRepository playlistLikeRepository;

    // playlistId로 playlist 좋아요
    public void likePlaylist(SecurityMember member, Long playlistId) {
        Member memberEntity = memberRepository.findByUsername(member.getUsername()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.")
        );

        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.")
        );

        if (playlistLikeRepository.existsByPlaylistIdAndMemberId(playlistId, memberEntity.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 좋아요를 누른 플레이리스트입니다.");
        }

        playlistLikeRepository.save(PlaylistLike.builder()
                .member(memberEntity)
                .playlist(playlist)
                .build()
        );
    }

    // playlistId로 playlist 좋아요 취소
    public void unlikePlaylist(SecurityMember member, Long playlistId) {
        Member memberEntity = memberRepository.findByUsername(member.getUsername()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.")
        );

        PlaylistLike playlistLike = playlistLikeRepository.findByPlaylistIdAndMemberId(playlistId, memberEntity.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "좋아요를 누르지 않은 플레이리스트입니다.")
        );

        playlistLikeRepository.delete(playlistLike);
    }

    // playlistId로 playlist 좋아요 멤버 조회
    public List<LikeMember> getLikeMembers(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.")
        );

        List<Member> members = playlistLikeRepository.findAllByPlaylist(playlist).stream()
                .map(PlaylistLike::getMember)
                .toList();
        return members.stream()
                .map(member -> LikeMember.builder()
                        .id(member.getId())
                        .username(member.getUsername())
                        .introduction(member.getIntroduction())
                        .profileImageUrl(member.getProfileImage() != null ? member.getProfileImage().getUrl() : null)
                        .build()
                )
                .toList();
    }

    // 내가 좋아요한 playlist 조회
    public List<PlaylistDto.Response> getLikedPlaylists(SecurityMember member) {
        Member memberEntity = memberRepository.findByUsername(member.getUsername()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.")
        );

        List<Playlist> likedPlaylists = playlistLikeRepository.findAllByMember(memberEntity).stream()
                .map(PlaylistLike::getPlaylist)
                .toList();

        return likedPlaylists.stream()
                .map(playlist -> PlaylistDto.Response.builder()
                        .id(playlist.getId())
                        .playlistName(playlist.getName())
                        .numberOfMusics(playlist.getMusics() == null ? 0 : playlist.getMusics().size())
                        .thumbnailUrl(playlist.getImage() != null ? playlist.getImage().getUrl() : null)
                        .likeCount(playlistLikeRepository.countByPlaylistId(playlist.getId()))
                        .isLike(true)
                        .build())
                .toList();
    }
}
