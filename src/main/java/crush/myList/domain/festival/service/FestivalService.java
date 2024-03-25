package crush.myList.domain.festival.service;

import crush.myList.domain.festival.dto.FormData;
import crush.myList.domain.festival.mongo.document.Form;
import crush.myList.domain.festival.mongo.repository.FormRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j(topic = "FestivalService")
@Transactional
@RequiredArgsConstructor
public class FestivalService {
    private final FormRepository formRepository;

    private void checkUsername(FormData formData) {
        // 사용자 이름이 설정되어 있는지 확인
        if (formData.getUsername() == null || formData.getUsername().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자 이름이 설정되지 않았습니다.");
        }
        // 이미 신청한 사용자인지 확인
        if (formRepository.existsByUsername(formData.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 신청한 사용자 입니다.");
        }
    }

    public String matchPost(FormData formData) {
        checkUsername(formData);
        return formRepository.save(Form.builder()
                .name(formData.getName())
                .sex(formData.getSex())
                .phone(formData.getPhone())
                .username(formData.getUsername())
                .build()).getId();
    }

    public FormData matchGet(String username) {
        Form form = formRepository.findByUsername(username).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "신청서를 찾을 수 없습니다."));
        return FormData.builder()
                .name(form.getName())
                .sex(form.getSex())
                .phone(form.getPhone())
                .username(form.getUsername())
                .build();
    }

    public String matchDelete(String username) {
        try {
            formRepository.deleteByUsername(username);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "신청서를 찾을 수 없습니다.");
        }
        return username;
    }
}
