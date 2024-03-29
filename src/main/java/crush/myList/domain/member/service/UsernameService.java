package crush.myList.domain.member.service;

import crush.myList.config.EnvBean;
import crush.myList.domain.member.dto.PerspectiveDtoResponse;
import crush.myList.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@Slf4j(topic = "SafeNameService")
@RequiredArgsConstructor
public class UsernameService {
    private final EnvBean envBean;
    private final MemberRepository memberRepository;

    private static final String USERNAME_PATTERN = "^[a-z0-9._]{3,30}$";
    public void checkUsername(String username) {
        checkCharacterRules(username);
        checkDuplication(username);
        checkBadWords(username);
    }

    public void checkBadWords(String username) {
        String PERSPECTIVE_API_URL = "https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=";
        final String url = PERSPECTIVE_API_URL + envBean.getPerspectiveApiKey();
        final String requestBody = buildRequestBody(username);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PerspectiveDtoResponse> response = restTemplate.postForEntity(url, requestBody, PerspectiveDtoResponse.class);

        try {
            Double toxicityScore = response.getBody().getAttributeScores().getTOXICITY().getSummaryScore().getValue();
            Double BADWORD_THRESHOLD = 0.3;
            if (toxicityScore > BADWORD_THRESHOLD) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임이 부적절합니다.");
            }
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "통신에 실패했습니다");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다.");
        }
    }

    public void checkCharacterRules(String username) {
        if (!username.matches(USERNAME_PATTERN)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임은 3~30자의 영문, 숫자, ., _만 사용할 수 있습니다.");
        }
    }

    /** 사용자 닉네임 유효성 검사 */
    public void checkDuplication(String username) {
        if (memberRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"이미 존재하는 닉네임입니다.");
        }
    }

    private String buildRequestBody(String name) {
        return "{" +
                "\"comment\": {\"text\": \"" + name + "\"}, " +
                "\"languages\": [\"en\"], " +
                "\"requestedAttributes\": {\"TOXICITY\": {}}}";
    }
}
