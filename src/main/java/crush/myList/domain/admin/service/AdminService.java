package crush.myList.domain.admin.service;

import crush.myList.domain.festival.dto.FormData;
import crush.myList.domain.festival.mongo.document.Form;
import crush.myList.domain.festival.mongo.repository.FormRepository;
import crush.myList.domain.member.dto.MemberDto;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.music.mongo.repository.MusicRepository;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import crush.myList.domain.search.dto.PlaylistDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AdminService {
    private final MemberRepository memberRepository;
    private final PlaylistRepository playlistRepository;
    private final FormRepository formRepository;

    public List<MemberDto> getAllMembersOrderByCreatedDateDesc() {
        return MemberDto.of(memberRepository.findAllByOrderByCreatedDateDesc());
    }

    public List<PlaylistDto> getAllPlaylistsOrderByCreatedDateDesc() {
        return PlaylistDto.of(playlistRepository.findAllByOrderByCreatedDateDesc());
    }

    public List<FormData> getAllFormsOrderByCreatedDateDesc() {
        return FormData.of(formRepository.findAllByOrderByCreatedDateDesc());
    }
}
