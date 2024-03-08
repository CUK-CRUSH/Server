package crush.myList.domain.festival.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormData {
    @NotBlank
    @Schema(description = "나이", example = "20")
    private String age;
    @NotBlank
    @Schema(description = "성별", example = "남자")
    private String sex;
    @NotBlank
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    @NotBlank
    @Schema(description = "이름", example = "홍길동")
    private String name;
    @NotBlank
    @Schema(description = "url", example = "https://mylist.im/user/username")
    private String link;
    @NotBlank
    @Schema(description = "장르", example = "힙합")
    private String genre;
}
