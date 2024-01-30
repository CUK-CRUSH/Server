package crush.myList.domain.image.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import crush.myList.config.EnvBean;
import crush.myList.domain.image.dto.ImageDto;
import crush.myList.domain.image.entity.Image;
import crush.myList.domain.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    public byte[] convertToWebP(MultipartFile file) throws IOException {
        // MultipartFile을 BufferedImage로 변환
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 변환에 실패했습니다. 잘못된 이미지 파일입니다.");
        }

        // 비표준 색공간 이미지를 표준 RGB로 변환
        BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = convertedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // WebP 포맷으로 이미지 저장을 위한 설정
        ImageWriter writer = ImageIO.getImageWritersByMIMEType(file.getContentType()).next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        if (writeParam.canWriteCompressed()) {
            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            writeParam.setCompressionQuality(0.8f); // 압축 품질 설정 (0 ~ 1)
        }

        // BufferedImage를 WebP 형식으로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(convertedImage, null, null), writeParam);
        } catch (IOException e) {
            log.error("ImageService.convertToWebP: {}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            writer.dispose();
        }

        return baos.toByteArray();
    }


    public ImageDto saveImageToGcs(MultipartFile imageFile) throws RuntimeException {
        Image image = saveImageToGcs_Image(imageFile);
        return ImageDto.builder()
                .id(image.getId())
                .originalName(image.getOriginalName())
                .name(image.getName())
                .extension(image.getExtension())
                .uuid(image.getUuid())
                .url(image.getUrl())
                .build();
    }

    // ImageDto말고 Image가 필요한 경우
    public Image saveImageToGcs_Image(MultipartFile imageFile) throws RuntimeException {
        String originalName = imageFile.getOriginalFilename();
//        String ext = imageFile.getContentType();
        // 확장자를 webp로 고정
        String ext = "image/webp";
        String uuid = UUID.randomUUID().toString();

        // 파일은 https://storage.googleapis.com/{버킷_이름}/{UUID}를 통해 조회할 수 있음
        BlobInfo imageInfo = BlobInfo.newBuilder(envBean.getBucketName(), uuid)
                .setContentType(ext)
                .build();

        try {
            BlobInfo blobInfo = storage.create(imageInfo, convertToWebP(imageFile));
            String imageUrl = String.format("https://storage.googleapis.com/%s/%s", envBean.getBucketName(), uuid);
            String fileName = blobInfo.getName();

            Image image = Image.builder()
                    .originalName(originalName)
                    .name(fileName)
                    .extension(ext)
                    .uuid(uuid)
                    .url(imageUrl)
                    .build();

            return imageRepository.save(image);
        } catch (IOException e) {
            log.error("ImageService.saveImageToGcs: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void deleteImage(Image image) throws RuntimeException {
        String fileName = image.getName();
        Blob blob = storage.get(envBean.getBucketName(), fileName);
        if (blob == null) {
            log.error("ImageService.deleteObject: The object {} wasn't found in {}", fileName, envBean.getBucketName());
            return;
        }
        try {
            Storage.BlobSourceOption precondition =
                    Storage.BlobSourceOption.generationMatch(blob.getGeneration());

            storage.delete(envBean.getBucketName(), fileName, precondition);

            imageRepository.deleteByName(fileName);
        } catch (Exception e) {
            log.error("ImageService.deleteImageToGcs: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void deleteImageToGcs(Long id) throws RuntimeException {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 이미지가 존재하지 않습니다."));
        deleteImage(image);
    }
    // id말고 Image를 인자로 받는 경우
    public void deleteImageToGcs(Image image) throws RuntimeException {
        deleteImage(image);
    }
}
