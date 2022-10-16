package pl.com.seremak.simplebills.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.simplebills.dto.BillQueryParams;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.service.BillService;
import pl.com.seremak.simplebills.util.JwtUtils;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillCrudEndpoint {

    public static final String BILL_CREATION_RECEIVED_MESSAGE = "Received bill creation request from user={}";
    public static final String X_TOTAL_COUNT_HEADER = "XTotalCount";
    public static final String BILL_CREATED_MESSAGE = "Bill for user={} with number={} successfully created";
    public static final String FIND_BILL_REQUEST_MESSAGE = "Find bill with number={} for user={}";
    public static final String FIND_BILLS_REQUEST_MESSAGE = "Find bills with category={} for user={}";
    public static final String BILL_FOUND_MESSAGE = "Bill with number={} for user={} successfully found.";
    public static final String BILLS_FETCHED_MESSAGE = "List of bills successfully fetched.";
    public static final String DELETE_BILL_REQUEST_MESSAGE = "Bill delete request for user={} and billNumber={}";
    public static final String DELETE_UPDATE_REQUEST_MESSAGE = "Bill update request for user={} and billNumber={}";
    public static final String BILL_DELETED_MESSAGE = "Bill with user={} and billNumber={} successfully deleted.";
    public static final String BILL_UPDATED_MESSAGE = "Bill with user={} and billNumber={} successfully update.";
    public static final String BILL_URI_PATTERN = "/bills/%s";
    private final BillService service;


    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> createBill(final Principal principal, @Valid @RequestBody final Bill bill) {
        final String username = JwtUtils.extractUsername(principal);
        log.info(BILL_CREATION_RECEIVED_MESSAGE, username);
        return service.createBill(username, bill)
                .doOnSuccess(createdBill -> log.info(BILL_CREATED_MESSAGE, createdBill.getUser(), createdBill.getBillNumber()))
                .map(createdBill -> bill.getBillNumber())
                .map(billNumber -> createResponse(billNumber, BILL_URI_PATTERN));
    }

    @GetMapping(value = "/{billNumber}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Bill>> findBillByBillNumberForUser(final Principal principal, @PathVariable final String billNumber) {
        final String username = JwtUtils.extractUsername(principal);
        log.info(FIND_BILL_REQUEST_MESSAGE, billNumber, username);
        return service.findBillByBillNumberForUser(username, billNumber)
                .doOnSuccess(bill -> log.info(BILL_FOUND_MESSAGE, bill.getBillNumber(), bill.getUser()))
                .map(ResponseEntity::ok);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Bill>>> findAllBillsByCategory(final JwtAuthenticationToken principal, final BillQueryParams params) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String username = JwtUtils.extractUsername(authentication);
        log.info(FIND_BILLS_REQUEST_MESSAGE, Optional.ofNullable(params.getCategory()).orElse("All categories"), username);
        return service.findBillsByCategoryForUser(username, params)
                .doOnSuccess(__ -> log.info(BILLS_FETCHED_MESSAGE))
                .map(tuple -> ResponseEntity.ok().headers(prepareXTotalCountHeader(tuple.getT2())).body(tuple.getT1()));
    }

    @DeleteMapping(value = "/{billNumber}", produces = APPLICATION_JSON_VALUE)
    public Mono<Object> deleteBill(final Principal principal, @PathVariable final String billNumber) {
        final String username = JwtUtils.extractUsername(principal);
        log.info(DELETE_BILL_REQUEST_MESSAGE, username, billNumber);
        return service.deleteBillByBillNumberForUser(username, billNumber)
                .doOnSuccess(bill -> log.info(BILL_DELETED_MESSAGE, bill.getUser(), bill.getBillNumber()))
                .map(bill -> ResponseEntity.noContent());
    }

    @PatchMapping(value = "/{billNumber}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Bill>> updateBill(final Principal principal, @RequestBody final Bill bill, @PathVariable final String billNumber) {
        final String username = JwtUtils.extractUsername(principal);
        log.info(DELETE_UPDATE_REQUEST_MESSAGE, username, billNumber);
        return service.updateBillNumber(username, bill)
                .doOnSuccess(theBill -> log.info(BILL_UPDATED_MESSAGE, theBill.getUser(), theBill.getBillNumber()))
                .map(ResponseEntity::ok);
    }

    private static ResponseEntity<String> createResponse(final String identifier, final String uriPattern) {
        return ResponseEntity.created(URI.create(String.format(uriPattern, identifier)))
                .body(identifier);
    }

    private static HttpHeaders prepareXTotalCountHeader(final Long count) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(X_TOTAL_COUNT_HEADER, String.valueOf(count));
        return headers;
    }
}
