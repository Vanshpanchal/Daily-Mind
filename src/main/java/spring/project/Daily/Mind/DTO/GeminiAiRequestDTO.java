package spring.project.Daily.Mind.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(name = "AiEmailDraft", description = "Request payload for AI email draft generation")
public class GeminiAiRequestDTO {

    private List<Content> contents = new ArrayList<>();

    public GeminiAiRequestDTO() {
        // Initialize 1 content with 1 empty part
        Content content = new Content();
        this.contents.add(content);
    }

    @Getter
    @Setter
    @Schema(name = "Content", description = "A content block containing parts")
    public static class Content {
        private List<Part> parts = new ArrayList<>();

        public Content() {
            // Add 1 empty part automatically
            parts.add(new Part());
        }
    }

    @Getter
    @Setter
    @Schema(name = "Part", description = "A part of content with text")
    public static class Part {
        @Schema(description = "Text for this part", example = "Please draft an email to...")
        private String text = ""; // always empty by default
    }
}