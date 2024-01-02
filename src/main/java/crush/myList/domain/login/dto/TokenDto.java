package crush.myList.domain.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    @Schema(name = "access_token", description = "access token 입니다. 유효시간 10분")
    private String accessToken;
    @Schema(name = "refresh_token", description = "refresh token 입니다. 유효시간 1일")
    private String refreshToken;
}
