package crush.myList.etc;

import crush.myList.domain.autocomplete.AutocompleteAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("AutocompleteAPI 테스트")
public class AutocompleteAPITest {
    @InjectMocks
    private AutocompleteAPI autocompleteAPI;

    @Test
    @DisplayName("구글 자동완성 API 테스트")
    public void getAutocompleteGoogleTest() throws Exception {
        // given
        String language = AutocompleteAPI.KOREAN;
        String text = "뉴진스";

        // when
        String res = AutocompleteAPI.getAutocompleteGoogle(language, text);
        List<String> list = AutocompleteAPI.getList(res);

        // then
        list.forEach(System.out::println);
    }
}
