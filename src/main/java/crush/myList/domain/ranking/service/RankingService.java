package crush.myList.domain.ranking.service;

import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@EnableScheduling
public interface RankingService<T> {
    void init();
    List<T> getRanking();
    void updateRanking();
}
