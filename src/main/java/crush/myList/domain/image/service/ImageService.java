package crush.myList.domain.image.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import crush.myList.config.EnvBean;
import crush.myList.domain.image.entity.Image;
import crush.myList.domain.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Transactional
@Slf4j(topic = "ImageService")
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final EnvBean envBean;
    private final Storage storage;

    public void saveImageToGcs(MultipartFile imageFile) throws RuntimeException {
        String originalName = imageFile.getOriginalFilename();
        String ext = imageFile.getContentType();
        String uuid = UUID.randomUUID().toString();

        // 파일은 https://storage.googleapis.com/{버킷_이름}/{UUID}를 통해 조회할 수 있음
        BlobInfo imageInfo = BlobInfo.newBuilder(envBean.BUCKET_NAME, uuid)
                .setContentType(ext)
                .build();

        try {
            BlobInfo blobInfo = storage.create(imageInfo, imageFile.getBytes());
            String uploadPath = blobInfo.getMediaLink();
            String fileName = blobInfo.getName();

            Image image = Image.builder()
                    .originalName(originalName)
                    .name(fileName)
                    .extension(ext)
                    .uuid(uuid)
                    .url(uploadPath)
                    .build();

            imageRepository.save(image);

        } catch (IOException e) {
            log.error("ImageService.saveImageToGcs: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void deleteImageToGcs(String fileName) throws RuntimeException {
        Storage storage = StorageOptions.newBuilder().setProjectId(envBean.PROJECT_ID).build().getService();
        Blob blob = storage.get(envBean.BUCKET_NAME, fileName);
        if (blob == null) {
            log.error("ImageService.deleteObject: The object {} wasn't found in {}", fileName, envBean.BUCKET_NAME);
            return;
        }

        Storage.BlobSourceOption precondition =
                Storage.BlobSourceOption.generationMatch(blob.getGeneration());

        storage.delete(envBean.BUCKET_NAME, fileName, precondition);

        try {
            imageRepository.deleteByName(fileName);
        } catch (Exception e) {
            log.error("ImageService.deleteImageToGcs: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        log.info("ImageService.deleteObject: The object {} was deleted from {}", fileName, envBean.BUCKET_NAME);
    }
}
