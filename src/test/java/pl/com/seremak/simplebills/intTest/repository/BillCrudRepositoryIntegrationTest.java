package pl.com.seremak.simplebills.intTest.repository;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.repository.BillCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.com.seremak.simplebills.intTest.repository.RepositoryTestData.*;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class BillCrudRepositoryIntegrationTest {

    @Autowired
    public BillCrudRepository repository;

    @BeforeEach
    public void setup() {
        try {
            repository.deleteAll().block();
        } catch (Exception e) {
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
        repository.save(prepareBill("2")).block();

        // when
        Mono<Bill> monoBill = repository.findById("2");

        // then
        StepVerifier
                .create(monoBill)
                .assertNext(bill -> {
                    assertEquals(GROCERY_CATEGORY, bill.getCategory());
                    assertEquals(BIEDRONKA_SHOPPING, bill.getDescription());
                })
                .expectComplete()
                .verify();
    }


    @Test
    public void fetchAllDocuments() {

        // given
        IntStream.range(11, 21)
                .forEach(i -> repository.save(prepareBill(String.valueOf(i))).block());

        // when
        Flux<Bill> fluxBills = repository.findAll();
        List<Bill> bills = repository.findAll().collectList().block();

        // then
        StepVerifier
                .create(fluxBills)
                .assertNext(bill -> assertEquals(GROCERY_CATEGORY, bill.getCategory()))
                .expectNextCount(9)
                .expectComplete()
                .verify();
    }
}
