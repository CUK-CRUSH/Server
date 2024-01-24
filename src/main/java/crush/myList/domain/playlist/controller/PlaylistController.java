package crush.myList.domain.playlist.controller;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.playlist.dto.PlaylistDto;
import crush.myList.domain.playlist.service.PlaylistService;
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

@Tag(name = "Playlist", description = "플레이리스트 API")
@RestController
@Slf4j(topic = "PlaylistController")
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlist")
public class PlaylistController {
    private final PlaylistService playlistService;

    @Operation(summary = "유저의 플레이리스트 조회하기")
    @GetMapping("/user/{username}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<List<PlaylistDto.Response>> getUserPlaylists(@PathVariable String username) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "플레이리스트 조회 성공",
                playlistService.getPlaylists(username)
        );
    }

    @Operation(summary = "플레이리스트 단일 조회")
    @GetMapping("/{playlistId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "플레이리스트 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<PlaylistDto.Response> getPlaylist(@PathVariable String playlistId) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "플레이리스트 조회 성공",
                playlistService.getPlaylist(playlistId)
        );
    }

    @Operation(summary = "유저의 플레이리스트 생성하기")
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 생성 성공"),
            @ApiResponse(responseCode = "404", description = "인증 문제 발생", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<PlaylistDto.Response> addPlaylist(
            @ModelAttribute PlaylistDto.PostRequest postRequest,
            @AuthenticationPrincipal SecurityMember member
            ) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "플레이리스트 생성 성공",
                playlistService.addPlaylist(member, postRequest)
        );
    }

    @Operation(summary = "유저의 플레이리스트 변경하기")
    @PatchMapping(value = "/{playlistId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 정보 수정 성공"),
            @ApiResponse(responseCode = "403", description = "비허가된 유저의 접근", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "플레이리스트 변경 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<PlaylistDto.Response> updatePlaylist(
            @AuthenticationPrincipal SecurityMember member,
            @PathVariable Long playlistId,
            @ModelAttribute PlaylistDto.PatchRequest patchRequest
    ) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "플레이리스트 수정 완료",
                playlistService.updatePlaylist(member, playlistId, patchRequest)
        );
    }

    @Operation(summary = "유저의 플레이리스트 삭제하기")
    @DeleteMapping("/{playlistId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "비허가된 유저의 접근", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "플레이리스트 삭제 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<Long> deletePlaylist(@AuthenticationPrincipal SecurityMember member, @PathVariable Long playlistId) {
        playlistService.deletePlaylist(member, playlistId);
        return JsonBody.of(
                HttpStatus.OK.value(),
                "플레이리스트 삭제 완료",
                playlistId
        );
    }

    @Operation(summary = "플레이리스트의 이미지 삭제하기")
    @DeleteMapping("/{playlistId}/image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 이미지 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "비허가된 유저의 접근", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "플레이리스트 이미지 삭제 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<Long> deletePlaylistImage(@AuthenticationPrincipal SecurityMember member, @PathVariable Long playlistId) {
        playlistService.deletePlaylistImage(member, playlistId);
        return JsonBody.of(
                HttpStatus.OK.value(),
                "플레이리스트 이미지 삭제 완료",
                playlistId
        );
    }
}
