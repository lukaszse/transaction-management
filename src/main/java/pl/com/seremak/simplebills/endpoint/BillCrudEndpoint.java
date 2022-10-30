package pl.com.seremak.simplebills.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.simplebills.dto.BillQueryParams;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.service.BillService;
import pl.com.seremak.simplebills.util.JwtExtractionHelper;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;


@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillCrudEndpoint {

    public static final String BILL_CREATION_RECEIVED_LOG_MESSAGE = "Received bill creation request from user={}";
    public static final String X_TOTAL_COUNT_HEADER = "x-total-count";
    public static final String BILL_CREATED_MESSAGE = "Bill for user={} with number={} successfully created";
    public static final String FIND_BILL_REQUEST_LOG_MESSAGE = "Find bill with number={} for user={}";
    public static final String FIND_BILLS_REQUEST_LOG_MESSAGE = "Find bills with category={} for user={}";
    public static final String BILL_FOUND_MESSAGE = "Bill with number={} for user={} successfully found.";
    public static final String BILLS_FETCHED_MESSAGE = "List of bills successfully fetched.";
    public static final String DELETE_BILL_REQUEST_LOG_MESSAGE = "Bill delete request for user={} and billNumber={}";
    public static final String DELETE_UPDATE_REQUEST_LOG_MESSAGE = "Bill update request for user={} and billNumber={}";
    public static final String BILL_DELETED_MESSAGE = "Bill for user={} with billNumber={} successfully deleted.";
    public static final String BILL_UPDATED_MESSAGE = "Bill with user={} and billNumber={} successfully update.";
    public static final String BILL_URI_PATTERN = "/bills/%s";
    public static final String ALL_CATEGORIES_LOG_MESSAGE = "All categories";
    private final BillService billService;
    private final JwtExtractionHelper jwtExtractionHelper;


    @PostMapping(produces = TEXT_PLAIN_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> createBill(@AuthenticationPrincipal final JwtAuthenticationToken principal,
                                                   @Valid @RequestBody final Bill bill) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info(BILL_CREATION_RECEIVED_LOG_MESSAGE, username);
        return billService.createBill(username, bill)
                .doOnSuccess(createdBill -> log.info(BILL_CREATED_MESSAGE, createdBill.getUser(), createdBill.getBillNumber()))
                .map(Bill::getBillNumber)
                .map(String::valueOf)
                .map(billNumber -> createResponse(billNumber, BILL_URI_PATTERN));
    }

    @GetMapping(value = "/{billNumber}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Bill>> findBillByBillNumber(final JwtAuthenticationToken principal,
                                                           @PathVariable final Integer billNumber) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info(FIND_BILL_REQUEST_LOG_MESSAGE, billNumber, username);
        return billService.findBillByBillNumber(username, billNumber)
                .doOnSuccess(bill -> log.info(BILL_FOUND_MESSAGE, bill.getBillNumber(), bill.getUser()))
                .map(ResponseEntity::ok);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Bill>>> findAllBillsByCategory(final JwtAuthenticationToken principal,
                                                                   final BillQueryParams params) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info(FIND_BILLS_REQUEST_LOG_MESSAGE, Optional.ofNullable(params.getCategory()).orElse(ALL_CATEGORIES_LOG_MESSAGE), username);
        return billService.findBillsByCategory(username, params)
                .doOnSuccess(__ -> log.info(BILLS_FETCHED_MESSAGE))
                .map(tuple -> ResponseEntity.ok().headers(prepareXTotalCountHeader(tuple.getT2())).body(tuple.getT1()));
    }

    @DeleteMapping(value = "/{billNumber}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> deleteBill(final JwtAuthenticationToken principal, @PathVariable final Integer billNumber) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info(DELETE_BILL_REQUEST_LOG_MESSAGE, username, billNumber);
        return billService.deleteBillByBillNumber(username, billNumber)
                .doOnSuccess(__ -> log.info(BILL_DELETED_MESSAGE, username, billNumber))
                .map(__ -> ResponseEntity.noContent().build());
    }

    @PatchMapping(value = "/{billNumber}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Bill>> updateBill(final JwtAuthenticationToken principal,
                                                 @RequestBody final Bill bill,
                                                 @PathVariable final Integer billNumber) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info(DELETE_UPDATE_REQUEST_LOG_MESSAGE, username, billNumber);
        return billService.updateBillNumber(username, bill)
                .doOnSuccess(theBill -> log.info(BILL_UPDATED_MESSAGE, theBill.getUser(), theBill.getBillNumber()))
                .map(ResponseEntity::ok);
    }

    private static ResponseEntity<String> createResponse(final String identifier,
                                                         final String uriPattern) {
        return ResponseEntity.created(URI.create(String.format(uriPattern, identifier)))
                .body(identifier);
    }

    private static HttpHeaders prepareXTotalCountHeader(final Long count) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(X_TOTAL_COUNT_HEADER, String.valueOf(count));
        headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, X_TOTAL_COUNT_HEADER);
        return headers;
    }
}
