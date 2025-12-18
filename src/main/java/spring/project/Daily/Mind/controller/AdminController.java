package spring.project.Daily.Mind.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.project.Daily.Mind.entity.Users;
import spring.project.Daily.Mind.service.UserService;
import spring.project.Daily.Mind.utility.ApiResponse;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Administrative operations")
public class AdminController {

    @Autowired
    UserService userService;

    @GetMapping("/all-users")
    @Operation(summary = "List all users",
            description = "Returns all users (admin only).",
            responses = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fetched All users ", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))) },
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    public ResponseEntity<ApiResponse<?>> findAllUsers() {
        try {
            List<Users> allUsers = userService.findAllUsers();

            if (!allUsers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        ApiResponse.success("Fetched All users ", allUsers)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error("Error  " + e.getMessage(), 400)
            );
        }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("Error  ", 404)
        );
    }
}
