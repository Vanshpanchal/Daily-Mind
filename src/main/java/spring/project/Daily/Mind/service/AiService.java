package spring.project.Daily.Mind.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import spring.project.Daily.Mind.DTO.GeminiAiRequestDTO;
import spring.project.Daily.Mind.DTO.AiVoiceRequestDTO;
import spring.project.Daily.Mind.cache.AppCache;
import spring.project.Daily.Mind.responseDTO.AiEmailDraftResponse;

import static spring.project.Daily.Mind.utility.Constants.ELEVANLABS_API;
import static spring.project.Daily.Mind.utility.Constants.GEMINI_API;


@Component
public class AiService {

    private static final Logger log = LogManager.getLogger(AiService.class);
    @Autowired
    RestTemplate restTemplate;

    @Value("${elevanlabs.api.key}")
    private String ElevanLabsAPIKEY;

    @Value("${gemini.api.key}")
    private String GeminiAPIKEY;

    @Autowired
    private AppCache appCache;

    public byte[] getAiVoice(AiVoiceRequestDTO reqBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("xi-api-key", ElevanLabsAPIKEY);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>(reqBody, headers);
        return restTemplate.exchange(appCache.APPCACHE.get(ELEVANLABS_API.toString()), HttpMethod.POST, entity, byte[].class).getBody();
    }

    public String getAiEmailDraft(GeminiAiRequestDTO reqBody) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<>(reqBody, headers);

            log.info("Calling Gemini API with request: {}", reqBody);

            AiEmailDraftResponse response = restTemplate.exchange(appCache.APPCACHE.get(GEMINI_API.toString()), HttpMethod.POST, entity, AiEmailDraftResponse.class).getBody();


            if (response == null) {
                log.error("Gemini API returned null response");
                throw new RuntimeException("Gemini API returned null response");
            }

            log.info("Gemini API response received. Candidates: {}", response.getCandidates());

            return response.getText();
        } catch (Exception e) {
            log.error("Error in getAiEmailDraft: {}", e.getMessage(), e);
            throw e;
        }
    }
}


