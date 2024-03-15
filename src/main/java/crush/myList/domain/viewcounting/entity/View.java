package crush.myList.domain.viewcounting.entity;

import crush.myList.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "view")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class View extends BaseEntity {
    @Column(name = "totalViews", nullable = false)
    private int totalViews = 0;
    @Column(name = "todayViews", nullable = false)
    private int todayViews = 0;
}
