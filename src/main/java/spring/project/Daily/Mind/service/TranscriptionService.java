package spring.project.Daily.Mind.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
//import org.springframework.util.LinkedMultiValueMap;//
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class TranscriptionService {

    @Value("${appwrite.bucket.endpoint}")
    String APPWRITE_BUCKET_ENDPOINT;

    @Value("${X-Appwrite-Key}")
    String X_Appwrite_Key;

    @Value("${X-Appwrite-Project}")
    String X_Appwrite_Project;



    @Autowired
    RestTemplate restTemplate;

    public String uploadAudioFile(byte[] audioFileBytes) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Appwrite-Project", X_Appwrite_Project);
        headers.set("X-Appwrite-Key", X_Appwrite_Key);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("fileId", "unique()");
        body.add("file", new ByteArrayResource(audioFileBytes) {

            @Override
            public String getFilename() {
                return "audio.mp3";
            }

            @Override
            public long contentLength() {
                return audioFileBytes.length;
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(APPWRITE_BUCKET_ENDPOINT, requestEntity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to upload audio to Appwrite");
        }

        return (String) response.getBody().get("$id");
    }


}
