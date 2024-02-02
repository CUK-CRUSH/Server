package crush.myList.domain.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReissueReq {
    @Schema(name = "refresh_token", description = "access token 발급용 refresh token 입니다.")
    private String refreshToken;
}
