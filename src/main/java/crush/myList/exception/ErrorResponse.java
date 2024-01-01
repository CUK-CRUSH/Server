package crush.myList.exception;

import crush.myList.global.dto.ResponseBody;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse implements ResponseBody {
    private HttpStatus status;
    private String message;

    public static ErrorResponse toResponse(ErrorCode errorCode){
        return ErrorResponse.builder()
                        .status(errorCode.getStatus())
                        .message(errorCode.getMessage())
                        .build();
    }
}
