package crush.myList.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvBean {
    @Value("${spring.cloud.gcp.storage.bucket}")
    public String BUCKET_NAME;
    @Value("${spring.cloud.gcp.storage.project-id}")
    public String PROJECT_ID;
}
