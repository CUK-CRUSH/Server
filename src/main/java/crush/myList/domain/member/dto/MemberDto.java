package crush.myList.domain.member.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String oauth2id;
    private String username;
    private String name;
    private String introduction;
    private String profileImageUrl;
    private String backgroundImageUrl;
}
