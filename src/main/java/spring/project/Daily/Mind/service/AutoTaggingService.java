package spring.project.Daily.Mind.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import spring.project.Daily.Mind.DTO.GeminiAiRequestDTO;
import spring.project.Daily.Mind.DTO.KeyPhraseExtractionRequestDTO;
import spring.project.Daily.Mind.cache.AppCache;
import spring.project.Daily.Mind.responseDTO.AiEmailDraftResponse;
import spring.project.Daily.Mind.responseDTO.KeyPhraseExtractionResponse;
import spring.project.Daily.Mind.utility.Constants;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static spring.project.Daily.Mind.utility.Constants.GEMINI_API;

@Slf4j
@Service
public class AutoTaggingService {
    Logger logger;
    @Autowired
    RestTemplate restTemplate;

    @Value("${sentiment.analysis.key}")
    String APIKEY;


    @Autowired
    private AppCache appCache;

    @Autowired
    private RedisService redisService;

    String KeyPhrasesExtractPrompt = """
            You are an assistant that selects the most dominant and meaningful key phrases.
            
            Given the original content and the extracted key phrases, analyze their importance, frequency, relevance, and emotional/cognitive weight.
            
            Your task:
            - Select a minimum of 2 and a maximum of 3 key phrases.
            - Choose only the most powerful, overarching, and representative phrases.
            - Avoid duplicates, overly specific items, or low-impact phrases.
            - Return only the selected key phrases as a simple , separated of strings.
            
            Content:
            %s
            
            Extracted Key Phrases:
            %s
            
            Respond in this format:
            phrase1, phrase2, phrase3
            """;

    public List<String> AutoTaggingJournalEntry(String content) {

        KeyPhraseExtractionRequestDTO reqBody = new KeyPhraseExtractionRequestDTO("KeyPhraseExtraction", new KeyPhraseExtractionRequestDTO.Parameters("latest"), new KeyPhraseExtractionRequestDTO.AnalysisInput(List.of(new KeyPhraseExtractionRequestDTO.Document("1", "en", content))));

        HttpHeaders headers = new HttpHeaders();
        headers.set(Constants.OCP_APIM_SUBSCRIPTION_KEY.getValue(), APIKEY);
        headers.set("Content-Type", "application/json");

        HttpEntity<?> entity = new HttpEntity<>(reqBody, headers);

        KeyPhraseExtractionResponse response = restTemplate.exchange(appCache.APPCACHE.get(Constants.SENTIMENT_ANALYSIS.toString()), HttpMethod.POST, entity, KeyPhraseExtractionResponse.class).getBody();

        List<String> keyPhrases = response.getResults().getDocuments().get(0).getKeyPhrases();
        String output = GeminiAutoTagging(keyPhrases, content);
        List<String> list = Arrays.asList(output.split(", "));
        assert response != null;
        log.info(response.getResults().getDocuments().get(0).getKeyPhrases().toString());
//        redisService.set(userName + "weekly_sentimentAnalysis", response.getResults().documents().get(0).sentiment(), 25200L);
        return list;
    }

    public String GeminiAutoTagging(List<String> keyPhrases, String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        GeminiAiRequestDTO reqBody = new GeminiAiRequestDTO();

        String formattedPrompt = String.format(KeyPhrasesExtractPrompt, content, keyPhrases);
        reqBody.getContents().get(0).getParts().get(0).setText(formattedPrompt);
        HttpEntity<?> entity = new HttpEntity<>(reqBody, headers);


        AiEmailDraftResponse response = restTemplate.exchange(appCache.APPCACHE.get(GEMINI_API.toString()), HttpMethod.POST, entity, AiEmailDraftResponse.class).getBody();


        if (response == null) {
            throw new RuntimeException("Gemini API returned null response");
        }

        log.info("Gemini KeyPhrases", response.getText());


        return response.getText();
    }

}
