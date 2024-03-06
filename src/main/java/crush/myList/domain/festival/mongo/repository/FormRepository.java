package crush.myList.domain.festival.mongo.repository;

import crush.myList.domain.festival.mongo.document.Form;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormRepository extends MongoRepository<Form, String> {
}
