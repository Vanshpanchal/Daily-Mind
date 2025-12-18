package spring.project.Daily.Mind.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import spring.project.Daily.Mind.entity.Users;
import spring.project.Daily.Mind.responseDTO.UserProfileDTO;

import java.util.List;

@Repository
public class UserRepositoryImpl {

    @Autowired
    MongoTemplate mongoTemplate;

    public List<Users> getUserFromSA() {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"));
        query.addCriteria(Criteria.where("SentimentAnalysis").is(true));
        List<Users> users = mongoTemplate.find(query, Users.class);
        return users;
    }

    public UserProfileDTO getMyProfile(String userName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(userName));
        Users user = mongoTemplate.findOne(query, Users.class);

        UserProfileDTO profile= new UserProfileDTO();

        assert user != null;
        profile.setName(user.getUsername());
        profile.setId(user.getId());
        profile.setEmail(user.getEmail());
        profile.setRoles(user.getRoles());
        profile.setSentimentAnalysis(user.isSentimentAnalysis());

        return profile;
    }
}

