package spring.project.Daily.Mind.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import spring.project.Daily.Mind.DTO.GeminiAiRequestDTO;
import spring.project.Daily.Mind.entity.Users;
import spring.project.Daily.Mind.service.AiService;
import spring.project.Daily.Mind.service.GoogleAuthService;
import spring.project.Daily.Mind.service.SchedulingService;
import spring.project.Daily.Mind.service.UserService;
import spring.project.Daily.Mind.utility.ApiResponse;
import spring.project.Daily.Mind.utility.JwtUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/public")
@Slf4j
@Tag(name = "Public", description = "Authentication and public endpoints")
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private GoogleAuthService googleAuthService;


    @PostMapping("/signup")
    @Operation(summary = "Create a new user account",
            description = "Register a new user. Provide username and password in the request body.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(implementation = Users.class))),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request")
            }
    )
    public ResponseEntity<ApiResponse<List<Users>>> AddUsers(@RequestBody Users user) {
        try {
            user.setDate(LocalDateTime.now());
            userService.saveuser(user);

            List<Users> data = Collections.singletonList(user);

            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(" ", data));
        } catch (Exception e) {
            log.error("Exception {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.error("Error  " + e.getMessage(), 404));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate and receive JWT",
            description = "Authenticate with username and password to receive a JWT token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(implementation = Users.class))),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Login failed")
            }
    )
    public HttpEntity<ApiResponse<String>> Login(@RequestBody Users users) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(users.getUsername(), users.getPassword()));

            UserDetails userDetails = userDetailsService.loadUserByUsername(users.getUsername());
            String jwt = jwtUtils.generateToken(userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Login Done", jwt));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.error("Error  " + e.getMessage(), 404));
        }
    }

    @GetMapping("/healthcheck")
    @Operation(summary = "Health check",
            description = "Simple health check endpoint returning 'healthy'",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Application is healthy", content = @Content(mediaType = "text/plain"))
            }
    )
    public String healthCheck() {
        return "healthy";
    }

    @Autowired
    SchedulingService schedulingService;

    @Autowired
    AiService aiService;

    @GetMapping("/check")
    @Operation(summary = "Trigger scheduled weekly email job",
            description = "Triggers the weekly scheduled email job (used for testing)."
    )
    public void check() {
        schedulingService.SendWeeklySaMail();
    }

    @PostMapping("/gemini-test")
    @Operation(summary = "Test Gemini AI email draft",
            description = "Send a prompt string and receive a generated email draft from the AI service.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json"))
    )
    public ResponseEntity<String> testGemini(@RequestBody String prompt) {
        try {
            GeminiAiRequestDTO geminiAiRequestDTO = new GeminiAiRequestDTO();
            geminiAiRequestDTO.getContents().get(0).getParts().get(0).setText(prompt);

            String response = aiService.getAiEmailDraft(geminiAiRequestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/oauth/google/callback")
    @Operation(summary = "Google OAuth callback - exchange code for JWT",
            description = "Exchanges the authorization code returned by Google's OAuth flow for an application JWT. Provide the 'code' query parameter that Google returns after user consent.",
            parameters = {@Parameter(name = "code", description = "Authorization code from Google", required = true)},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Returns ApiResponse with JWT token",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
            }
    )
    public ResponseEntity<ApiResponse<Object>> googleCallback(@RequestParam String code) {
        try {
            String jwtToken = googleAuthService.GoogleExchangeTokenWithAuth(code);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ApiResponse.success("Done", jwtToken)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    ApiResponse.error("Error  " + e.getMessage(), 404)
            );
        }
    }
}
