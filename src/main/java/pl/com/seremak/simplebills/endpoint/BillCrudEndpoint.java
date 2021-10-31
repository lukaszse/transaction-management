package pl.com.seremak.simplebills.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.simplebills.endpoint.dto.BillQueryParams;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.service.BillCrudService;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillCrudEndpoint {

    public static final String BILL_CREATION_RECEIVED_MESSAGE = "Received bill creation request from user={}";
    public static final String X_TOTAL_COUNT_HEADER = "XTotalCount";
    @Value("${hello}")
    private String hello;
    private final BillCrudService service;


    @GetMapping(value = "/hello", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> sayHello(final Mono<Principal> principal) {
        return principal
                .map(Principal::getName)
                .map(name -> hello.formatted(name))
                .map(ResponseEntity::ok);
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> createBill(final Mono<Principal> principal, @Valid @RequestBody final Bill bill) {
        return principal
                .map(Principal::getName)
                .doOnSuccess(userName -> log.info(BILL_CREATION_RECEIVED_MESSAGE, userName))
                .flatMap(userName -> service.createBill(userName, bill))
                .map(this::createResponse);
    }

    @GetMapping(value = "/{billNumber}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Bill>> findBillById(final Mono<Principal> principal, @PathVariable final String billNumber) {
        return principal
                .map(Principal::getName)
                .flatMap(userName -> service.findBillByBillNumberForUser(userName, billNumber))
                .map(ResponseEntity::ok);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Bill>>> findAllBillsByCategory(final Mono<Principal> principal, BillQueryParams params) {

        return principal
                .map(Principal::getName)
                .flatMap(userName -> service.findBillsByCategoryForUser(userName, params))
                .map(tuple -> ResponseEntity.ok().headers(prepareXTotalCountHeader(tuple.getT2())).body(tuple.getT1()));
    }

    @DeleteMapping(value = "/{billNumber}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> deleteBill(final Mono<Principal> principal, @PathVariable final String billNumber) {
        return principal
                .map(Principal::getName)
                .flatMap(userName -> service.deleteBillByBillNumberForUser(userName, billNumber))
                .map(this::createResponse);
    }

    @PatchMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> updateBill(final Mono<Principal> principal, @RequestBody final Bill bill, @PathVariable final String id) {
        bill.setBillNumber(id);
        return principal
                .map(Principal::getName)
                .flatMap(userName -> service.updateBillByBillNumberForUser(userName, bill))
                .map(ResponseEntity::ok);
    }

    private ResponseEntity<String> createResponse(final String id) {
        return ResponseEntity.created(URI.create(String.format("/bills/%s", id)))
                .body(id);
    }

    private HttpHeaders prepareXTotalCountHeader(final Long count) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_TOTAL_COUNT_HEADER, String.valueOf(count));
        return headers;
    }
}
