package crush.myList.domain.playlist.repository;

import crush.myList.domain.playlist.entity.GuestBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestBookRepository extends JpaRepository<GuestBook, Long> {
    Page<GuestBook> findAllByPlaylistId(Long playlistId, Pageable pageable);
}
