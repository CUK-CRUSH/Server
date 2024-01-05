package crush.myList.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class JsonBody<T> implements ResponseBody {
    @NotNull
    @Schema(example = "200 OK")
    private HttpStatus status;
    @NotBlank
    @Schema(example = "성공")
    private String message;
    private T data;
}
