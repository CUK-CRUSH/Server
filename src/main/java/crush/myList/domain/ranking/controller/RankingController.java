package crush.myList.domain.ranking.controller;

import crush.myList.domain.member.dto.MemberDto;
import crush.myList.domain.playlist.dto.PlaylistDto;
import crush.myList.domain.ranking.service.DailyMemberRankingService;
import crush.myList.domain.ranking.service.DailyPlaylistRankingService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Ranking", description = "랭킹 관련 API 모두 보기")
@RestController
@Slf4j(topic = "RankingController")
@RequiredArgsConstructor
@RequestMapping("/api/v1/ranking")
public class RankingController {
    private final DailyPlaylistRankingService dailyPlaylistRankingService;
    private final DailyMemberRankingService dailyMemberRankingService;

    @Operation(summary = "일간 플레이리스트 랭킹 조회", tags = {"Ranking"})
    @GetMapping("/daily/playlists")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일간 플레이리스트 랭킹 조회 성공"),
            @ApiResponse(responseCode = "404", description = "일간 플레이리스트 랭킹 조회 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<List<PlaylistDto.Response>> getDailyRanking() {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "일간 플레이리스트 랭킹 조회 성공",
                dailyPlaylistRankingService.getRanking()
        );
    }

    @Operation(summary = "일간 유저 랭킹 조회", tags = {"Ranking"})
    @GetMapping("/daily/users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일간 유저 랭킹 조회 성공"),
            @ApiResponse(responseCode = "404", description = "일간 유저 랭킹 조회 실패", content = @Content(schema = @Schema(hidden = true)))
    })
    public JsonBody<List<MemberDto>> getDailyUserRanking() {
        return JsonBody.of(
                HttpStatus.OK.value(),
                "일간 유저 랭킹 조회 성공",
                dailyMemberRankingService.getRanking()
        );
    }
}
