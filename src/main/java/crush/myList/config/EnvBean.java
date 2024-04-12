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

    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.password}")
    private String adminPassword;

    @Value("${youtube.api-key1}")
    private String youtubeApiKey1;
    @Value("${youtube.api-key2}")
    private String youtubeApiKey2;
    @Value("${youtube.api-key3}")
    private String youtubeApiKey3;
    @Value("${youtube.api-key4}")
    private String youtubeApiKey4;
    @Value("${youtube.api-key5}")
    private String youtubeApiKey5;
    @Value("${youtube.api-key6}")
    private String youtubeApiKey6;
    @Value("${youtube.api-key7}")
    private String youtubeApiKey7;
    @Value("${youtube.api-key8}")
    private String youtubeApiKey8;
    @Value("${youtube.api-key9}")
    private String youtubeApiKey9;
    @Value("${youtube.api-key10}")
    private String youtubeApiKey10;
    @Value("${youtube.api-key11}")
    private String youtubeApiKey11;
    @Value("${youtube.api-key12}")
    private String youtubeApiKey12;
    @Value("${youtube.api-key13}")
    private String youtubeApiKey13;
    @Value("${youtube.api-key14}")
    private String youtubeApiKey14;

    private int youtubeKeyMaxSize = 14;
}
