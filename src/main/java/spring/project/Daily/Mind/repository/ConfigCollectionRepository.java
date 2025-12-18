package spring.project.Daily.Mind.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import spring.project.Daily.Mind.entity.ConfigCollectionEntity;

public interface ConfigCollectionRepository extends MongoRepository<ConfigCollectionEntity, ObjectId> {

}
