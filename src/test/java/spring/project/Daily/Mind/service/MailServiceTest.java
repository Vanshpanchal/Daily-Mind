package spring.project.Daily.Mind.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MailServiceTest {

    @Autowired
    MailService mailService;

    @Test
    public void mailSendTest() {
        mailService.sendMail(
                "Testing the Mail Sending",
                "acc.tempmail123@gmail.com",
                "Hello How are you?"
        );
    }
}
