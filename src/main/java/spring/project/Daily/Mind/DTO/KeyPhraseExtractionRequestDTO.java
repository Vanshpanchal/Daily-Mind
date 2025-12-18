package spring.project.Daily.Mind.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyPhraseExtractionRequestDTO {

    private String kind;
    private Parameters parameters;
    private AnalysisInput analysisInput;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Parameters {
        private String modelVersion;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnalysisInput {
        private List<Document> documents;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Document {
        private String id;
        private String language;
        private String text;
    }
}

