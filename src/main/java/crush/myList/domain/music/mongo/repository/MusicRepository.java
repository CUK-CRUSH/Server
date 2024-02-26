package crush.myList.domain.music.mongo.repository;

import crush.myList.domain.music.mongo.document.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicRepository extends MongoRepository<Music, String> {
    List<Music> findAllByPlaylistId(Long playlistId);
    Page<Music> findAllByPlaylistId(Long playlistId, Pageable pageable);
    void deleteAllByPlaylistId(Long playlistId);

    Integer countByPlaylistId(Long playlistId);
}
