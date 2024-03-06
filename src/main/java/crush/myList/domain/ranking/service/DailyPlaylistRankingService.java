package crush.myList.domain.ranking.service;

import crush.myList.domain.music.mongo.repository.MusicRepository;
import crush.myList.domain.playlist.dto.PlaylistDto;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.repository.PlaylistLikeRepository;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import crush.myList.domain.ranking.entity.PlaylistRanking;
import crush.myList.domain.ranking.repository.PlaylistRankingRepository;
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
@Slf4j(topic = "DailyPlaylistRankingService")
@Transactional
@RequiredArgsConstructor
@EnableScheduling
public class DailyPlaylistRankingService implements RankingService<PlaylistDto.Response> {
    private final PlaylistRankingRepository playlistRankingRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistLikeRepository playlistLikeRepository;
    private final MusicRepository musicRepository;

    @Override
    @PostConstruct
    public void init() {
        updateRanking();
    }

    @Override
    public List<PlaylistDto.Response> getRanking() {
        List<PlaylistRanking> playlistRankings = playlistRankingRepository.findAll();
        return convertToDtoList(playlistRankings);
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateRanking() {
        log.info("일간 플레이리스트 랭킹 업데이트");
        playlistRankingRepository.deleteAllInBatch();
        playlistRankingRepository.flush();

        Pageable pageable = PageRequest.of(0, LimitConstants.PLAYLIST_RANKING_SIZE.getLimit());

        List<Playlist> bestPlaylists = playlistRepository.findTopPlaylists(pageable).getContent();

        for (int i = 0; i < bestPlaylists.size(); i++) {
            Playlist playlist = bestPlaylists.get(i);
            playlistRankingRepository.save(PlaylistRanking.builder()
                    .playlist(playlist)
                    .rank(i + 1)
                    .likeCount(playlistLikeRepository.countByPlaylistId(playlist.getId()))
                    .build()
            );
        }
    }

    private List<PlaylistDto.Response> convertToDtoList(List<PlaylistRanking> playlistRankings) {
        return playlistRankings.stream()
                .map(this::convertToDto)
                .toList();
    }

    private PlaylistDto.Response convertToDto(PlaylistRanking playlistRanking) {
        if (playlistRanking.getPlaylist() == null) {
            return null;
        }
        return PlaylistDto.Response.builder()
                .id(playlistRanking.getPlaylist().getId())
                .playlistName(playlistRanking.getPlaylist().getName())
                .username(playlistRanking.getPlaylist().getMember().getUsername())
                .thumbnailUrl(playlistRanking.getPlaylist().getImage() == null ? null : playlistRanking.getPlaylist().getImage().getUrl())
                .numberOfMusics(musicRepository.countByPlaylistId(playlistRanking.getPlaylist().getId()))
                .likeCount(playlistRanking.getLikeCount())
                .build();
    }
}
