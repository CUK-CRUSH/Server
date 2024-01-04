package crush.myList.domain.playlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Repository, Long> {
    List<Repository> findAllByMember_Id(Long memberId);
}
