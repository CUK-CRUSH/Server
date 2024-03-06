package crush.myList.domain.ranking.service;

import crush.myList.domain.member.dto.MemberDto;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.playlist.repository.PlaylistLikeRepository;
import crush.myList.domain.ranking.entity.MemberRanking;
import crush.myList.domain.ranking.repository.MemberRankingRepository;
import crush.myList.global.enums.LimitConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j(topic = "DailyMemberRankingService")
@Transactional
@RequiredArgsConstructor
@EnableScheduling
public class DailyMemberRankingService implements RankingService<MemberDto> {
    private final MemberRankingRepository memberRankingRepository;
    private final MemberRepository memberRepository;
    private final PlaylistLikeRepository playlistLikeRepository;

    @PostConstruct
    @Override
    public void init() {
        updateRanking();
    }

    @Override
    public List<MemberDto> getRanking() {
        List<MemberRanking> memberRankings = memberRankingRepository.findAll();
        return convertToDtoList(memberRankings);
    }



    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateRanking() {
        log.info("일간 유저 랭킹 업데이트");
        memberRankingRepository.deleteAllInBatch();
        memberRankingRepository.flush();

        Pageable pageable = PageRequest.of(0, LimitConstants.MEMBER_RANKING_SIZE.getLimit());

        List<Member> bestMembers = memberRepository.findTopMembers(pageable).getContent();

        for (int i = 0; i < bestMembers.size(); i++) {
            Member member = bestMembers.get(i);
            memberRankingRepository.save(MemberRanking.builder()
                    .member(member)
                    .rank(i + 1)
                    .likeCount(playlistLikeRepository.countByMemberId(member.getId()))
                    .build()
            );
        }
    }

    private List<MemberDto> convertToDtoList(List<MemberRanking> memberRankings) {
        return memberRankings.stream()
                .map(this::convertToDto)
                .toList();
    }

    private MemberDto convertToDto(MemberRanking memberRanking) {
        if (memberRanking.getMember() == null) {
            return null;
        }
        return MemberDto.builder()
                .id(memberRanking.getMember().getId())
                .username(memberRanking.getMember().getUsername())
                .introduction(memberRanking.getMember().getIntroduction())
                .profileImageUrl(memberRanking.getMember().getProfileImage() == null ? null : memberRanking.getMember().getProfileImage().getUrl())
                .build();
    }
}
