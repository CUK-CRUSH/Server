package crush.myList.service;

import crush.myList.domain.autocomplete.service.AutocompleteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("AutocompleteAPI 테스트")
public class AutocompleteServiceTest {
    @InjectMocks
    private AutocompleteService autocompleteService;

    @Test
    @DisplayName("구글 자동완성 API 테스트")
    public void getAutocompleteGoogleTest() throws Exception {
        // given
        String language = AutocompleteService.KOREAN;
        String text = "뉴진스";

        // when
        List<String> list = autocompleteService.getAutocompleteGoogle(language, text);

        // then
        list.forEach(System.out::println);
    }
}
