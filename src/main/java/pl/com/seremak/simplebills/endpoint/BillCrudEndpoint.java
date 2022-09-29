package pl.com.seremak.simplebills.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.simplebills.dto.BillQueryParams;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.service.BillCrudService;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.net.http.HttpResponse;
import java.security.Principal;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillCrudEndpoint {

    public static final String BILL_CREATION_RECEIVED_MESSAGE = "Received bill creation request from user={}";
    public static final String X_TOTAL_COUNT_HEADER = "XTotalCount";
    public static final String SAY_HELLO_REQUEST_MESSAGE = "Say hello request received for user={}";
    public static final String HELLO_SUCCESSFULLY_SAID_MESSAGE = "Hello successfully said :)";
    public static final String CREATE_BILL_REQUEST_MESSAGE = "Create bill request for user={} received";
    public static final String BILL_CREATED_MESSAGE = "Bill for user={} with number={} successfully created";
    public static final String FIND_BILL_REQUEST_MESSAGE = "Find bill with number={} for user={}";
    public static final String FIND_BILLS_REQUEST_MESSAGE = "Find bills with category={} for user={}";
    public static final String BILL_FOUND_MESSAGE = "Bill with number={} for user={} successfully found.";
    public static final String BILLS_FETCHED_MESSAGE = "List of bills successfully fetched.";
    public static final String DELETE_BILL_REQUEST_MESSAGE = "Bill delete request for user={} and billNumber={}";
    public static final String DELETE_UPDATE_REQUEST_MESSAGE = "Bill update request for user={} and billNumber={}";
    public static final String BILL_DELETED_MESSAGE = "Bill with user={} and billNumber={} successfully deleted.";
    public static final String BILL_UPDATED_MESSAGE = "Bill with user={} and billNumber={} successfully update.";

    @Value("${hello}")
    private String hello;
    private final BillCrudService service;


    @GetMapping(value = "/hello", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> sayHello(final Mono<Principal> principal) {
        return principal
                .doOnEach(userName -> log.info(SAY_HELLO_REQUEST_MESSAGE, userName))
                .map(Principal::getName)
                .map(name -> hello.formatted(name))
                .doOnSuccess(__ -> log.info(HELLO_SUCCESSFULLY_SAID_MESSAGE))
                .map(response -> ResponseEntity.ok().headers(prepareXTotalCountHeader(1L)).body(response));
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> createBill(final Mono<Principal> principal, @Valid @RequestBody final Bill bill) {
        return principal
                .doOnEach(userName -> log.info(CREATE_BILL_REQUEST_MESSAGE, userName))
                .map(Principal::getName)
                .doOnSuccess(userName -> log.info(BILL_CREATION_RECEIVED_MESSAGE, userName))
                .flatMap(userName -> service.createBill(userName, bill))
                .doOnSuccess(theBill -> log.info(BILL_CREATED_MESSAGE, theBill.getUser(), theBill.getBillNumber()))
                .map(theBill -> bill.getBillNumber())
                .map(this::createResponse);
    }

    @GetMapping(value = "/{billNumber}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Bill>> findBillByBillNumberForUser(final Mono<Principal> principal, @PathVariable final String billNumber) {
        return principal
                .map(Principal::getName)
                .doOnEach(userName -> log.info(FIND_BILL_REQUEST_MESSAGE, billNumber, userName))
                .flatMap(userName -> service.findBillByBillNumberForUser(userName, billNumber))
                .doOnSuccess(bill -> log.info(BILL_FOUND_MESSAGE, bill.getBillNumber(), bill.getUser()))
                .map(ResponseEntity::ok);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Bill>>> findAllBillsByCategory(final Mono<Principal> principal, BillQueryParams params) {

        return principal
                .map(Principal::getName)
                .doOnEach(userName -> log.info(FIND_BILLS_REQUEST_MESSAGE, params.getCategory(), userName))
                .flatMap(userName -> service.findBillsByCategoryForUser(userName, params))
                .doOnSuccess(__ -> log.info(BILLS_FETCHED_MESSAGE))
                .map(tuple -> ResponseEntity.ok().headers(prepareXTotalCountHeader(tuple.getT2())).body(tuple.getT1()));
    }

    @DeleteMapping(value = "/{billNumber}", produces = APPLICATION_JSON_VALUE)
    public Mono<Object> deleteBill(final Mono<Principal> principal, @PathVariable final String billNumber) {
        return principal
                .map(Principal::getName)
                .doOnEach(userName -> log.info(DELETE_BILL_REQUEST_MESSAGE, userName, billNumber))
                .flatMap(userName -> service.deleteBillByBillNumberForUser(userName, billNumber))
                .doOnSuccess(bill -> log.info(BILL_DELETED_MESSAGE, bill.getUser(), bill.getBillNumber()))
                .map(bill -> ResponseEntity.noContent());
    }

    @PatchMapping(value = "/{billNumber}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Bill>> updateBill(final Mono<Principal> principal, @RequestBody final Bill bill, @PathVariable final String billNumber) {
        bill.setBillNumber(billNumber);
        return principal
                .map(Principal::getName)
                .doOnEach(userName -> log.info(DELETE_UPDATE_REQUEST_MESSAGE, userName, billNumber))
                .flatMap(userName -> service.updateBillByBillNumberForUser(userName, bill))
                .doOnSuccess(theBill -> log.info(BILL_UPDATED_MESSAGE, theBill.getUser(), theBill.getBillNumber()))
                .map(ResponseEntity::ok);
    }

    private ResponseEntity<String> createResponse(final String billNumber) {
        return ResponseEntity.created(URI.create(String.format("/bills/%s", billNumber)))
                .body(billNumber);
    }

    private HttpHeaders prepareXTotalCountHeader(final Long count) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_TOTAL_COUNT_HEADER, String.valueOf(count));
        headers.setAccessControlAllowOrigin("*");
        return headers;
    }
}
