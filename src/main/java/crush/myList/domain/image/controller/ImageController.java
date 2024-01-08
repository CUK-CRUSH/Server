package crush.myList.domain.image.controller;

import crush.myList.domain.image.dto.ImageDto;
import crush.myList.domain.image.service.ImageService;
import crush.myList.global.dto.JsonBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping(value = "/save", produces = "application/json", consumes = "multipart/form-data")
    public JsonBody<String> saveImageToGcs(@RequestParam(value = "image") MultipartFile imageFile) {
        ImageDto imageDto = imageService.saveImageToGcs(imageFile);
        return JsonBody.of(HttpStatus.OK.value(), "이미지가 성공적으로 저장되었습니다.", imageDto.getUrl());
    }

    @DeleteMapping(value = "/delete", produces = "application/json")
    public JsonBody<Long> deleteImageToGcs(@RequestParam(value = "id") Long imageId) {
        imageService.deleteImageToGcs(imageId);
        return JsonBody.of(HttpStatus.OK.value(), "이미지가 성공적으로 삭제되었습니다.", imageId);
    }
}
