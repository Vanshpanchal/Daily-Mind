package spring.project.Daily.Mind.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.project.Daily.Mind.entity.Users;
import spring.project.Daily.Mind.repository.UserRepository;

@Service
public class OtpService {

    @Autowired
    MailService mailService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RedisService redisService;

    @Transactional
    public String sentOTP(String userName) {
        try {
            String otp = String.valueOf((int) ((Math.random() * 900000) + 100000));
            Users user = userRepository.findByusername(userName);
            String RedisKey = "OTP_" + user.getUsername();
            redisService.set(RedisKey, otp, 300L);
            mailService.sendMail(
                    "Your OTP for Daily Mind",
                    user.getEmail().toLowerCase(),
                    "Your One Time Password (OTP) for Daily Mind is: " + otp + ". It is valid for 5 minutes."
            );
            return "OTP sent successfully";
        } catch (Exception e) {

            return "Error sending OTP: " + e.getMessage();
        }
    }

    public String verifyOTP(String userName, String otp) {
        String RedisKey = "OTP_" + userName;
        String storedOtp = redisService.get(RedisKey, String.class);

        if (storedOtp != null && storedOtp.equals(otp)) {
            redisService.set(RedisKey, true, 250L);
            return "OTP verified successfully";
        } else {
            return "Invalid or expired OTP";
        }
    }
}
