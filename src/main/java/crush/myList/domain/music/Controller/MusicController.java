package crush.myList.domain.music.Controller;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.music.Dto.MusicDto;
import crush.myList.domain.music.Service.MusicService;
import crush.myList.global.dto.JsonBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Music", description = "음악 API")
@RestController
@Slf4j(topic = "MusicController")
@RequiredArgsConstructor
@RequestMapping("/api/v1/music")
public class MusicController {
    private final MusicService musicService;

    @Operation(summary = "플레이리스트의 음악 조회하기")
    @GetMapping("/{playlistId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "음악 조회 성공"),
            @ApiResponse(responseCode = "404", description = "음악 조회 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<List<MusicDto.Result>> getMusics(@PathVariable Long playlistId) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "음악 조회 성공",
                musicService.getMusics(playlistId)
        );
    }

    @Operation(summary = "플레이리스트에 음악 추가하기")
    @PostMapping(value = "/{playlistId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "음악 생성 성공"),
            @ApiResponse(responseCode = "403", description = "비허가된 유저의 접근", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "음악 생성 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<MusicDto.Result> addMusic(@AuthenticationPrincipal SecurityMember member, @PathVariable Long playlistId, @RequestBody MusicDto.Request request) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "음악 추가 성공",
                musicService.addMusic(member, playlistId, request)
        );
    }

    @Operation(summary = "유저의 플레이리스트 삭제하기")
    @DeleteMapping("")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "음악 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "비허가된 유저의 접근", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "음악 삭제 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<Long> deleteMusic(@AuthenticationPrincipal SecurityMember member, @RequestParam Long musicId) {
        musicService.deleteMusic(member, musicId);
        return JsonBody.of(
                HttpStatus.OK.value(),
                "음악 삭제 성공",
                musicId
        );
    }
}
