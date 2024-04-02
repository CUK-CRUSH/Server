package crush.myList.domain.music.controller;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.music.dto.MusicDto;
import crush.myList.domain.music.service.MusicService;
import crush.myList.domain.playlist.dto.PlaylistDto;
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
import org.springframework.http.MediaType;
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
    public JsonBody<List<MusicDto.Result>> getMusics(@PathVariable Long playlistId,
                                                     @RequestParam(required = false, defaultValue = "0") Integer page) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "음악 조회 성공",
                musicService.getMusics(playlistId, page)
        );
    }

    @Operation(summary = "플레이리스트에 음악 추가하기")
    @PostMapping(value = "/{playlistId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "음악 생성 성공"),
            @ApiResponse(responseCode = "403", description = "비허가된 유저의 접근", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "음악 생성 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<MusicDto.Result> addMusic(@AuthenticationPrincipal SecurityMember member, @PathVariable Long playlistId, @RequestBody MusicDto.PostRequest postRequest) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "음악 추가 성공",
                musicService.addMusic(member, playlistId, postRequest)
        );
    }

    @Operation(summary = "음악 여러곡 추가하기")
    @PostMapping("/{playlistId}/multiple")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "음악 추가 성공"),
            @ApiResponse(responseCode = "403", description = "비허가된 유저의 접근", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "음악 추가 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<List<MusicDto.Result>> addMultipleMusic(@AuthenticationPrincipal SecurityMember member, @PathVariable Long playlistId, @RequestBody List<MusicDto.PostRequest> postRequests) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "음악 추가 성공",
                musicService.addMultipleMusic(member, playlistId, postRequests)
        );
    }

    @Operation(summary = "음악 수정하기")
    @PatchMapping("")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "음악 수정 성공"),
            @ApiResponse(responseCode = "403", description = "비허가된 유저의 접근", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "음악 수정 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<MusicDto.Result> updateMusic(@AuthenticationPrincipal SecurityMember member, @RequestParam String musicId, @RequestBody MusicDto.PatchRequest patchRequest) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "음악 수정 성공",
                musicService.updateMusic(member, musicId, patchRequest)
        );
    }


    @Operation(summary = "여러개의 음악 한번에 수정하기")
    @PatchMapping("/{playlistId}/multiple")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "음악 수정 성공"),
            @ApiResponse(responseCode = "403", description = "비허가된 유저의 접근", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "음악 수정 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<List<MusicDto.Result>> updateMultipleMusic(@AuthenticationPrincipal SecurityMember member, @PathVariable Long playlistId, @RequestBody List<MusicDto.PatchRequestV1> patchRequests) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "음악 수정 성공",
                musicService.updateMultipleMusics(member, playlistId, patchRequests)
        );
    }


    @Operation(summary = "유저의 플레이리스트 삭제하기")
    @DeleteMapping("")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "음악 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "비허가된 유저의 접근", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "음악 삭제 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<String> deleteMusic(@AuthenticationPrincipal SecurityMember member, @RequestParam String musicId) {
        musicService.deleteMusic(member, musicId);
        return JsonBody.of(
                HttpStatus.OK.value(),
                "음악 삭제 성공",
                musicId
        );
    }
}
