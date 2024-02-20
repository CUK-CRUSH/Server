package crush.myList.domain.playlist.service;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.playlist.dto.GuestBookDto;
import crush.myList.domain.playlist.entity.GuestBook;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.repository.GuestBookRepository;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j(topic = "GuestBookService")
@Transactional
@RequiredArgsConstructor
public class GuestBookService {
    private final GuestBookRepository guestBookRepository;
    private final MemberRepository memberRepository;
    private final PlaylistRepository playlistRepository;

    public List<GuestBookDto> getGuestBooks(Long playlistId) {
        return guestBookRepository.findAllByPlaylistId(playlistId).stream()
                .map((guestBook) -> GuestBookDto.builder()
                        .id(guestBook.getId())
                        .username(guestBook.getMember().getUsername())
                        .content(guestBook.getContent())
                        .modifiedDate(guestBook.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .build()
                ).toList();
    }

    public GuestBookDto addGuestBook(SecurityMember member, Long playlistId, String content) {
        Member memberEntity = memberRepository.findByUsername(member.getUsername()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.")
        );
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다.")
        );
        GuestBook guestBook = guestBookRepository.save(GuestBook.builder()
                .member(memberEntity)
                .playlist(playlist)
                .content(content)
                .build()
        );
        return GuestBookDto.builder()
                .id(guestBook.getId())
                .username(guestBook.getMember().getUsername())
                .content(guestBook.getContent())
                .modifiedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .build();
    }

    public void deleteGuestBook(SecurityMember member, Long playlistId, Long guestbookId) {
        GuestBook guestBook = guestBookRepository.findById(guestbookId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "방명록을 찾을 수 없습니다.")
        );

        if (!guestBook.getMember().getUsername().equals(member.getUsername()) || !guestBook.getPlaylist().getId().equals(playlistId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "방명록을 삭제할 수 없습니다.");
        }

        guestBookRepository.delete(guestBook);
    }

    public GuestBookDto updateGuestBook(SecurityMember member, Long playlistId, Long guestbookId, String content) {
        GuestBook guestBook = guestBookRepository.findById(guestbookId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "방명록을 찾을 수 없습니다.")
        );

        if (!guestBook.getMember().getUsername().equals(member.getUsername()) || !guestBook.getPlaylist().getId().equals(playlistId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "방명록을 수정할 수 없습니다.");
        }

        guestBook.setContent(content);
        return GuestBookDto.builder()
                .id(guestBook.getId())
                .username(guestBook.getMember().getUsername())
                .content(guestBook.getContent())
                .modifiedDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .build();
    }
}