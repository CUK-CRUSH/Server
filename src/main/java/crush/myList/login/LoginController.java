package crush.myList.login;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    @RequestMapping("/oauth2/success")
    public String redirect(@RequestParam("token") String token) {
        return token;
    }
}
