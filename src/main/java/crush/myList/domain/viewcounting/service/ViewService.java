package crush.myList.domain.viewcounting.service;

import crush.myList.domain.viewcounting.entity.View;
import crush.myList.domain.viewcounting.entity.ViewEntity;
import crush.myList.domain.viewcounting.repository.ViewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "ViewService")
@Transactional
@RequiredArgsConstructor
@EnableScheduling
public class ViewService {
    private final ViewRepository viewRepository;

    /**
     * 조회수 증가, View 엔티티 없을 시 생성
     * @param viewEntity 조회수 증가할 플레이리스트
    */
    public void increaseViewCount(ViewEntity viewEntity) {
        // View 엔티티가 없는 경우
        if (viewEntity.getView() == null) {
            viewEntity.setView(new View());
        }
        View view = viewEntity.getView();

        view.setTotalViews(view.getTotalViews() + 1);
        view.setTodayViews(view.getTodayViews() + 1);

        viewRepository.save(view);
    }

    /**
     * 서버시간 0시에 오늘의 조회수 초기화
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetTodayViews() {
        viewRepository.resetTodayViews();
    }
}
