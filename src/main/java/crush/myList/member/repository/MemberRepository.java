package crush.myList.member.repository;

import crush.myList.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);

    Optional<Member> findByOauth2id(String oAuth2Id);

    boolean existsByOauth2id(String oauth2Id);
}
