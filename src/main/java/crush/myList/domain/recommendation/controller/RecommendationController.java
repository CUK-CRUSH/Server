package crush.myList.domain.recommendation.controller;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.playlist.dto.PlaylistDto;
import crush.myList.domain.recommendation.service.RandomRecommendationService;
import crush.myList.global.dto.JsonBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Recommendation", description = "플레이리스트 추천 API")
@RestController
@Slf4j(topic = "RecommendationController")
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendation")
public class RecommendationController {
    private final RandomRecommendationService recommendationService;

    @Operation(summary = "추천 음악 조회하기")
    @GetMapping("")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 음악 조회 성공"),
            @ApiResponse(responseCode = "404", description = "추천 음악 조회 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<List<PlaylistDto.Response>> getRecommendation(@AuthenticationPrincipal SecurityMember member) {
        return JsonBody.of(
                200,
                "추천 음악 조회 성공",
                recommendationService.getRecommendation(member)
        );
    }
}
