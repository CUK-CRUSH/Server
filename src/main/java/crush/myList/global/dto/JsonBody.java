package crush.myList.global.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class JsonBody implements ResponseBody {
    @NotNull
    private HttpStatus status;
    @NotBlank
    private String message;
    private Object data;
}
