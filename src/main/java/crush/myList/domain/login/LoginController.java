package crush.myList.domain.login;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    @RequestMapping("/oauth2/success")
    public String redirect(@RequestParam(value = "token", required = false) String token) {
        return token;
    }

    @RequestMapping("/oauth2/callback")
    public String callback(@RequestParam(value = "code", required = false) String code) {
        return code;
    }
}
