package crush.myList.exception;

import crush.myList.global.dto.ResponseBody;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse implements ResponseBody {
    private int status;
    private String message;

    public static ErrorResponse toResponse(ErrorCode errorCode){
        return ErrorResponse.builder()
                        .status(errorCode.getStatus().value())
                        .message(errorCode.getMessage())
                        .build();
    }

    public static ErrorResponse toResponse(ResponseStatusException e){
        return ErrorResponse.builder()
                        .status(e.getStatusCode().value())
                        .message(e.getReason())
                        .build();
    }
}
