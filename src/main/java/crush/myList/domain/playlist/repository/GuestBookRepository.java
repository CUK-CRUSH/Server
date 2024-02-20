package crush.myList.domain.playlist.repository;

import crush.myList.domain.playlist.dto.GuestBookDto;
import crush.myList.domain.playlist.entity.GuestBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestBookRepository extends JpaRepository<GuestBook, Long> {
    List<GuestBook> findAllByPlaylistId(Long playlistId);
}
