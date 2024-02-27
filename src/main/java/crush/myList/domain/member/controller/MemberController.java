package crush.myList.domain.member.controller;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.member.dto.EditProfileReq;
import crush.myList.domain.member.dto.EditProfileRes;
import crush.myList.domain.member.dto.MemberDto;
import crush.myList.domain.member.service.MemberService;
import crush.myList.domain.member.service.UsernameService;
import crush.myList.domain.playlist.dto.PlaylistDto;
import crush.myList.domain.playlist.service.LikeService;
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

@Tag(name = "Member", description = "회원 API")
@RestController
@RequiredArgsConstructor
@Slf4j(topic = "MemberController")
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;
    private final UsernameService usernameService;
    private final LikeService likeService;

    @Operation(summary = "id로 특정 회원 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "회원 정보 조회 실패", content = {@Content(mediaType = "application/json")})
    })
    @GetMapping("/id/{id}")
    public JsonBody<MemberDto> getMember(@PathVariable Long id) {
        MemberDto memberDto = memberService.getMember(id);
        return JsonBody.of(HttpStatus.OK.value(), "회원 정보 조회 성공", memberDto);
    }

    @Operation(summary = "닉네임으로 특정 회원 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "회원 정보 조회 실패", content = {@Content(mediaType = "application/json")})
    })
    @GetMapping("/nickname/{username}")
    public JsonBody<MemberDto> getMember(@PathVariable String username) {
        MemberDto memberDto = memberService.getMember(username);
        return JsonBody.of(HttpStatus.OK.value(), "회원 정보 조회 성공", memberDto);
    }

    @Operation(summary = "내 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "회원 정보 조회 실패", content = {@Content(mediaType = "application/json")})
    })
    @GetMapping("/me")
    public JsonBody<MemberDto> getMember(@AuthenticationPrincipal SecurityMember member) {
        MemberDto memberDto = memberService.getMember(member.getId());
        return JsonBody.of(HttpStatus.OK.value(), "회원 정보 조회 성공", memberDto);
    }
    @Operation(summary = "내 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "회원 정보 수정 실패", content = {@Content(mediaType = "application/json")})
    })
    @PatchMapping(value = "/me", consumes = "multipart/form-data")
    public JsonBody<EditProfileRes> updateInfo(@ModelAttribute EditProfileReq editProfileReq, @AuthenticationPrincipal SecurityMember member) {
        EditProfileRes res = memberService.updateInfo(editProfileReq, member.getId());
        return JsonBody.of(HttpStatus.OK.value(), "회원 정보 수정 성공", res);
    }

//    @Operation(summary = "회원 닉네임 변경. deprecated")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "닉네임 변경 성공", content = {@Content(mediaType = "application/json")}),
//            @ApiResponse(responseCode = "400", description = "닉네임 변경 실패", content = {@Content(mediaType = "application/json")})
//    })
//    @PutMapping("/me/{username}")
//    public JsonBody<String> changeNickname(@PathVariable String username,  @AuthenticationPrincipal SecurityMember member) {
//        memberService.changeUsername(member.getId(), username);
//        return JsonBody.of(HttpStatus.OK.value(), "닉네임 변경 성공", username);
//    }

    @Operation(summary = "회원 닉네임 중복 검사")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "이미 사용중인 닉네임", content = {@Content(mediaType = "application/json")})
    })
    @GetMapping("/nickname/available/{username}")
    public JsonBody<String> checkNickname(@PathVariable String username) {
        usernameService.checkDuplication(username);
        return JsonBody.of(HttpStatus.OK.value(), "사용 가능한 닉네임", username);
    }

    @Operation(summary = "회원 탈퇴")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "회원 탈퇴 실패", content = {@Content(mediaType = "application/json")})
    })
    @DeleteMapping("/me")
    public JsonBody<Long> deleteMember(@AuthenticationPrincipal SecurityMember member) {
        memberService.deleteMember(member);
        return JsonBody.of(HttpStatus.OK.value(), "회원 탈퇴 성공", member.getId());
    }

    @Operation(summary = "사용자가 좋아요한 플레이리스트 조회하기", tags = {"좋아요"})
    @GetMapping("/playlist/like")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자가 좋아요한 플레이리스트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자가 좋아요한 플레이리스트 조회 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<List<PlaylistDto.Response>> getUserLikedPlaylist(
            @AuthenticationPrincipal SecurityMember member,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = true) String username
    ) {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "사용자가 좋아요한 플레이리스트 조회 성공",
                likeService.getLikedPlaylists(username, page)
        );
    }
}
