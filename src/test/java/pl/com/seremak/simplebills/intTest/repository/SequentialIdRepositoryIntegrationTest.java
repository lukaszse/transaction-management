package pl.com.seremak.simplebills.intTest.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import pl.com.seremak.simplebills.repository.SequentialIdRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
public class SequentialIdRepositoryIntegrationTest {

    @Autowired
    public SequentialIdRepository sequentialIdRepository;

    @Test
    public void shouldReturnProperSequentialId() {

        // when
        Mono<String> sequentialId = sequentialIdRepository.generateId();

        // then
        StepVerifier
                .create(sequentialId)
                .assertNext(seq -> Assertions.assertEquals(seq, "1"))
                .expectComplete()
                .verify();
    }
}
