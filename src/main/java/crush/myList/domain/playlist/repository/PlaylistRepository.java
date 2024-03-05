package crush.myList.domain.playlist.repository;

import crush.myList.domain.member.entity.Member;
import crush.myList.domain.playlist.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long>, JpaSpecificationExecutor<Playlist> {
    List<Playlist> findAll(@Nullable Specification<Playlist> spec);

    Long countByMember(Member member);

    List<Playlist> findAllByMemberOrderByCreatedDateDesc(Member member);

    Page<Playlist> findByNameContaining(String q, Pageable pageable);

    @Query("SELECT p FROM Playlist p WHERE p.id IN (" +
            "SELECT pl.playlist.id FROM PlaylistLike pl " +
            "GROUP BY pl.playlist.id " +
            "ORDER BY COUNT(pl) DESC" +
            ") ORDER BY SIZE(p.likes) DESC")
    Page<Playlist> findTopPlaylists(Pageable pageable);
}
