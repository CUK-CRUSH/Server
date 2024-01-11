package crush.myList.domain.member.controller;

import com.google.api.client.http.HttpResponseException;
import crush.myList.config.security.SecurityMember;
import crush.myList.domain.image.dto.ImageDto;
import crush.myList.domain.image.service.ImageService;
import crush.myList.domain.member.dto.EditProfileReq;
import crush.myList.domain.member.dto.EditProfileRes;
import crush.myList.domain.member.dto.MemberDto;
import crush.myList.domain.member.service.MemberService;
import crush.myList.global.dto.JsonBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@ApiResponse(description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@Slf4j(topic = "MemberController")
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;
    private final ImageService imageService;

    @Operation(summary = "회원 닉네임 중복 검사")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "이미 사용중인 닉네임", content = {@Content(mediaType = "application/json")})
    })
    @GetMapping("/nickname/check/{username}")
    public JsonBody<String> checkNickname(@PathVariable String username) {
        memberService.checkUsername(username);
        return JsonBody.of(HttpStatus.OK.value(), "사용 가능한 닉네임", username);
    }

    @Operation(summary = "회원 닉네임 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 변경 성공", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "닉네임 변경 실패", content = {@Content(mediaType = "application/json")})
    })
    @PostMapping("/nickname/change/{username}")
    public JsonBody<String> changeNickname(@PathVariable String username,  @AuthenticationPrincipal SecurityMember member) {
        memberService.changeUsername(member.getId(), username);
        return JsonBody.of(HttpStatus.OK.value(), "닉네임 변경 성공", username);
    }

    @Operation(summary = "회원 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = EditProfileRes.class))}),
            @ApiResponse(responseCode = "400", description = "회원 정보 수정 실패", content = {@Content(mediaType = "application/json")})
    })
    @PatchMapping(value = "/info", consumes = "multipart/form-data")
    public JsonBody<EditProfileRes> updateInfo(@ModelAttribute EditProfileReq editProfileReq, @AuthenticationPrincipal SecurityMember member) {
        EditProfileRes res = memberService.updateInfo(editProfileReq, member.getId());
        return JsonBody.of(HttpStatus.OK.value(), "회원 정보 수정 성공", res);
    }
}
