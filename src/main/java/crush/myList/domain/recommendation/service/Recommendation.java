package crush.myList.domain.recommendation.service;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.playlist.dto.PlaylistDto;

import java.util.List;

public interface Recommendation {
    List<PlaylistDto.Response> getRecommendation(SecurityMember member);
}
