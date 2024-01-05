package crush.myList.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import crush.myList.config.EnvBean;
import crush.myList.domain.image.entity.Image;
import crush.myList.domain.image.repository.ImageRepository;
import crush.myList.domain.image.service.ImageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("GCS 이미지 파일 mock 테스트")
public class ImageServiceTest {
//    @InjectMocks
//    private ImageService imageService;
//    @BeforeEach
//    void before() throws Exception {
////        System.out.println("bucketName: " + this.bucketName);
//        Class<EnvBean> envBeanClass = EnvBean.class;
//        Field bucketName = envBeanClass.getDeclaredField("bucketName");
//        Field projectId = envBeanClass.getDeclaredField("projectId");
//
//        bucketName.setAccessible(true);
////        bucketName.set(envBean, this.bucketName);
//        bucketName.set(envBean, "mylist-image");
//        bucketName.setAccessible(false);
//
//        projectId.setAccessible(true);
////        projectId.set(envBean, this.projectId);
//        projectId.set(envBean, "eighth-alchemy-408113");
//        projectId.setAccessible(false);
//
//        System.out.println("bucketName: " + envBean.getBucketName());
//    }
//
//    @Test
//    @DisplayName("이미지 파일을 GCS에 저장한다.")
//    void saveImageToGcsTest() throws NoSuchFieldException {
//        System.out.println("bucketName: " + envBean.getBucketName());
//
//        // given
//        MockMultipartFile imageFile = new MockMultipartFile("image", "image.jpg", "image/jpeg", "image".getBytes());
//
//        given(imageRepository.save(Mockito.any(Image.class))).will(invocation -> invocation.getArgument(0));
//
//        // when
//        imageService.saveImageToGcs(imageFile);
//
//        // then
//    }
}
