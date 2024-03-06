package crush.myList.exception;

import crush.myList.global.dto.ResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j(topic = "GlobalExceptionHandler")
@RestControllerAdvice
public class GlobalExceptionHandler {
    // null pointer exception
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NullPointerException.class)
    public ResponseBody handleNullPointerException(NullPointerException e){
        log.error(e.getMessage());
        return ErrorResponse.toResponse(ErrorCode.InternalError);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseBody handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        // 데이터 무결성 위반 예외 처리
        log.error(e.getMessage());
        return ErrorResponse.toResponse(ErrorCode.DataIntegrityViolation);
    }
}
