package pl.com.seremak.simplebills.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.simplebills.commons.dto.http.TransactionDto;
import pl.com.seremak.simplebills.commons.dto.http.TransactionQueryParams;
import pl.com.seremak.simplebills.commons.model.Transaction;
import pl.com.seremak.simplebills.commons.utils.JwtExtractionHelper;
import pl.com.seremak.simplebills.service.TransactionService;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static pl.com.seremak.simplebills.commons.utils.EndpointUtils.prepareCreatedResponse;


@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionEndpoint {

    public static final String TRANSACTION_CREATION_RECEIVED_LOG_MESSAGE = "Received transaction creation request from user={}";
    public static final String X_TOTAL_COUNT_HEADER = "x-total-count";
    public static final String TRANSACTION_CREATED_MESSAGE = "Transaction for user={} with number={} successfully created";
    public static final String FIND_TRANSACTION_REQUEST_LOG_MESSAGE = "Find transaction with number={} for user={}";
    public static final String FIND_TRANSACTIONS_REQUEST_LOG_MESSAGE = "Find transactions with category={} for user={}";
    public static final String TRANSACTION_FOUND_MESSAGE = "Transaction with number={} for user={} successfully found.";
    public static final String TRANSACTIONS_FETCHED_MESSAGE = "List of transactions successfully fetched.";
    public static final String DELETE_TRANSACTION_REQUEST_LOG_MESSAGE = "Transaction delete request for user={} transactionNumber={}";
    public static final String DELETE_UPDATE_REQUEST_LOG_MESSAGE = "Transaction update request for user={} and transactionNumber={}";
    public static final String TRANSACTION_DELETED_MESSAGE = "Transaction for user={} with transactionNumber={} successfully deleted.";
    public static final String TRANSACTION_UPDATED_MESSAGE = "Transaction with user={} and transactionNumber={} successfully update.";
    public static final String TRANSACTION_URI_PATTERN = "/transactions/%s";
    public static final String ALL_CATEGORIES_LOG_MESSAGE = "All categories";
    private final TransactionService transactionService;


    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Transaction>> createTransaction(@AuthenticationPrincipal final JwtAuthenticationToken principal,
                                                               @Valid @RequestBody final TransactionDto transactionDto) {
        final String username = JwtExtractionHelper.extractUsername(principal);
        log.info(TRANSACTION_CREATION_RECEIVED_LOG_MESSAGE, username);
        return transactionService.createTransaction(username, transactionDto)
                .doOnSuccess(createTransaction -> log.info(TRANSACTION_CREATED_MESSAGE, createTransaction.getUser(), createTransaction.getTransactionNumber()))
                .map(transaction -> prepareCreatedResponse(TRANSACTION_URI_PATTERN, String.valueOf(transaction.getTransactionNumber()), transaction));
    }

    @GetMapping(value = "/{transactionNumber}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Transaction>> findTransactionByTransactionNumber(final JwtAuthenticationToken principal,
                                                                                @PathVariable final Integer transactionNumber) {
        final String username = JwtExtractionHelper.extractUsername(principal);
        log.info(FIND_TRANSACTION_REQUEST_LOG_MESSAGE, transactionNumber, username);
        return transactionService.findTransactionByTransactionNumber(username, transactionNumber)
                .doOnSuccess(transaction -> log.info(TRANSACTION_FOUND_MESSAGE, transaction.getTransactionNumber(), transaction.getUser()))
                .map(ResponseEntity::ok);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Transaction>>> findAllTransactionsByCategory(final JwtAuthenticationToken principal,
                                                                                 final TransactionQueryParams params) {
        final String username = JwtExtractionHelper.extractUsername(principal);
        log.info(FIND_TRANSACTIONS_REQUEST_LOG_MESSAGE, Optional.ofNullable(params.getCategory()).orElse(ALL_CATEGORIES_LOG_MESSAGE), username);
        return transactionService.findTransactionsByCategory(username, params)
                .doOnSuccess(__ -> log.info(TRANSACTIONS_FETCHED_MESSAGE))
                .map(tuple -> ResponseEntity.ok().headers(prepareXTotalCountHeader(tuple.getT2())).body(tuple.getT1()));
    }

    @DeleteMapping(value = "/{transactionNumber}")
    public Mono<ResponseEntity<Void>> deleteTransaction(final JwtAuthenticationToken principal, @PathVariable final Integer transactionNumber) {
        final String username = JwtExtractionHelper.extractUsername(principal);
        log.info(DELETE_TRANSACTION_REQUEST_LOG_MESSAGE, username, transactionNumber);
        return transactionService.deleteTransactionByTransactionNumber(username, transactionNumber)
                .doOnSuccess(__ -> log.info(TRANSACTION_DELETED_MESSAGE, username, transactionNumber))
                .map(__ -> ResponseEntity.noContent().build());
    }

    @PatchMapping(value = "/{transactionNumber}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Transaction>> updateTransaction(final JwtAuthenticationToken principal,
                                                               @Valid @RequestBody final TransactionDto transactionDto,
                                                               @PathVariable final Integer transactionNumber) {
        final String username = JwtExtractionHelper.extractUsername(principal);
        log.info(DELETE_UPDATE_REQUEST_LOG_MESSAGE, username, transactionNumber);
        return transactionService.updateTransaction(username, transactionDto)
                .doOnSuccess(updatedTransaction -> log.info(TRANSACTION_UPDATED_MESSAGE, updatedTransaction.getUser(), updatedTransaction.getTransactionNumber()))
                .map(ResponseEntity::ok);
    }

    private static HttpHeaders prepareXTotalCountHeader(final Long count) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(X_TOTAL_COUNT_HEADER, String.valueOf(count));
        headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, X_TOTAL_COUNT_HEADER);
        return headers;
    }
}
