package spring.project.Daily.Mind.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spring.project.Daily.Mind.repository.UserRepository;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    UserRepository userRepository;

    @Disabled
    @Test
    public void Test1() {
        Assertions.assertNotNull(userRepository.findByusername("ram"));
    }

//    For Multiple Parameter
    @Disabled
    @ParameterizedTest
    @CsvSource({
            "kartik",
            "ram",
            "admin"

    })
    public void Test2(String username) {
        Assertions.assertNotNull(userRepository.findByusername(username), "Failed :" + username);
    }

//    For Single Parameter
    @Disabled
    @ParameterizedTest
    @ValueSource(strings = {
            "kartik",
            "ram",
            "admin"

    })
    public void Test3(String username) {
        Assertions.assertNotNull(userRepository.findByusername(username), "Failed :" + username);
    }


/*
 JUnit 5 lifecycle annotations:


 @BeforeAll
 - Runs once before any test methods in the class.
 - Use for expensive/shared setup.
 - Must be static unless the class uses @TestInstance(Lifecycle.PER_CLASS).

 @AfterAll
 - Runs once after all test methods in the class.
 - Use for global cleanup.
 - Must be static unless the class uses @TestInstance(Lifecycle.PER_CLASS).

 @BeforeEach
 - Runs before each @Test method.
 - Use to prepare a fresh state per test.
 - Instance (non-static) method.

 @AfterEach
 - Runs after each @Test method.
 - Use to clean up per-test resources.
 - Instance (non-static) method.

 Execution order:
 @BeforeAll → @BeforeEach → @Test → @AfterEach → @AfterAll
*/



}
