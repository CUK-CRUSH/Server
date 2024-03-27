package crush.myList.domain.member.repository;

import crush.myList.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);

    Optional<Member> findByOauth2id(String oAuth2Id);

    Boolean existsByUsername(String username);

    Page<Member> findByUsernameContaining(String q, Pageable pageable);

    @Query("SELECT m FROM Member m " +
            "ORDER BY (SELECT COUNT(pl) FROM Playlist p JOIN p.likes pl WHERE p.member.id = m.id) DESC")
    Page<Member> findTopMembers(Pageable pageable);

    List<Member> findAllByOrderByCreatedDateDesc();
}
