package crush.myList.domain.festival.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormData {
    @NotBlank
    private String age;
    @NotBlank
    private String sex;
    @NotBlank
    private String phone;
    @NotBlank
    private String name;
    @NotBlank
    private String link;
    @NotBlank
    private String genre;
}
