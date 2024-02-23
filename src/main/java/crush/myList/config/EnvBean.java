package crush.myList.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class EnvBean {
    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    @Value("${spring.cloud.gcp.storage.bucket-dir}")
    private String bucketDir;

    @Value("${spring.cloud.gcp.storage.project-id}")
    private String projectId;

    @Value("${spring.cloud.gcp.storage.key-name}")
    private String keyName;

    @Value("${react.uri}")
    private String reactUri;

    @Value("${perspective.api-key}")
    private String perspectiveApiKey;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String mongoDatabase;
}
