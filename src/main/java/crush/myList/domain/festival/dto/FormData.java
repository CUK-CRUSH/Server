package crush.myList.domain.festival.dto;

import crush.myList.domain.festival.mongo.document.Form;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormData {
    @NotBlank
    @Schema(description = "이름", example = "홍길동")
    private String name;
    @NotBlank
    @Schema(description = "성별", example = "남자")
    private String sex;
    @NotBlank
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    @NotBlank
    @Schema(description = "사용자명", example = "username")
    private String username;

    public static FormData of(Form form) {
        return FormData.builder()
                .name(form.getName())
                .sex(form.getSex())
                .phone(form.getPhone())
                .username(form.getUsername())
                .build();
    }

    public static List<FormData> of(List<Form> list) {
        return list.stream().map(FormData::of).toList();
    }
}
