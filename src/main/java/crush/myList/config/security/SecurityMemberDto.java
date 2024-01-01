package crush.myList.config.security;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityMemberDto {
    private String id;
    private String oauth2id;
    private String username;
    private String name;
}
