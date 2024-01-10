package crush.myList.domain.member.controller;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.member.service.MemberService;
import crush.myList.global.dto.JsonBody;
import crush.myList.global.schema.ChangeNickname_200;
import crush.myList.global.schema.ChangeNickname_400;
import crush.myList.global.schema.CheckNickname_200;
import crush.myList.global.schema.CheckNickname_400;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@ApiResponse(description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@Slf4j(topic = "MemberController")
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "회원 닉네임 중복 검사")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = CheckNickname_200.class))}),
            @ApiResponse(responseCode = "400", description = "이미 사용중인 닉네임", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = CheckNickname_400.class))})
    })
    @GetMapping("/nickname/check/{username}")
    public JsonBody<String> checkNickname(@PathVariable String username) {
        memberService.checkUsername(username);
        return JsonBody.of(HttpStatus.OK.value(), "사용 가능한 닉네임", username);
    }

    @Operation(summary = "회원 닉네임 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 변경 성공", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ChangeNickname_200.class))}),
            @ApiResponse(responseCode = "400", description = "닉네임 변경 실패", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ChangeNickname_400.class))})
    })
    @PostMapping("/nickname/change/{username}")
    public JsonBody<String> changeNickname(@PathVariable String username,  @AuthenticationPrincipal SecurityMember member) {
        memberService.changeUsername(Long.parseLong(member.getId()), username);
        return JsonBody.of(HttpStatus.OK.value(), "닉네임 변경 성공", username);
    }
}
