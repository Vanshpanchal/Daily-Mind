package spring.project.Daily.Mind.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    String name;
    String id;
    List<String> roles;
    String email;
    Boolean sentimentAnalysis;
}
