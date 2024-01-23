package crush.myList.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleName {
    TEMPORARY_USER("TEMPORARY_USER"), USER("USER"), ADMIN("ADMIN");

    private final String value;
}
