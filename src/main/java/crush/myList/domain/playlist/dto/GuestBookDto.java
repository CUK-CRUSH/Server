package crush.myList.domain.playlist.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestBookDto {
    private Long id;
    private String username;
    private String content;
    private String modifiedDate;
}
