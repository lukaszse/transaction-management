package pl.com.seremak.simplebills.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import pl.com.seremak.simplebills.model.SequentialId;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SequentialIdService {

    private static final int STARTING_ID = 1;
    public static final String ID_FIELD = "_id";
    public static final String SEQUENTIAL_ID_FIELD = "sequentialId";
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<String> generateId(final String user) {
        return mongoTemplate.findAndModify(
                prepareFindUserQuery(user),
                prepareSequentialIdIncrementUpdate(),
                new FindAndModifyOptions().returnNew(true),
                SequentialId.class)
                .map(SequentialId::getSequentialId)
                .map(String::valueOf)
                .switchIfEmpty(insertFirstSequentialId(user));
        // todo implement error handling
    }

    public Mono<Void> deleteUser(final String user) {
        return mongoTemplate.findAndRemove(prepareFindUserQuery(user), SequentialId.class)
                .switchIfEmpty(Mono.error(new Exception())) //todo add error handling
                .then();
    }

    private Mono<String> insertFirstSequentialId(final String user) {
        return mongoTemplate.insert(buildFirstSequentialId(user))
                .map(SequentialId::getSequentialId)
                .map(String::valueOf);
    }

    private Query prepareFindUserQuery(final String user) {
        return new Query()
                .addCriteria(Criteria.where(ID_FIELD).is(user));
    }

    private Update prepareSequentialIdIncrementUpdate() {
        return new Update()
                .inc(SEQUENTIAL_ID_FIELD, 1);
    }

    private SequentialId buildFirstSequentialId(final String user) {
        return new SequentialId(user, STARTING_ID);
    }
}
