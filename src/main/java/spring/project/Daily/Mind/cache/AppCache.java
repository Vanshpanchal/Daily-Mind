package spring.project.Daily.Mind.cache;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spring.project.Daily.Mind.entity.ConfigCollectionEntity;
import spring.project.Daily.Mind.repository.ConfigCollectionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    @Autowired
    ConfigCollectionRepository configCollectionRepository;

    public Map<String, String> APPCACHE;

    @PostConstruct
    public void init() {
        APPCACHE = new HashMap<>();
        List<ConfigCollectionEntity> data = configCollectionRepository.findAll();
        for (ConfigCollectionEntity configCollectionEntity : data) {
            APPCACHE.put(configCollectionEntity.getKey(), configCollectionEntity.getValue());
        }
    }
}
