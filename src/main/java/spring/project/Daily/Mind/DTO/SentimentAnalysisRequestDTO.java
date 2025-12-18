package spring.project.Daily.Mind.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class SentimentAnalysisRequestDTO {

    public String kind = "SentimentAnalysis";
    public Parameters parameters = new Parameters();       // FIXED
    public AnalysisInput analysisInput = new AnalysisInput(); // FIXED

    @Getter
    @Setter
    public class AnalysisInput {
        public ArrayList<Document> documents = new ArrayList<>(); // FIXED

        public AnalysisInput() {
            documents.add(new Document()); // FIXED → ensures index 0 exists
        }
    }

    @Getter
    @Setter
    public class Document {
        public String id = "1";
        public String language = "en";
        public String text;
    }

    @Getter
    @Setter
    public class Parameters {
        public String modelVersion = "latest";
        public String opinionMining = "True";
    }
}
