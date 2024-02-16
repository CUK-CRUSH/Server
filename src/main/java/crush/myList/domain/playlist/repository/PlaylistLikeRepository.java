package crush.myList.domain.playlist.repository;

import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.entity.PlaylistLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistLikeRepository extends JpaRepository<PlaylistLike, Long> {
    Integer countByPlaylistId(Long playlistId);
    List<PlaylistLike> findAllByMemberId(Long memberId);

    Boolean existsByPlaylistIdAndMemberId(Long playlistId, Long memberId);

    Optional<PlaylistLike> findByPlaylistIdAndMemberId(Long playlistId, Long id);

    List<PlaylistLike> findAllByPlaylist(Playlist playlist);
}
