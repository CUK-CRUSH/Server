package crush.myList.domain.music.Repository;

import crush.myList.domain.music.Entity.Music;
import crush.myList.domain.playlist.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {
    List<Music> findAllByPlaylist(Playlist playlist);
    void deleteAllByPlaylist(Playlist playlist);
}
