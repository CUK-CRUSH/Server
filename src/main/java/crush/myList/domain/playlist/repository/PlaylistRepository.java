package crush.myList.domain.playlist.repository;

import crush.myList.domain.member.entity.Member;
import crush.myList.domain.playlist.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findAllByMember(Member member);
    Long countByMember(Member member);
    List<Playlist> findAllByMemberOrderByCreatedDateDesc(Member member);

    List<Playlist> findByName(String name);

    @Query("select p from Playlist p where p.member != :member")
    ArrayList<Playlist> findAllExceptMember(Member member);
}
