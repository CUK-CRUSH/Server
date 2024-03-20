package crush.myList.domain.festival.controller;

import crush.myList.domain.festival.dto.FormData;
import crush.myList.domain.festival.service.FestivalService;
import crush.myList.global.dto.JsonBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Festival", description = "축제 매칭 신청 API")
@RestController
@Slf4j(topic = "FestivalController")
@RequiredArgsConstructor
@RequestMapping("/api/v1/festival")
public class FestivalController {
    private final FestivalService festivalService;
    @Operation(summary = "축제 매칭 신청 확인하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "축제 매칭 신청 확인 성공", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "축제 매칭 신청 확인 실패", content = {@Content(mediaType = "application/json")})
    })
    @GetMapping("/match")
    public JsonBody<FormData> matchGet(@RequestParam String username) {
        return JsonBody.of(200, "축제 매칭 신청 확인 성공", festivalService.matchGet(username));
    }

    @Operation(summary = "축제 매칭 신청하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "축제 매칭 신청 성공"),
            @ApiResponse(responseCode = "400", description = "축제 매칭 신청 실패")
    })
    @PostMapping("/match")
    public JsonBody<String> matchPost(FormData formData) {
        return JsonBody.of(200, "축제 매칭 신청 성공", festivalService.matchPost(formData));
    }
    @Operation(summary = "축제 매칭 신청 취소하기")
    @DeleteMapping("/match")
    public JsonBody<String> matchDelete(@RequestParam String username) {
        return JsonBody.of(200, "축제 매칭 신청 취소 성공", festivalService.matchDelete(username));
    }
}
