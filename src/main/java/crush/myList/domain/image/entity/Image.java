package crush.myList.domain.image.entity;

import crush.myList.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "image")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Image extends BaseEntity {
    @Column(name = "original_name", nullable = false)
    String originalName;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "extension", nullable = false, length = 30)
    String extension;

    @Column(name = "uuid", nullable = false, length = 50)
    String uuid;

    @Column(name = "url", nullable = false)
    String url;
}
