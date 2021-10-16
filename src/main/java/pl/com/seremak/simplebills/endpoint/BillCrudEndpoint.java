package pl.com.seremak.simplebills.endpoint;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.service.BillCrudService;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/bill")
@RequiredArgsConstructor
public class BillCrudEndpoint {

    @Value("${hello}")
    private String hello;
    private final BillCrudService service;


    @GetMapping("/hello")
    public Mono<ResponseEntity<String>> sayHello() {
        return Mono.just(hello)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<String>> createBill(final Bill bill) {
        return service.createBill(bill)
                .map(this::createResponse);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Bill>> findBillById(@PathVariable String id) {
        return service.findBillById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Mono<ResponseEntity<List<Bill>>> findAllBills() {
        return service.findAllBills()
                .map(ResponseEntity::ok);
    }

    @GetMapping("{category}")
    public Mono<ResponseEntity<List<Bill>>> findAllBillsByCategory(@PathVariable final String category) {
        return service.findBillsByCategory(category)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> deleteBill(@PathVariable String id) {
        return service.deleteBillById(id)
                .map(this::createResponse);
    }

    private ResponseEntity<String> createResponse(final String id) {
        return ResponseEntity.created(URI.create(String.format("/bill/%s", id)))
                .body(id);
    }
}
