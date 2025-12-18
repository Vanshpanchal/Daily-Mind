package spring.project.Daily.Mind.service;

//import lombok.Value;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import spring.project.Daily.Mind.entity.Users;
import spring.project.Daily.Mind.repository.UserRepository;
import spring.project.Daily.Mind.utility.JwtUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class GoogleAuthService {

    // fixed property keys (removed accidental spaces) and added a configurable redirect URI
    @Value("${google.auth.client.id}")
    private String clientId;

    @Value("${google.auth.client.secret}")
    private String clientSecret;

    @Value("${google.auth.redirect-uri:https://developers.google.com/oauthplayground/}")
    private String redirectUri;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    JwtUtils jwtUtils;
    public String GoogleExchangeTokenWithAuth(String code) {
        try {
            String tokenEndpoint = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", "https://developers.google.com/oauthplayground");
            params.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            // use explicit typed HttpEntity and ResponseEntity for clarity
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

            ResponseEntity<Map<String, Object>> tokenResponse = restTemplate.exchange(
                    tokenEndpoint,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }
            );

            Map<String, Object> tokenBody = tokenResponse.getBody();
            if (tokenBody == null || !tokenBody.containsKey("id_token")) {
                log.error("Token response missing id_token: status={} body={}", tokenResponse.getStatusCode(), tokenBody);
                return "Error Occurred: invalid token response";
            }

            String idToken = Objects.toString(tokenBody.get("id_token"), null);
            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;

            ResponseEntity<Map<String, Object>> userInfoResponse = restTemplate.exchange(
                    userInfoUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }
            );

            Map<String, Object> userInfoBody = userInfoResponse.getBody();
            if (userInfoResponse.getStatusCode() == HttpStatus.OK && userInfoBody != null) {
                String email = Objects.toString(userInfoBody.get("email"), null);
                if (email == null) {
                    log.error("No email found in id_token info: {}", userInfoBody);
                    return "Error Occurred: no email in token info";
                }

                try {
                    userDetailsService.loadUserByUsername(email);
                } catch (Exception e) {
                    Users user = new Users();
                    user.setRoles(List.of("USER"));
                    user.setEmail(email);
                    user.setUsername(generateUsernameFromEmail(email));
                    user.setDate(LocalDateTime.now());
                    userRepository.save(user);
                }
                return jwtUtils.generateToken(email);

            } else {
                log.error("Failed to fetch token info: status={} body={}", userInfoResponse.getStatusCode(), userInfoBody);
                return "Error Occurred: failed to fetch token info";
            }
        } catch (Exception e) {
            log.error("in Exception Error exchanging Google token", e);
            return "Error Occurred";
        }
    }

    public String generateUsernameFromEmail(String email) {
        String base = email.split("@")[0].toLowerCase();

        base = base.replaceAll("[^a-zA-Z0-9]", "");

        return base;
    }
}
