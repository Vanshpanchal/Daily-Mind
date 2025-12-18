package spring.project.Daily.Mind.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import spring.project.Daily.Mind.entity.Users;
import spring.project.Daily.Mind.responseDTO.UserProfileDTO;


public interface UserRepository extends MongoRepository<Users, ObjectId> {

    Users findByusername(String username);

    void deleteByUsername(String username);



}
