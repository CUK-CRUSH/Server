package crush.myList.exception;

import crush.myList.global.dto.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j(topic = "GlobalExceptionHandler")
@RestControllerAdvice
public class GlobalExceptionHandler {
    // null pointer exception
    @ExceptionHandler(NullPointerException.class)
    public ResponseBody handleNullPointerException(NullPointerException e){
        log.error(e.getMessage());
        return ErrorResponse.toResponse(ErrorCode.InternalError);
    }
}
