package spring.project.Daily.Mind.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "User", description = "Application user information")
public class Users {

    @Id
    @Schema(description = "MongoDB ObjectId for the user")
    private ObjectId id;

    @Indexed(unique = true)
    @NonNull
    @Schema(description = "Unique username of the user", example = "alice@example.com")
    private String username;

    @NonNull
    @Schema(description = "(Hashed) password. When creating/updating provide plain password. Not returned in responses in production.")
    private String password;

    @DBRef
    @Schema(description = "List of the user's journal entries")
    private List<JournalEntry> journalEntries = new ArrayList<>();

    @Schema(description = "Roles assigned to the user")
    private List<String> roles;

    @Schema(description = "Whether sentiment analysis is enabled for the user")
    private boolean SentimentAnalysis;

    @Schema(description = "User's email address")
    private String email;

    @Schema(description = "Account creation date", type = "string", format = "date-time")
    private LocalDateTime date;


    public String getId() {
        return id == null ? null : id.toHexString();
    }
}
