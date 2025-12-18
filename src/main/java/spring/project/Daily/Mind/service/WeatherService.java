package spring.project.Daily.Mind.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import spring.project.Daily.Mind.cache.AppCache;
import spring.project.Daily.Mind.responseDTO.WeatherResponse;
import spring.project.Daily.Mind.utility.Constants;


@Component
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;
    private final static String ENDPOINT = "http://api.weatherstack.com/current?access_key=APIKEY&query=CITY";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    private AppCache appCache;

    public WeatherResponse getWeather(String city) {

        WeatherResponse weatherResponse = redisService.get("weather_of_" + city, WeatherResponse.class);
        if (weatherResponse != null) {
            return weatherResponse;
        } else {
            String finalURl = appCache.APPCACHE.get(Constants.WEATHER_API.toString()).replace("APIKEY", apiKey).replace("CITY", city);
            ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalURl, HttpMethod.GET, null, WeatherResponse.class);
            WeatherResponse body = response.getBody();
            redisService.set("weather_of_" + city, body, 300L);

            return response.getBody();
        }
    }

}
