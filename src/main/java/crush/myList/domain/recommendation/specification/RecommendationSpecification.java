package crush.myList.domain.recommendation.specification;

import crush.myList.domain.member.entity.Member;
import crush.myList.domain.playlist.entity.Playlist;
import org.springframework.data.jpa.domain.Specification;

public class RecommendationSpecification {
    public static Specification<Playlist> withoutTitle(String title) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("name"), title);
    }

    public static Specification<Playlist> withoutMember(Member member) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("member"), member);
    }

    public static Specification<Playlist> withoutPlaylistId(Long playlistId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("id"), playlistId);
    }

    public static Specification<Playlist> withImage() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("image"));
    }
}
