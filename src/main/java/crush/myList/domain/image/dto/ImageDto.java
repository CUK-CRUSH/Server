package crush.myList.domain.image.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    private Long id;
    private String originalName;
    private String name;
    private String extension;
    private String uuid;
    private String url;
}
