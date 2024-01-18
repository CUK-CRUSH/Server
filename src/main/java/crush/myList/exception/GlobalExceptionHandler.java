package crush.myList.exception;

import crush.myList.global.dto.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j(topic = "GlobalExceptionHandler")
@RestControllerAdvice
public class GlobalExceptionHandler {
    // null pointer exception
    @ExceptionHandler(NullPointerException.class)
    public ResponseBody handleNullPointerException(NullPointerException e){
        log.error(e.getMessage());
        return ErrorResponse.toResponse(ErrorCode.InternalError);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseBody handleResponseStatusException(ResponseStatusException e){
        log.error(e.getMessage());
        return ErrorResponse.toResponse(e);
    }
}
