package spring.project.Daily.Mind.utility;

public enum Constants {

    WEATHER_API,
    ELEVANLABS_API,
    SENTIMENT_ANALYSIS,
    GEMINI_API,

    OCP_APIM_SUBSCRIPTION_KEY("Ocp-Apim-Subscription-Key");



    private final String value;

    // Constructor for simple constants (no extra value)
    Constants() {
        this.value = null;
    }

    // Constructor for constants with a value
    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
