package crush.myList.domain.viewcounting.repository;

import crush.myList.domain.viewcounting.entity.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ViewRepository extends JpaRepository<View, Long> {
    @Modifying
    @Query("update View v set v.todayViews = 0")
    void resetTodayViews();
}
