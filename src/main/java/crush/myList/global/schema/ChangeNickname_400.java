package crush.myList.global.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Schema(name = "CheckNickname_400", description = "닉네임 변경 실패 응답")
public class ChangeNickname_400 {
    @NotNull
    @Schema(example = "400")
    private int status;
    @NotBlank
    @Schema(example = "이미 사용중인 닉네임")
    private String message;
    @NotBlank
    @Schema(example = "crush")
    private String data;
}
