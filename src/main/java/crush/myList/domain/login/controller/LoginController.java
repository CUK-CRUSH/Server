package crush.myList.domain.login.controller;

import crush.myList.config.jwt.JwtTokenProvider;
import crush.myList.domain.login.dto.TokenDto;
import crush.myList.domain.login.service.LoginService;
import crush.myList.global.dto.JsonBody;
import crush.myList.global.dto.ResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@ApiResponse(description = "로그인 API. ")
@RestController
@Slf4j(topic = "LoginController")
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    private final LoginService loginService;

    @Operation(summary = "google login token 발급 경로")
//    @ExampleObject(name = "google login token 발급 경로", value = "/login/oauth2/code/google")
    @GetMapping(value = "/oauth2/code/google", produces = "application/json; charset=utf-8")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    public JsonBody<TokenDto> redirect(@RequestParam(value = "state") String state,
                                       @RequestParam(value = "scope", required = false) String scope,
                                       @RequestParam(value = "authuser", required = false) String authuser,
                                       @RequestParam(value = "prompt", required = false) String prompt,
                                       @RequestParam(value = "code") String code) {
        log.info("state: {}, scope: {}, authuser: {}, prompt: {}, code: {}", state, scope, authuser, prompt, code);
        return null;
    }

    @Operation(summary = "로그인 리다이렉트 임시 경로", description = "여기서 받은 code를 통해 토큰을 발급 받는다.")
    @GetMapping("/oauth2/callback")
    public String callback(@RequestParam(value = "code", required = false) String code) {
        return code;
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/token/reissue")
    public JsonBody<?> reissue(HttpServletRequest request) {
        Map<String, String> accessToken = loginService.reissue(request);
        return JsonBody.of(HttpStatus.OK, "토큰 재발급 성공", accessToken);
    }
}
