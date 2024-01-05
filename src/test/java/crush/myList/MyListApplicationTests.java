package crush.myList;

import crush.myList.domain.image.dto.ImageDto;
import crush.myList.domain.image.service.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("통합 테스트")
@SpringBootTest
class MyListApplicationTests {
    @Autowired
    private ImageService imageService;
    @Test
    @WithMockUser(username = "test")
    @DisplayName("이미지 파일 GCS에 저장 및 삭제 테스트.")
    void saveImageToGcsTest() {
        // given
        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", "image/jpeg", "image".getBytes());

        // when
        ImageDto imageDto = imageService.saveImageToGcs(imageFile);
        imageService.deleteImageToGcs(imageDto.getId());

        // then
        assertThat(imageDto.getUrl()).isNotNull();
    }
}
