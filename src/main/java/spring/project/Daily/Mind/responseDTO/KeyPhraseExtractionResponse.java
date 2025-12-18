package spring.project.Daily.Mind.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyPhraseExtractionResponse {

    private String kind;
    private Results results;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Results {
        private List<Document> documents;
        private List<Object> errors;       // Azure returns empty array → keep generic
        private String modelVersion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {
        private String id;
        private List<String> keyPhrases;
        private List<Object> warnings;     // Empty list → generic object OK
    }
}
