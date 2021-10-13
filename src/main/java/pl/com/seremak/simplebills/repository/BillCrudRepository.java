package pl.com.seremak.simplebills.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.com.seremak.simplebills.model.Bill;

@Repository
public interface BillCrudRepository extends ReactiveCrudRepository<Bill, String> {

}
