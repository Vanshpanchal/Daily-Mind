package spring.project.Daily.Mind.responseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class WeatherResponse {

    private Current current;


    @Getter
    @Setter
    public class Current {
        public int temperature;
        @JsonProperty("weather_descriptions")
        public ArrayList<String> weatherDescriptions;
        public int pressure;
        public int precip;
        public int humidity;
        public int cloudcover;
        public int feelslike;
    }


}
