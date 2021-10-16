package pl.com.seremak.simplebills.repository;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import pl.com.seremak.simplebills.model.sequentialId.SequentialId;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class CounterRepository {

    private static final String SEQUENTIAL_ID_COLLECTION_NAME = "SequentialId";
    private static final String SEQUENTIAL_ID_FIELD = "seq";
    public static final String FIXED_ID = "1";

    private final MongoClient mongoClient;
    private MongoDatabase database;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @PostConstruct
    public void init() {
        database = mongoClient.getDatabase(databaseName);
    }

    public Mono<String> generateId() {
        return Mono.from(getSequentialIdCollection()
                .findOneAndUpdate(Filters.eq(FIXED_ID), prepareIncrementExpression()))
                .map(SequentialId::getSeq)
                .map(String::valueOf)
                .switchIfEmpty(insertFirstSequentialId());
        // todo implement error handling
    }

    private Mono<String> insertFirstSequentialId() {
        return Mono.from(getSequentialIdCollection()
                .insertOne(buildFirstSequentialId()))
                .filter(InsertOneResult::wasAcknowledged)
                .map(insertOneResult -> String.valueOf(1));
        //todo - should throw exception if empty - use switchIfEmpty and throw custom Repository Exception
    }
    
    private Bson prepareIncrementExpression() {
        return Updates.inc(SEQUENTIAL_ID_FIELD, 1);
    }

    private SequentialId buildFirstSequentialId() {
        return new SequentialId(FIXED_ID, 0);
    }

    private MongoCollection<SequentialId> getSequentialIdCollection() {
        return database.getCollection(SEQUENTIAL_ID_COLLECTION_NAME, SequentialId.class);
    }
}
