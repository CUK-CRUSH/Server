package crush.myList.utils.repository;

import crush.myList.domain.member.entity.Member;

import java.util.Objects;
import java.util.Optional;

public class MemberTestRepository extends TestRepository<Member> {
    public Optional<Member> findByUsername(String username) {
        return list.stream()
                // null 제외
                .filter(Objects::nonNull)
                .filter(member -> member.getUsername().equals(username))
                .findFirst();
    }

    public Optional<Member> findByOauth2id(String oAuth2Id) {
        return list.stream()
                // null 제외
                .filter(Objects::nonNull)
                .filter(member -> member.getOauth2id().equals(oAuth2Id))
                .findFirst();
    }

    public Boolean existsByUsername(String username) {
        return list.stream()
                // null 제외
                .filter(Objects::nonNull)
                .anyMatch(member -> member.getUsername().equals(username));
    }
}
