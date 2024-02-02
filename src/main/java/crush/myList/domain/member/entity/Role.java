package crush.myList.domain.member.entity;

import crush.myList.domain.member.enums.RoleName;
import crush.myList.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "role")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {
    @Column(name = "name", nullable = false, unique = true)
    private RoleName name;
}
