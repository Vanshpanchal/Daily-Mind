package spring.project.Daily.Mind.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import spring.project.Daily.Mind.entity.Users;

import java.util.List;

@Slf4j
@SpringBootTest
public class UserRepositoryImplTest {

    @Autowired
    UserRepositoryImpl userRepository;

    @Test
    public void getUserFromSATest() {
        Assertions.assertNotNull(userRepository.getUserFromSA());
    }
}
