package crush.myList.domain.viewcounting.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewDto {
    private int totalViews;
    private int todayViews;
}
