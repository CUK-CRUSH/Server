package crush.myList.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LimitConstants {
    PLAYLIST_LIMIT(4),
    MUSIC_LIMIT(9),

    PLAYLIST_PAGE_SIZE(9),
    LIKED_PLAYLIST_PAGE_SIZE(8),

    LIKED_MEMBER_PAGE_SIZE(15),
    GUESTBOOK_PAGE_SIZE(15),

    SEARCH_PLAYLIST_PAGE_SIZE(8),
    SEARCH_MEMBER_PAGE_SIZE(10),

    PLAYLIST_RECOMMENDATION_SIZE(7),

    PLAYLIST_RANKING_SIZE(10),
    MEMBER_RANKING_SIZE(10);

    private final int limit;
}
