package spring.project.Daily.Mind.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import spring.project.Daily.Mind.DTO.AiVoiceRequestDTO;
import spring.project.Daily.Mind.entity.JournalEntry;
import spring.project.Daily.Mind.entity.Users;
import spring.project.Daily.Mind.service.AiService;
import spring.project.Daily.Mind.service.JournalEntryService;
import spring.project.Daily.Mind.service.RedisService;
import spring.project.Daily.Mind.service.UserService;
import spring.project.Daily.Mind.utility.ApiResponse;

import java.util.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@RestController
@RequestMapping("/journal")
@Tag(name = "Journal", description = "Create, read, update and delete journal entries")
public class JournalEntryController {

    @Autowired
    JournalEntryService journalEntryService;

    @Autowired
    UserService userService;

    @Autowired
    AiService aiVoiceService;

    @Autowired
    RedisService redisService;


    @GetMapping()
    @Operation(summary = "List current user's journal entries",
            description = "Returns the list of journal entries belonging to the currently authenticated user.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Journal entries fetched successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No Journal entries Found")
            },
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    public ResponseEntity<ApiResponse<Page<JournalEntry>>> getAllJournalEntriesOfUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        size = Math.min(size, 50);
        Pageable pageable =
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));

        Page<JournalEntry> data = journalEntryService.findByUserName(username, pageable);
        if (data.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("No Journal entries Found ", 404));
        }
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Journal entries fetched successfully", data));


    }

    @PostMapping("/")
    @Operation(summary = "Create a new journal entry",
            description = "Create a new journal entry for the authenticated user. Provide title and content in the request body.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JournalEntry.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Journal Entry Added Successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request")
            },
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    public ResponseEntity<ApiResponse<JournalEntry>> addJournalEntry(@org.springframework.web.bind.annotation.RequestBody JournalEntry entry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            journalEntryService.saveEntry(entry, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Journal Entry Added Successfully ", entry));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Error " + e.getMessage(), 404));
        }
    }

    @PostMapping("/id/{myId}")
    @Operation(summary = "Get a journal entry by id",
            description = "Return a single journal entry by its ObjectId. Only returns the entry if it belongs to the authenticated user.",
            parameters = {@Parameter(name = "myId", description = "ObjectId of the journal entry", required = true)},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Journal Found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JournalEntry.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Journal Not Found")
            },
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    public ResponseEntity<ApiResponse<JournalEntry>> getJournalEntryById(@PathVariable ObjectId myId) {
        Optional<JournalEntry> entry = journalEntryService.findById(myId);

        return entry.map(journalEntry -> ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Journal Found", journalEntry))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Journal Not Found ", 404)));

    }

    @DeleteMapping("/id/{myId}")
    @Operation(summary = "Delete a journal entry",
            description = "Deletes the specified journal entry if it belongs to the authenticated user.",
            parameters = {@Parameter(name = "myId", description = "ObjectId of the journal entry to delete", required = true)},
            responses = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Journal Deleted Successfully"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not Found")},
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    public ResponseEntity<ApiResponse<?>> deleteJournalEntryById(@PathVariable ObjectId myId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            boolean response = journalEntryService.deleteById(myId, username);
            if (response) {
                return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Journal Deleted Successfully ", "Done"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Not Found", 404));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Error  " + e.getMessage(), 404));
        }
    }

    @PutMapping("/id/{myId}")
    @Operation(summary = "Update a journal entry",
            description = "Updates the journal entry identified by id. Only provided fields will be updated.",
            parameters = {@Parameter(name = "myId", description = "ObjectId of the journal entry to update", required = true)},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(implementation = JournalEntry.class))),
            responses = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Journal Updated Successfully"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Journal not found or not owned by user")},
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    public ResponseEntity<ApiResponse<?>> updateJournalEntryById(@PathVariable ObjectId myId, @RequestBody JournalEntry entry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            JournalEntry journalEntry = journalEntryService.UpdateById(myId, entry, username);
            if (journalEntry == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Journal not found or not owned by user", 404));
            }
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Journal Updated Successfully ", journalEntry));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Error  " + e.getMessage(), 404));
        }
    }

    @GetMapping(path = "/{id}/audio", produces = "audio/mpeg")
    @Operation(summary = "Speak a journal entry (audio)",
            description = "Generates and returns an audio (MP3) reading of the specified journal entry's title and content.",
            parameters = {@Parameter(name = "id", description = "ObjectId of the journal entry", required = true)},
            responses = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "MP3 audio returned", content = @Content(mediaType = "audio/mpeg")), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Entry not found")},
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    public ResponseEntity<?> speakJournalEntry(@PathVariable ObjectId id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JournalEntry entry = journalEntryService.getJournalContentById(id, authentication.getName());
        AiVoiceRequestDTO reqBody = new AiVoiceRequestDTO();
        reqBody.setText(
                "Let me read your journal entry. " +
                        "The title is: " + entry.getTitle() + ". " +
                        "Here is what you wrote: " + entry.getContent()
        );
        byte[] audio = aiVoiceService.getAiVoice(reqBody);
        return ResponseEntity.ok().header("Content-Type", "audio/mpeg").body(audio);
    }


    @GetMapping("/filter/")
    public ResponseEntity<?> filterJournalListonDate(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            List<JournalEntry> journalEntryList = journalEntryService.DateFilter(authentication.getName(), startDate, endDate);

            return ResponseEntity.status(HttpStatus.OK).body(
                    ApiResponse.success(" ", journalEntryList)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error("Error  " + e.getMessage(), 400)
            );
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchJournalEntries(@RequestParam String query) {
        try {
            if (query == null || query.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        ApiResponse.error("Query parameter cannot be empty", 400)
                );
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Users user = userService.findByUserName(username);
            JournalEntry[] entries = redisService.get(
                    "search::" + username + query,
                    JournalEntry[].class
            );
            if (entries != null) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        ApiResponse.success("Done", Arrays.asList(entries))
                );
            }
            List<JournalEntry> journalEntryList = journalEntryService.Search(query, user);
            redisService.set("search::" + username + query, journalEntryList.toArray(new JournalEntry[0]), 3600L);
            return ResponseEntity.status(HttpStatus.OK).body(
                    ApiResponse.success("Done", journalEntryList)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.error("Error  " + e.getMessage(), 400)
            );
        }
    }

}
