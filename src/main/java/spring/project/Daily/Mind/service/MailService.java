package spring.project.Daily.Mind.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailService {

    @Autowired
    JavaMailSender javaMailSender;

    public void sendMail(String subject, String receiver, String message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();

            mail.setTo(receiver);
            mail.setSubject(subject);
            mail.setText(message);
            javaMailSender.send(mail);
        } catch (Exception e) {
            log.error("Error : {}", e.getMessage());
        }

    }
    public void sendMail1(String subject, String receiver, String HtmlMessage) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mail = new MimeMessageHelper(message);

            mail.setTo(receiver);
            mail.setSubject(subject);
            mail.setText(HtmlMessage,true);
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("Error : {}", e.getMessage());
        }

    }
}
