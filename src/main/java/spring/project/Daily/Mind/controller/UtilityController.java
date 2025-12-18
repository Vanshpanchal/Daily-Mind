package spring.project.Daily.Mind.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import spring.project.Daily.Mind.cache.AppCache;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/utils")
@Tag(name = "Utility", description = "Utility and maintenance endpoints")
public class UtilityController {

    @Autowired
    private AppCache appCache;

    @Value("${MasterKey}")
    private String masterKey;

    @GetMapping("/config")
    @Operation(summary = "Reload configuration",
            description = "Reloads application configuration into cache. Requires X-ADMIN-KEY header matching server master key.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Configuration reloaded successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized: Invalid X-ADMIN-KEY")
            },
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    public ResponseEntity<String> configKeys(
            @RequestHeader(value = "X-ADMIN-KEY", required = false) String adminKey
    ) {
        // Optional: Validate custom header
        if (adminKey == null || !adminKey.equals(masterKey)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid X-ADMIN-KEY");
        }

        appCache.init();

        return ResponseEntity.ok()
                .header("X-CONFIG-STATUS", "SUCCESS")
                .body("Configuration reloaded successfully");
    }
}
