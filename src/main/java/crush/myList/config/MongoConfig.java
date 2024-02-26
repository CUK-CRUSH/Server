package crush.myList.config;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@RequiredArgsConstructor
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "crush.myList.domain.*.mongo.repository")
public class MongoConfig {
//    private final EnvBean envBean;
//
//    @Bean
//    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
//        return new MongoTransactionManager(dbFactory);
//    }
//
//    @Bean
//    public MongoTemplate mongoTemplate() {
//        return new MongoTemplate(mongoClient(), getDatabaseName());
//    }
//
//    @Override
//    protected String getDatabaseName() {
//        return envBean.getMongoDatabase();
//    }
//
//    @Override
//    public MongoClient mongoClient() {
//        final ConnectionString connectionString = new ConnectionString(envBean.getMongoUri());
//        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
//                .applyConnectionString(connectionString)
//                .build();
//        return MongoClients.create(mongoClientSettings);
//    }
}