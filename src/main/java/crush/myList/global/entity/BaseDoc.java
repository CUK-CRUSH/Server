package crush.myList.global.entity;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseDoc {
    @Id
    private String id;
}
