package spring.project.Daily.Mind.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "AiVoiceRequest", description = "Request payload for voice synthesis API")
public class AiVoiceRequestDTO {
    @Schema(description = "Text to be synthesized into voice", example = "Hello there, this is a test.")
    private String text = "hello how are you";
    @JsonProperty("model_id")
    @Schema(description = "Voice model identifier", example = "eleven_multilingual_v2")
    private String modelId = "eleven_multilingual_v2";
}
