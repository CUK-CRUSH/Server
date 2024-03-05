package crush.myList.domain.ranking.repository;

import crush.myList.domain.ranking.entity.MemberRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRankingRepository extends JpaRepository<MemberRanking, Long> {
    List<MemberRanking> findAllBy();
}
