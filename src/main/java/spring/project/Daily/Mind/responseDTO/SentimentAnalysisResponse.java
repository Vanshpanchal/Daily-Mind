package spring.project.Daily.Mind.responseDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)   // <-- This is important
public class SentimentAnalysisResponse {

    public String kind;
    public Results results;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record Results(
            java.util.List<Document> documents
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record Document(
            String id,
            String sentiment
    ) {}
}
