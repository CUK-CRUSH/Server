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

    private void checkFormData(FormData formData) {
        // link가 'https://mylist.im/user/*' 형식의 유효한 URL인지 확인
        if (!formData.getLink().matches("https://mylist.im/user/.*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효한 URL이 아닙니다.");
        }
    }

    public String matchPost(FormData formData) {
        checkFormData(formData);
        return formRepository.save(Form.builder()
                .age(formData.getAge())
                .sex(formData.getSex())
                .phone(formData.getPhone())
                .name(formData.getName())
                .link(formData.getLink())
                .genre(formData.getGenre())
                .build()).getId();
    }

    public FormData matchGet(String id) {
        Form form = formRepository.findById(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "신청서를 찾을 수 없습니다."));
        return FormData.builder()
                .age(form.getAge())
                .sex(form.getSex())
                .phone(form.getPhone())
                .name(form.getName())
                .link(form.getLink())
                .genre(form.getGenre())
                .build();
    }

    public String matchDelete(String id) {
        formRepository.deleteById(id);
        return id;
    }
}
