package crush.myList.domain.playlist.controller;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.playlist.dto.GuestBookDto;
import crush.myList.domain.playlist.dto.PlaylistDto;
import crush.myList.domain.playlist.dto.LikeMember;
import crush.myList.domain.playlist.service.GuestBookService;
import crush.myList.domain.playlist.service.LikeService;
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

@Tag(name = "Playlist", description = "플레이리스트 관련 API 모두 보기")
@RestController
@Slf4j(topic = "PlaylistController")
@RequiredArgsConstructor
@RequestMapping("/api/v1/playlist")
public class PlaylistController {
    private final PlaylistService playlistService;
    private final LikeService likeService;
    private final GuestBookService guestBookService;

    @Operation(summary = "유저의 플레이리스트 조회하기", tags = {"플레이리스트"})
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

    @Operation(summary = "플레이리스트 단일 조회", tags = {"플레이리스트"})
    @GetMapping("/{playlistId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "플레이리스트 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<PlaylistDto.Response> getPlaylist(@PathVariable Long playlistId, @AuthenticationPrincipal SecurityMember securityMember) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "플레이리스트 조회 성공",
                playlistService.getPlaylist(playlistId, securityMember)
        );
    }

    @Operation(summary = "유저의 플레이리스트 생성하기", tags = {"플레이리스트"})
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

    @Operation(summary = "유저의 플레이리스트 변경하기", tags = {"플레이리스트"})
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

    @Operation(summary = "유저의 플레이리스트 삭제하기", tags = {"플레이리스트"})
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

    @Operation(summary = "플레이리스트의 이미지 삭제하기", tags = {"플레이리스트"})
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



    /**
    #################################################################################################################
     좋아요 관련 API
    #################################################################################################################
     */


    @Operation(summary = "플레이리스트의 좋아요 누른 사용자 조회하기", tags = {"좋아요"})
    @GetMapping("/{playlistId}/like")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 좋아요 조회 성공"),
            @ApiResponse(responseCode = "404", description = "플레이리스트 좋아요 조회 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<List<LikeMember>> getPlaylistLike(@PathVariable Long playlistId) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "플레이리스트 좋아요 조회 성공",
                likeService.getLikeMembers(playlistId)
        );
    }

    @Operation(summary = "플레이리스트 좋아요 하기", tags = {"좋아요"})
    @PostMapping("/{playlistId}/like")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 좋아요 성공"),
            @ApiResponse(responseCode = "403", description = "비허가된 유저의 접근", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "플레이리스트 좋아요 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<Long> likePlaylist(@AuthenticationPrincipal SecurityMember member, @PathVariable Long playlistId) {
        likeService.likePlaylist(member, playlistId);
        return JsonBody.of(
                HttpStatus.OK.value(),
                "플레이리스트 좋아요 완료",
                playlistId
        );
    }

    @Operation(summary = "플레이리스트 좋아요 취소하기", tags = {"좋아요"})
    @DeleteMapping("/{playlistId}/like")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 좋아요 취소 성공"),
            @ApiResponse(responseCode = "403", description = "비허가된 유저의 접근", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "플레이리스트 좋아요 취소 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<Long> unlikePlaylist(@AuthenticationPrincipal SecurityMember member, @PathVariable Long playlistId) {
        likeService.unlikePlaylist(member, playlistId);
        return JsonBody.of(
                HttpStatus.OK.value(),
                "플레이리스트 좋아요 취소 완료",
                playlistId
        );
    }

    /**
    #################################################################################################################
     플레이리스트 방명록 관련 API
    #################################################################################################################
     */


    @Operation(summary = "플레이리스트 방명록 조회하기", tags = {"방명록"})
    @GetMapping("/{playlistId}/guestbook")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 방명록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "플레이리스트 방명록 조회 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<List<GuestBookDto.Response>> getPlaylistGuestbook(@PathVariable Long playlistId) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "플레이리스트 방명록 조회 성공",
                guestBookService.getGuestBooks(playlistId)
        );
    }

    @Operation(summary = "플레이리스트 방명록 작성하기", tags = {"방명록"})
    @PostMapping("/{playlistId}/guestbook")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 방명록 작성 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<GuestBookDto.Response> postPlaylistGuestbook(
            @AuthenticationPrincipal SecurityMember member,
            @PathVariable Long playlistId,
            @RequestBody GuestBookDto.Post request
    ) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "플레이리스트 방명록 작성 성공",
                guestBookService.postGuestBook(member, playlistId, request.getContent())
        );
    }

    @Operation(summary = "플레이리스트 방명록 삭제하기", tags = {"방명록"})
    @DeleteMapping("/{playlistId}/guestbook/{guestbookId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 방명록 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "플레이리스트 방명록 삭제 실패", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "플레이리스트 방명록 삭제 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<Long> deletePlaylistGuestbook(@AuthenticationPrincipal SecurityMember member, @PathVariable Long playlistId, @PathVariable Long guestbookId) {
        guestBookService.deleteGuestBook(member, playlistId, guestbookId);
        return JsonBody.of(
                HttpStatus.OK.value(),
                "플레이리스트 방명록 삭제 성공",
                guestbookId
        );
    }

    @Operation(summary = "플레이리스트 방명록 수정하기", tags = {"방명록"})
    @PatchMapping("/{playlistId}/guestbook/{guestbookId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "플레이리스트 방명록 수정 성공"),
            @ApiResponse(responseCode = "404", description = "플레이리스트 방명록 수정 실패", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", description = "플레이리스트 방명록 수정 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<GuestBookDto.Response> updatePlaylistGuestbook(@AuthenticationPrincipal SecurityMember member, @PathVariable Long playlistId, @PathVariable Long guestbookId, @RequestBody GuestBookDto.Post request) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "플레이리스트 방명록 수정 성공",
                guestBookService.updateGuestBook(member, playlistId, guestbookId, request.getContent())
        );
    }
}
