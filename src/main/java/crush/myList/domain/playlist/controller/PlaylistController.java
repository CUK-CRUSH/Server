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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiResponse(description = "플레이리스트 API. ")
@RestController
@Slf4j(topic = "PlaylistController")
@RequiredArgsConstructor
@RequestMapping("/user/{userId}/playlist")
public class PlaylistController {
    private final PlaylistService playlistService;
    @Operation(summary = "유저의 플레이리스트 조회하기")
    @GetMapping("/")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "플레이리스트 조회 실패")
    })
    public JsonBody<List<PlaylistDto>> getUserPlaylists(@PathVariable String userId) {
        List<PlaylistDto> playlists = playlistService.getPlaylists(userId);
        return JsonBody.of(
                HttpStatus.OK,
                "플레이리스트 조회 성공",
                playlists
        );
    }

//    @PostMapping("/")
//    public JsonBody<String> addPlaylist() {
//
//        return JsonBody.builder()
//                .message("플레이리스트 추가 성공")
//                .data("addPlaylistToUser")
//                .build();
//    }
//
//    @PutMapping("/{playlistID}")
//    public JsonBody<String> updatePlaylist(@PathVariable Long playlistID) {
//        return JsonBody.builder()
//                .message("플레이리스트 수정 성공")
//                .data("updatePlaylist")
//                .build();
//    }
//
//    @DeleteMapping("/{playlistID}")
//    public JsonBody<String> deletePlaylist(@PathVariable Long playlistID) {
//        return JsonBody.builder()
//                .message("플레이리스트 삭제 성공")
//                .data("deletePlaylist")
//                .build();
//    }
//
//    @GetMapping("/{playlistID}/musics")
//    public JsonBody<String> getMusics(@PathVariable Long playlistID) {
//        return JsonBody.builder()
//                .message("음악 조회 성공")
//                .data("getMusics")
//                .build();
//    }
//
//    @PostMapping("/{playlistID}/musics")
//    public JsonBody<String> addMusics(@PathVariable Long playlistID) {
//        return JsonBody.builder()
//                .message("음악 추가 성공")
//                .data("addMusics")
//                .build();
//    }
//
//    @DeleteMapping("/{playlistID}/musics/{musicID}")
//    public JsonBody<String> deleteMusic(@PathVariable Long playlistID, @PathVariable Long musicID) {
//        return JsonBody.builder()
//                .message("음악 삭제 성공")
//                .data("deleteMusic")
//                .build();
//    }
}
