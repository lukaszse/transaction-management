package pl.com.seremak.simplebills.intTest.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.com.seremak.simplebills.model.sequentialId.SequentialId;
import pl.com.seremak.simplebills.repository.SequentialIdRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class SequentialIdRepositoryIntegrationTest {

    private static final String USER = "user";

    @Autowired
    SequentialIdRepository repository;

    @BeforeEach
    void setup() {
        try {
            repository.deleteUser(USER).block();
        } catch (Exception e) {
            log.info("There was no user with name {} in database.", USER);
        }
    }

    @AfterEach
    void tearDown() {
        repository.deleteUser(USER).block();
    }

    @Test
    public void shouldReturnProperSequentialId() {

        // when
        Mono<String> sequentialId = repository.generateId(USER);

        // then
        StepVerifier
                .create(sequentialId)
                .assertNext(seq -> Assertions.assertEquals(seq, "1"))
                .expectComplete()
                .verify();
    }
}
