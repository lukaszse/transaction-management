package pl.com.seremak.simplebills.intTest.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.repository.BillCrudRepository;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
public class CrudRepositoryIntegrationTest {

    @Autowired
    BillCrudRepository repository;


    @Test
    public void shouldGetAppropriateNumberOfBills() {

        Bill bill = new Bill(1, Instant.now(), "opis", "kategoria", null);
        var insert = repository.save(bill).block();
        long count = Optional.ofNullable(repository.count().block()).orElse(0L);

        assertEquals(1L, count);
    }
}
