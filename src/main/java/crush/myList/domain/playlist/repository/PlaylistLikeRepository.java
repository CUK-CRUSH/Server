package crush.myList.domain.playlist.repository;

import crush.myList.domain.member.entity.Member;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.entity.PlaylistLike;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistLikeRepository extends JpaRepository<PlaylistLike, Long> {
    Integer countByPlaylistId(Long playlistId);
    Integer countByMemberId(Long memberId);
    List<PlaylistLike> findAllByMemberId(Long memberId);

    List<PlaylistLike> findAllByMember(Member member, Pageable pageable);

    Boolean existsByPlaylistIdAndMemberId(Long playlistId, Long memberId);

    Optional<PlaylistLike> findByPlaylistIdAndMemberId(Long playlistId, Long id);

    Page<PlaylistLike> findAllByPlaylist(Playlist playlist, Pageable pageable);
}
