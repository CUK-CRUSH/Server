package crush.myList.service;

import crush.myList.domain.autocomplete.service.GoogleAutocomplete;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("AutocompleteAPI 테스트")
public class GoogleAutocompleteTest {
    @InjectMocks
    private GoogleAutocomplete googleAutocomplete;

    @Test
    @DisplayName("구글 자동완성 API 테스트")
    public void getAutocompleteGoogleTest() throws Exception {
        // given
        String language = GoogleAutocomplete.KOREAN;
        String text = "뉴진스";

        // when
        List<String> list = googleAutocomplete.getAutocomplete(language, text, 4);

        // then
        list.forEach(System.out::println);
    }

    @Test
    @DisplayName("이모지 테스트")
    public void etcTest() {
        String t = new String(Character.toChars(0x1F60A));
        String emoji = "😊";
        System.out.println(t);
        System.out.println(emoji);
        System.out.println(t.equals(emoji));

        String t2 = new String(Character.toChars(0x1F415));
        String emoji2 = "🐕";
        System.out.println(t2);
        System.out.println(emoji2);
        System.out.println(t2.equals(emoji2));
    }
}
