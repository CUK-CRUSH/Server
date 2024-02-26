package crush.myList.domain.music.repository;

import crush.myList.domain.music.entity.MusicEntity;
import crush.myList.domain.playlist.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicJpaRepository extends JpaRepository<MusicEntity, Long> {
    List<MusicEntity> findAllByPlaylist(Playlist playlist);
    Page<MusicEntity> findAllByPlaylist(Playlist playlist, Pageable pageable);
    void deleteAllByPlaylist(Playlist playlist);

    public Integer countByPlaylist(Playlist playlist);
}
