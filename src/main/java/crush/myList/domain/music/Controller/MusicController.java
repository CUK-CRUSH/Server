package crush.myList.domain.music.Controller;

import crush.myList.domain.music.Dto.MusicDto;
import crush.myList.domain.music.Service.MusicService;
import crush.myList.global.dto.JsonBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiResponse(description = "음악 API. ")
@RestController
@Slf4j(topic = "MusicController")
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/{username}/playlist/{playlistId}/music")
public class MusicController {
    private final MusicService musicService;

    @Operation(summary = "플레이리스트의 음악 조회하기")
    @GetMapping("/")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "음악 조회 성공"),
            @ApiResponse(responseCode = "401", description = "음악 조회 실패")
    })
    public JsonBody<List<MusicDto.Result>> getMusics(@PathVariable String username, @PathVariable Long playlistId) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "음악 조회 성공",
                musicService.getMusics(playlistId)
        );
    }

    @Operation(summary = "플레이리스트에 음악 추가하기")
    @PostMapping(value = "/")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "음악 생성 성공"),
            @ApiResponse(responseCode = "401", description = "음악 생성 실패")
    })
    public JsonBody<MusicDto.Result> addMusic(@PathVariable String username, @PathVariable Long playlistId, @RequestBody MusicDto.Request request) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "음악 추가 성공",
                musicService.addMusic(username, playlistId, request)
        );
    }

    @Operation(summary = "유저의 플레이리스트 삭제하기")
    @DeleteMapping("/{musicId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "음악 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "음악 삭제 실패")
    })
    public JsonBody<Long> deleteMusic(@PathVariable String username, @PathVariable Long playlistId, @PathVariable Long musicId) {
        musicService.deleteMusic(username, musicId);
        return JsonBody.of(
                HttpStatus.OK.value(),
                "음악 삭제 성공",
                musicId
        );
    }
}
