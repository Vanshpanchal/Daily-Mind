package spring.project.Daily.Mind.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "config_collection")
@Data
@NoArgsConstructor
public class ConfigCollectionEntity {
    private String key;
    private String value;
}
