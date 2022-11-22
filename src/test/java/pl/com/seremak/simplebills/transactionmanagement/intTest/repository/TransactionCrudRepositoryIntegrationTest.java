package pl.com.seremak.simplebills.transactionmanagement.intTest.repository;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.com.seremak.simplebills.model.Transaction;
import pl.com.seremak.simplebills.transactionmanagement.repository.TransactionCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class TransactionCrudRepositoryIntegrationTest {

    @Autowired
    public TransactionCrudRepository repository;

    @BeforeEach
    public void setup() {
        try {
            repository.deleteAll().block();
        } catch (final Exception e) {
            log.info("Some errors while Bills deleting occurred");
        }
    }

    @AfterEach
    public void teardown() {
        repository.deleteAll().block();
        log.info("Deleting all bills");
    }


    @Test
    public void fetchDocument() {

        // given
        repository.save(RepositoryTestData.prepareBill(2)).block();

        // when
        final Mono<Transaction> monoBill = repository.findByUserAndTransactionNumber(RepositoryTestData.TEST_USER, 2);

        // then
        StepVerifier
                .create(monoBill)
                .assertNext(bill -> {
                    assertEquals(RepositoryTestData.GROCERY_CATEGORY, bill.getCategory());
                    assertEquals(RepositoryTestData.BIEDRONKA_SHOPPING, bill.getDescription());
                })
                .expectComplete()
                .verify();
    }


    @Test
    public void fetchAllDocuments() {

        // given
        IntStream.range(11, 21)
                .forEach(i -> repository.save(RepositoryTestData.prepareBill(i)).block());

        // when
        final Flux<Transaction> fluxBills = repository.findAll();
        final List<Transaction> transactions = repository.findAll().collectList().block();

        // then
        StepVerifier
                .create(fluxBills)
                .assertNext(bill -> assertEquals(RepositoryTestData.GROCERY_CATEGORY, bill.getCategory()))
                .expectNextCount(9)
                .expectComplete()
                .verify();
    }
}
