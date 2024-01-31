package crush.myList.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class EnvBean {
    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;
    @Value("${spring.cloud.gcp.storage.project-id}")
    private String projectId;
    @Value("${spring.cloud.gcp.storage.key-name}")
    private String keyName;

    @Value("${spring.security.oauth2.client.login-page}")
    private String loginPage;

    @Value("${react.uri}")
    private String reactUri;

    @Value("${perspective.api-key}")
    private String perspectiveApiKey;
}
