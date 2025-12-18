package spring.project.Daily.Mind.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import spring.project.Daily.Mind.DTO.SentimentAnalysisRequestDTO;
import spring.project.Daily.Mind.cache.AppCache;
import spring.project.Daily.Mind.responseDTO.SentimentAnalysisResponse;
import spring.project.Daily.Mind.utility.Constants;

@Slf4j
@Service
public class SentimentAnalysisService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${sentiment.analysis.key}")
    String APIKEY;


    @Autowired
    private AppCache appCache;

    @Autowired
    private RedisService redisService;


    public String getSentimentAnalysis(String content, String userName) {

        String sentiment = redisService.get(userName + "weekly_sentimentAnalysis", String.class);
        if (sentiment != null) {
            return sentiment;
        }
        SentimentAnalysisRequestDTO reqBody = new SentimentAnalysisRequestDTO();
        reqBody.getAnalysisInput().getDocuments().get(0).setText(content);

        HttpHeaders headers = new HttpHeaders();
        headers.set(Constants.OCP_APIM_SUBSCRIPTION_KEY.getValue(), APIKEY);
        headers.set("Content-Type", "application/json");

        HttpEntity<?> entity = new HttpEntity<>(reqBody, headers);

        SentimentAnalysisResponse response =
                restTemplate.exchange(appCache.APPCACHE.get(Constants.SENTIMENT_ANALYSIS.toString()), HttpMethod.POST, entity, SentimentAnalysisResponse.class)
                        .getBody();

        assert response != null;
        redisService.set(userName + "weekly_sentimentAnalysis", response.getResults().documents().get(0).sentiment(), 25200L);
        return response.getResults().documents().get(0).sentiment();
    }

}
