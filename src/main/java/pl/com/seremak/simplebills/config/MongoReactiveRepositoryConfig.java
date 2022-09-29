package pl.com.seremak.simplebills.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@Slf4j
@Configuration
public class MongoReactiveRepositoryConfig  {

    @Value("${spring.data.mongodb.database}")
    private String simpleBillDatabase;

    @Value("${spring.data.mongodb.uri}")
    private String simpleBillsDatabaseUri;



    public @Bean ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(mongoClient(), simpleBillDatabase);
    }

    @Bean
    public MongoClient mongoClient() {
        log.info("Creating MongoDb client for URI: {}", simpleBillsDatabaseUri);
        return MongoClients.create(simpleBillsDatabaseUri);
    }
}