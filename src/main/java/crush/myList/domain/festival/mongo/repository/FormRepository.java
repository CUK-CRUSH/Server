package crush.myList.domain.festival.mongo.repository;

import crush.myList.domain.festival.mongo.document.Form;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormRepository extends MongoRepository<Form, String> {
    boolean existsByUsername(String username);

    Optional<Form> findByUsername(String username);

    void deleteByUsername(String username);

    List<Form> findAllByOrderByCreatedDateDesc();
}
