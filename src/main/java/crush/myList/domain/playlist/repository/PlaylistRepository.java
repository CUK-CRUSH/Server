package crush.myList.domain.playlist.repository;

import crush.myList.domain.member.entity.Member;
import crush.myList.domain.playlist.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long>, JpaSpecificationExecutor<Playlist> {
    List<Playlist> findAll(@Nullable Specification<Playlist> spec);
    Long countByMember(Member member);
    List<Playlist> findAllByMemberOrderByCreatedDateDesc(Member member);

    List<Playlist> findByName(String name);

    ArrayList<Playlist> findAllByMemberIsNotAndNameIsNot(Member member, String name);

    ArrayList<Playlist> findAllByNameIsNot(String name);

    Page<Playlist> findByNameContaining(String q, Pageable pageable);
}
