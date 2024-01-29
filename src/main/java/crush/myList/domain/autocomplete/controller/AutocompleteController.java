package crush.myList.domain.autocomplete.controller;

import crush.myList.domain.autocomplete.service.Autocomplete;
import crush.myList.domain.autocomplete.service.GoogleAutocomplete;
import crush.myList.global.dto.JsonBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Autocomplete", description = "자동완성 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/autocomplete")
public class AutocompleteController {
    private final Autocomplete autocomplete;
    private final static int DEFAULT_MAX_SIZE = 4;

    @Operation(summary = "구글 자동완성 API")
    @Parameter(name = "language", description = "언어 설정", required = true, examples ={
            @ExampleObject(name = "한국어", value = GoogleAutocomplete.KOREAN),
            @ExampleObject(name = "영어", value = GoogleAutocomplete.ENGLISH)
    })
    @Parameter(name = "text", description = "검색어", required = true)
    @GetMapping("/google")
    public JsonBody<List<String>> getAutocompleteGoogle(@RequestParam String language, @RequestParam String text) {
        return JsonBody.of(200, "구글 자동완성 결과", autocomplete.getAutocomplete(language, text, DEFAULT_MAX_SIZE));
    }
}
