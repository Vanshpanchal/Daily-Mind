package spring.project.Daily.Mind.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import spring.project.Daily.Mind.entity.Users;
import spring.project.Daily.Mind.repository.UserRepository;
import spring.project.Daily.Mind.repository.UserRepositoryImpl;
import spring.project.Daily.Mind.responseDTO.UserProfileDTO;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserRepositoryImpl userRepositoryImpl;

    @Autowired
    RedisService redisService;

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    // Creation or explicit password change helper (expects raw password)
    public void saveuser(Users users) {
        users.setPassword(PASSWORD_ENCODER.encode(users.getPassword()));
        users.setRoles(Arrays.asList("USER"));
        userRepository.save(users);
    }

    public void updateUserPreservePassword(Users users) {
        // Ensure roles are not accidentally null
        if (users.getRoles() == null || users.getRoles().isEmpty()) {
            users.setRoles(Arrays.asList("USER"));
        }
        userRepository.save(users);
    }

    public Users fetchuser(String username) {
        return userRepository.findByusername(username);
    }

    public Optional<Users> findById(ObjectId Id) {
        return userRepository.findById(Id);
    }

    public boolean deleteById(ObjectId Id) {
        userRepository.deleteById(Id);
        return true;
    }

    public Users findByUserName(String user) {
        return userRepository.findByusername(user);
    }


    public UserProfileDTO myProfile(String user) {
        return userRepositoryImpl.getMyProfile(user);
    }

    public List<Users> findAllUsers() {
        return userRepository.findAll();
    }

    public Users updateSentimentAnalysis(String username) {
        Users user = userRepository.findByusername(username);

        user.setSentimentAnalysis(!user.isSentimentAnalysis());
        userRepository.save(user);
        return user;
    }

    public String updatePassword(String username, String password) {
        Boolean isOtpVerified = redisService.get("OTP_" + username, Boolean.class);
        if (isOtpVerified == null || !isOtpVerified) {
            return "OTP not verified";
        } else {
            redisService.delete("OTP_" + username);
            Users user = userRepository.findByusername(username);
            user.setPassword(password);
            userRepository.save(user);
            return "Password Changed Successfully";
        }
    }
}
