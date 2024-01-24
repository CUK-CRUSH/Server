package crush.myList.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleName {
    TEMPORARY("ROLE_TEMPORARY"), USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    private final String value;
}
