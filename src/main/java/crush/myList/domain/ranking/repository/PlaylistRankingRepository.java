package crush.myList.domain.ranking.repository;

import crush.myList.domain.ranking.entity.PlaylistRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRankingRepository extends JpaRepository<PlaylistRanking, Long> {
    List<PlaylistRanking> findAll();
}
