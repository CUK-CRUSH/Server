package crush.myList.domain.autocomplete.service;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public interface Autocomplete {
    List<String> getAutocomplete(String language, String text, int maxSize) throws ResponseStatusException;
}
