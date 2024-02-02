package crush.myList.utils.repository;

import crush.myList.domain.member.entity.Role;
import crush.myList.domain.member.enums.RoleName;

import java.util.Objects;
import java.util.Optional;

public class RoleTestRepository extends TestRepository<Role> {
    public RoleTestRepository() {
        Role temporaryRole = Role.builder()
                .id(1L)
                .name(RoleName.TEMPORARY)
                .build();
        Role userRole = Role.builder()
                .id(2L)
                .name(RoleName.USER)
                .build();
        Role adminRole = Role.builder()
                .id(3L)
                .name(RoleName.ADMIN)
                .build();
        list.add(temporaryRole);
        list.add(userRole);
        list.add(adminRole);
    }

    public Optional<Role> findByName(RoleName roleName) {
        return list.stream()
                // null 제외
                .filter(Objects::nonNull)
                .filter(role -> role.getName().equals(roleName))
                .findFirst();
    }
}
