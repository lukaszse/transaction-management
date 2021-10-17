package pl.com.seremak.simplebills.intTest.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import pl.com.seremak.simplebills.model.bill.Bill;
import pl.com.seremak.simplebills.repository.BillCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.com.seremak.simplebills.intTest.repository.RepositoryTestData.*;

@DataMongoTest
public class BillCrudRepositoryIntegrationTest {

    @Autowired
    public BillCrudRepository repository;

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
