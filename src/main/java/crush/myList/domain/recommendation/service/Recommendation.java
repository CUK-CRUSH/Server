package crush.myList.domain.recommendation.service;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.recommendation.dto.RecommendationDto;

import java.util.List;

public interface Recommendation {
    List<RecommendationDto.Response> getRecommendation(SecurityMember member);
}
