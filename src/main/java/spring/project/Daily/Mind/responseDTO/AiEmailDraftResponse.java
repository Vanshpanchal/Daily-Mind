package spring.project.Daily.Mind.responseDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)

public class AiEmailDraftResponse {

    private List<Candidate> candidates;

    // Getter for candidates
    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    // Getter with null checks
    public String getText() {
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("Gemini API returned no candidates in response");
        }
        if (candidates.get(0).content == null) {
            throw new RuntimeException("Gemini API returned null content");
        }
        if (candidates.get(0).content.parts == null || candidates.get(0).content.parts.isEmpty()) {
            throw new RuntimeException("Gemini API returned no parts in content");
        }
        if (candidates.get(0).content.parts.get(0).text == null) {
            throw new RuntimeException("Gemini API returned null text");
        }
        return candidates.get(0).content.parts.get(0).text;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {
        public Content content;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        public List<Part> parts;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {
        public String text;
    }
}


