package crush.myList.domain.music.Repository;

import crush.myList.domain.music.entity.Music;
import crush.myList.domain.playlist.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {
    List<Music> findAllByPlaylist(Playlist playlist);
    Page<Music> findAllByPlaylist(Playlist playlist, Pageable pageable);
    void deleteAllByPlaylist(Playlist playlist);

    public Integer countByPlaylist(Playlist playlist);
}
