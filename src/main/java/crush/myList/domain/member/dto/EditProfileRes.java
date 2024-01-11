package crush.myList.domain.member.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditProfileRes {
    private Long id;
    private String oauth2id;
    private String username;
    private String name;
    private String introduction;
}
