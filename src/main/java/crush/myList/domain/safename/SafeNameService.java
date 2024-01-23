package crush.myList.domain.safename;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j(topic = "SafeNameService")
@RequiredArgsConstructor
public class SafeNameService {
    public boolean isSafeName(String name) {
        return containsNoBadWord(name) && containsNoSpecialCharacterExcept(name, List.of('_', '.'));
    }

    public boolean containsNoBadWord(String name) {
        return true;
    }

    public boolean containsNoSpecialCharacterExcept(String name, List<Character> except) {
        for (char c : name.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && !except.contains(c)) {
                return false;
            }
        }

        return true;
    }
}
