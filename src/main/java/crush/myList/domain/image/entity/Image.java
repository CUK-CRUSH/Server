package crush.myList.domain.image.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "image")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_name", nullable = false)
    String originalName;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "extension", nullable = false, length = 30)
    String extension;

    @Column(name = "uuid", nullable = false, length = 30)
    String uuid;

    @Column(name = "url", nullable = false)
    String url;
}
