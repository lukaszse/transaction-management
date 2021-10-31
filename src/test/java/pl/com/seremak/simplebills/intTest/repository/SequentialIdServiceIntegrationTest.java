package pl.com.seremak.simplebills.intTest.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.com.seremak.simplebills.service.SequentialIdService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class SequentialIdServiceIntegrationTest {

    private static final String USER = "user";

    @Autowired
    SequentialIdService repository;

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
