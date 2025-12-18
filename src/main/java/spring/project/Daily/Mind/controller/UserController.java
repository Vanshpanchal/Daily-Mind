package spring.project.Daily.Mind.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import spring.project.Daily.Mind.entity.Users;
import spring.project.Daily.Mind.repository.UserRepository;
import spring.project.Daily.Mind.responseDTO.UserProfileDTO;
import spring.project.Daily.Mind.service.OtpService;
import spring.project.Daily.Mind.service.UserService;
import spring.project.Daily.Mind.service.WeatherService;
import spring.project.Daily.Mind.utility.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "Operations related to the authenticated user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    WeatherService weatherService;

    @Autowired
    OtpService Otpservice;


    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns profile information for the authenticated user", responses = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users fetched successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthenticated")}, security = {@SecurityRequirement(name = "BearerAuth")})
    public ResponseEntity<ApiResponse<?>> getUser() {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Unauthenticated", 401));
            }

            UserProfileDTO user = userService.myProfile(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("No Users Found ", 404));
            }
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Users fetched successfully", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Error  " + e.getMessage(), 400));
        }

    }


    @PutMapping()
    @Operation(summary = "Update current user", description = "Update the authenticated user's profile. Provide fields to update (username or password).", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(implementation = Users.class))), responses = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Updated Successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthenticated"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Username already in use")}, security = {@SecurityRequirement(name = "BearerAuth")})
    public ResponseEntity<ApiResponse<?>> updateUser(@RequestBody Users user) {
        // Validate authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Unauthenticated", 401));
        }

        // Load the current user from DB
        Users userInDb = userService.findByUserName(authentication.getName());
        if (userInDb == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("User not found", 404));
        }

        // Update username if provided and check uniqueness
        String newUsername = user.getUsername();
        if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(userInDb.getUsername())) {
            Users existing = userService.findByUserName(newUsername);
            if (existing != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("Username already in use", 409));
            }
            userInDb.setUsername(newUsername);
        }

        // Update password only if provided (and encode it). If not provided, keep existing password.
        String newPassword = user.getPassword();
        if (newPassword != null && !newPassword.isBlank()) {
            userInDb.setPassword(passwordEncoder.encode(newPassword));
        }

        // Persist changes using repository (returns saved entity)
        Users saved = userRepository.save(userInDb);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Updated Successfully ", saved));


    }

    @DeleteMapping
    @Operation(summary = "Delete current user", description = "Deletes the currently authenticated user's account", responses = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User Deleted Successfully")}, security = {@SecurityRequirement(name = "BearerAuth")})
    public ResponseEntity<ApiResponse<?>> deleteUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            userRepository.deleteByUsername(authentication.getName());
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("User Deleted Successfully ", ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Error  " + e.getMessage(), 400));
        }


    }

    @GetMapping("/greet")
    @Operation(summary = "Greet the user with weather info", description = "Returns a greeting that includes a simple weather 'feels like' value for Mumbai.", responses = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User Greeted Successfully")}, security = {@SecurityRequirement(name = "BearerAuth")})
    public ResponseEntity<ApiResponse<?>> greetUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String greet = " Hi " + authentication.getName() + " ,weather feels like " + weatherService.getWeather("Mumbai").getCurrent().feelslike;
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("User Greeted Successfully ", greet));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Error  " + e.getMessage(), 400));
        }


    }

    @GetMapping("/setSentimentAnalysis")
    @Operation(summary = "Toggle sentiment analysis",
            description = "Toggles the sentiment analysis feature for the authenticated user.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sentiment analysis setting updated"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
            },
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    public ResponseEntity<ApiResponse<String>> setSentimentAnalysis() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Users user = userService.updateSentimentAnalysis(authentication.getName());
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(" ", "Done"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.error("Error  " + e.getMessage(), 404));
        }
    }

    @PostMapping("/updatePassword")
    @Operation(summary = "Update password",
            description = "Updates the authenticated user's password.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "text/plain")),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password updated successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
            },
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    public ResponseEntity<ApiResponse<String>> updateUserPassword(@RequestBody String password) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            String data = userService.updatePassword(authentication.getName(), passwordEncoder.encode(password));
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(data, data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.error("Error  " + e.getMessage(), 404));
        }
    }

    @GetMapping("/resetPasswordOtp")
    @Operation(summary = "Request password reset OTP",
            description = "Sends an OTP to the authenticated user's email for password reset.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP sent successfully")
            },
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    public ResponseEntity<ApiResponse<Object>> ResetPasswordOtp() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String s = Otpservice.sentOTP(authentication.getName());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(s, null));
    }

    @GetMapping("/verifyOtp")
    @Operation(summary = "Verify password reset OTP",
            description = "Verifies the OTP provided by the user.",
            parameters = {@Parameter(name = "otp", description = "The OTP to verify", required = true)},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP verified successfully")
            },
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    public ResponseEntity<ApiResponse<Object>> VerifyOtp(@RequestParam String otp) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String s = Otpservice.verifyOTP(authentication.getName(), otp);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(s, null));
    }


}
