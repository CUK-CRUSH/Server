package crush.myList.domain.playlist.controller;

import crush.myList.domain.playlist.dto.PlaylistDto;
import crush.myList.domain.playlist.service.PlaylistService;
import crush.myList.global.dto.JsonBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@ApiResponse(description = "플레이리스트 API. ")
@RestController
@Slf4j(topic = "PlaylistController")
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/{username}/playlist")
public class PlaylistController {
    private final PlaylistService playlistService;

    @Operation(summary = "유저의 플레이리스트 조회하기")
    @GetMapping("/")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "플레이리스트 조회 실패")
    })
    public JsonBody<List<PlaylistDto.Result>> getUserPlaylists(@PathVariable String username) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "플레이리스트 조회 성공",
                playlistService.getPlaylists(username)
        );
    }

    @Operation(summary = "유저의 플레이리스트 생성하기")
    @PostMapping(value = "/", produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 생성 성공"),
            @ApiResponse(responseCode = "401", description = "플레이리스트 생성 실패")
    })
    public JsonBody<PlaylistDto.Result> addPlaylist(
            @PathVariable String username,
            @RequestPart(required = true) String playlistName,
            @RequestPart(value = "image", required = false) MultipartFile titleImage
            ) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "플레이리스트 생성 성공",
                playlistService.addPlaylist(username, new PlaylistDto.PostRequest(playlistName), titleImage)
        );
    }

//    @PutMapping("/{playlistId}")
//    public JsonBody<String> updatePlaylist(@PathVariable Long playlistId) {
//        return JsonBody.of(
//                HttpStatus.OK,
//                "플레이리스트 수정 완료",
//                "흠"
//        );
//    }
//
//    @DeleteMapping("/{playlistId}")
//    public JsonBody<String> deletePlaylist(@PathVariable Long playlistId) {
//        return JsonBody.of(
//                HttpStatus.OK,
//                "플레이리스트 삭제 완료",
//                "흐음"
//        );
//    }
}
