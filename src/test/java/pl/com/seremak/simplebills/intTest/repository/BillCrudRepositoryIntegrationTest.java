package pl.com.seremak.simplebills.intTest.repository;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.repository.BillCrudRepository;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
public class BillCrudRepositoryIntegrationTest {

    @Autowired
    BillCrudRepository repository;

    @Test
    public void countDocuments() {

        repository.save(Bill.builder()
                .id("33")
                .description("descddd")
                .build()).block();

        long count = Optional.ofNullable(repository.count().block()).orElse(0L);
        assertEquals(count, 1L);
    }

    @Test
    public void fetchDocument() {

        repository.save(Bill.builder()
                .id("33")
                .description("desc")
                .build()).block();

        String desc = Objects.requireNonNull(repository.findAll().blockFirst()).getDescription();
        assertEquals("desc", desc);
    }
}
