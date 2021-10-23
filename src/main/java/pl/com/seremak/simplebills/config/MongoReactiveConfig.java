package pl.com.seremak.simplebills.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.lang.NonNull;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "pl.com.seremak.simplebills.repository")
public class MongoReactiveConfig extends AbstractReactiveMongoConfiguration {

    public static final String SIMPLE_BILL_MONGO_DATABASE = "SimpleBill";

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @NonNull
    @Override
    protected String getDatabaseName() {
        return SIMPLE_BILL_MONGO_DATABASE;
    }
}