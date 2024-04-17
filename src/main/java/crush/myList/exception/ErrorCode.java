package crush.myList.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    InternalError(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러입니다."),
    BadRequest(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    Unauthorized(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    Forbidden(HttpStatus.FORBIDDEN, "접근할 수 없습니다."),
    NotFound(HttpStatus.NOT_FOUND, "해당 리소스를 찾을 수 없습니다."),

    NullPointer(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러입니다 - NullPointerException"),
    DataIntegrityViolation(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러입니다 - DataIntegrityViolationException"),
    IOAccess(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러입니다 - IOException");

    private final HttpStatus status;
    private final String message;
}
