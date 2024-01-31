package crush.myList.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import crush.myList.config.EnvBean;
import crush.myList.domain.image.dto.ImageDto;
import crush.myList.domain.image.entity.Image;
import crush.myList.domain.image.repository.ImageRepository;
import crush.myList.domain.image.service.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("ImageService 테스트")
public class ImageServiceTest {
    @InjectMocks
    private ImageService imageService;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private EnvBean envBean;
    @Mock
    private Storage storage;

    @Test
    @DisplayName("이미지 파일 GCS에 저장 테스트.")
    void saveImageToGcsTest() throws IOException {
        // given
        URL resource = getClass().getResource("/img/test.png");
        FileInputStream file = new FileInputStream(resource.getFile());
        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", "image/jpeg", file);
        Blob blob = Mockito.mock(Blob.class);
        Image image = Image.builder()
                .id(1L)
                .originalName("image.jpg")
                .uuid("uuid")
                .extension("webp")
                .name("image.webp")
                .url("테스트 url")
                .build();

        given(envBean.getBucketName()).willReturn("mylist-test-bucket");
        given(storage.create(Mockito.any(BlobInfo.class), Mockito.any(byte[].class))).willReturn(blob);
        given(blob.getName()).willReturn("image.webp");
        given(imageRepository.save(Mockito.any(Image.class))).willReturn(image);

        // when
        ImageDto imageDto = imageService.saveImageToGcs(imageFile);

        // then
        assertThat(imageDto.getUrl()).isNotNull();
        assertThat(imageDto.getId()).isEqualTo(1L);
        assertThat(imageDto.getOriginalName()).isEqualTo("image.jpg");
        assertThat(imageDto.getUuid()).isEqualTo("uuid");
        assertThat(imageDto.getExtension()).isEqualTo("webp");
        assertThat(imageDto.getName()).isEqualTo("image.webp");
        assertThat(imageDto.getUrl()).isEqualTo("테스트 url");
    }

    @Test
    @DisplayName("이미지 파일 GCS에 삭제 테스트.")
    void deleteImageToGcsTest() throws IOException {
        // given
        Image image = Image.builder()
                .id(1L)
                .originalName("image.jpg")
                .uuid("uuid")
                .extension("webp")
                .name("image.webp")
                .url("테스트 url")
                .build();
        given(imageRepository.findById(1L)).willReturn(Optional.ofNullable(image));
        given(envBean.getBucketName()).willReturn("mylist-test-bucket");
        given(storage.get(eq("mylist-test-bucket"), eq("image.webp"))).willReturn(Mockito.mock(Blob.class));
        given(storage.delete(eq("mylist-test-bucket"), eq("image.webp"), Mockito.any(Storage.BlobSourceOption.class))).willReturn(true);
        // imageRepository.delete(image)는 void이므로 생략

        // when
        imageService.deleteImageToGcs(1L);

        // then
        // 예외가 발생하지 않으면 성공
    }
}
