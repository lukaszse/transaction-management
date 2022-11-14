package pl.com.seremak.simplebills.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.commons.converter.TransactionConverter;
import pl.com.seremak.simplebills.commons.dto.http.TransactionDto;
import pl.com.seremak.simplebills.commons.dto.http.TransactionQueryParams;
import pl.com.seremak.simplebills.commons.dto.queue.ActionType;
import pl.com.seremak.simplebills.commons.dto.queue.CategoryEventDto;
import pl.com.seremak.simplebills.commons.dto.queue.TransactionEventDto;
import pl.com.seremak.simplebills.commons.exceptions.NotFoundException;
import pl.com.seremak.simplebills.commons.model.Transaction;
import pl.com.seremak.simplebills.commons.utils.OperationType;
import pl.com.seremak.simplebills.commons.utils.VersionedEntityUtils;
import pl.com.seremak.simplebills.messageQueue.MessagePublisher;
import pl.com.seremak.simplebills.repository.TransactionCrudRepository;
import pl.com.seremak.simplebills.repository.TransactionSearchRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static pl.com.seremak.simplebills.commons.converter.TransactionConverter.toTransaction;
import static pl.com.seremak.simplebills.commons.converter.TransactionConverter.toTransactionDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    public static final String OPERATION_ERROR_MESSAGE = "Cannot {} transaction with transactionNumber={} for user={}. Error={}";
    private static final String NOT_FOUND_ERROR_MESSAGE = "Transaction with transactionNumber=%s not found.";
    private final TransactionCrudRepository transactionCrudRepository;
    private final SequentialIdService sequentialIdRepository;
    private final TransactionSearchRepository transactionSearchRepository;
    private final MessagePublisher messagePublisher;


    public Mono<Transaction> createTransaction(final String username, final TransactionDto transactionDto) {
        final Transaction transaction = toTransaction(username, transactionDto);
        return createTransaction(transaction);
    }

    public Mono<Transaction> createTransaction(final Transaction transaction) {
        return sequentialIdRepository.generateId(transaction.getUser())
                .map(id -> setTransactionNumber(transaction, id))
                .map(TransactionService::setCurrentDateIfMissing)
                .map(VersionedEntityUtils::setMetadata)
                .flatMap(transactionCrudRepository::save)
                .doOnSuccess(createdTransaction -> prepareAndSendTransactionEventMessage(createdTransaction, ActionType.CREATION));
    }

    public Mono<Transaction> findTransactionByTransactionNumber(final String username, final Integer transactionNumber) {
        return transactionCrudRepository.findByUserAndTransactionNumber(username, transactionNumber)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND_ERROR_MESSAGE.formatted(transactionNumber))))
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE, OperationType.READ, transactionNumber, username, error.getMessage()));
    }

    public Mono<Tuple2<List<Transaction>, Long>> findTransactionsByCategory(final String username, final TransactionQueryParams params) {
        return transactionSearchRepository.find(username, params)
                .collectList()
                .zipWith(countTransactionsByCategory(username, params));
    }

    public Mono<Long> countTransactionsByCategory(final String username, final TransactionQueryParams params) {
        return transactionSearchRepository.count(username, params);
    }

    public Mono<Transaction> deleteTransactionByTransactionNumber(final String username, final Integer transactionNumber) {
        return transactionCrudRepository.deleteByUserAndTransactionNumber(username, transactionNumber)
                .doOnSuccess(deletedTransaction -> prepareAndSendTransactionEventMessage(deletedTransaction, ActionType.DELETION))
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE, OperationType.DELETE, transactionNumber, username, error.getMessage()));
    }

    public Mono<Transaction> updateTransaction(final String username, final Integer transactionNumber, final TransactionDto transactionDto) {
        final Transaction transaction = toTransaction(username, transactionNumber, transactionDto);
        transaction.setMetadata(null);
        return findTransactionByTransactionNumber(username, transaction.getTransactionNumber())
                .zipWith(transactionSearchRepository.updateTransaction(transaction))
                .doOnSuccess(TransactionTuple -> prepareAndSendTransactionUpdateActionMessage(TransactionTuple.getT1(), TransactionTuple.getT2()))
                .map(Tuple2::getT2)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND_ERROR_MESSAGE.formatted(transaction.getTransactionNumber()))));
    }

    public Flux<Transaction> handleCategoryDeletion(final CategoryEventDto categoryEventDto) {
        if (ActionType.DELETION.equals(categoryEventDto.getActionType())) {
            return changeTransactionCategory(categoryEventDto.getUsername(), categoryEventDto.getCategoryName(), categoryEventDto.getReplacementCategoryName());
        } else return Flux.empty();
    }

    private Flux<Transaction> changeTransactionCategory(final String username,
                                                        final String oldCategoryName,
                                                        final String newCategoryName) {
        return transactionCrudRepository.findByUserAndCategory(username, oldCategoryName)
                .map(transaction -> updateCategory(transaction, newCategoryName))
                .flatMap(transactionWithNewCategory ->
                        updateTransaction(username, transactionWithNewCategory.getTransactionNumber(), transactionWithNewCategory))
                .doOnNext(updatedTransaction -> log.info("A transaction with transactionNumber={} category changed from {} to {}",
                        updatedTransaction.getTransactionNumber(), oldCategoryName, updatedTransaction.getCategory()));
    }

    private void prepareAndSendTransactionEventMessage(final Transaction transaction, final ActionType actionType) {
        final TransactionEventDto transactionEventMessage = toTransactionDto(transaction, actionType);
        messagePublisher.sendTransactionEventMessage(transactionEventMessage);
    }

    private void prepareAndSendTransactionUpdateActionMessage(final Transaction oldTransaction, final Transaction newTransaction) {
        final BigDecimal amountDifference = newTransaction.getAmount().subtract(oldTransaction.getAmount());
        final TransactionEventDto transactionEventDto = toTransactionDto(newTransaction, ActionType.UPDATE, amountDifference);
        messagePublisher.sendTransactionEventMessage(transactionEventDto);
    }

    private static Transaction setTransactionNumber(final Transaction transaction, final Integer id) {
        transaction.setTransactionNumber(id);
        return transaction;
    }

    private static Transaction setCurrentDateIfMissing(final Transaction transaction) {
        if (transaction.getDate() == null) {
            transaction.setDate(Instant.now());
        }
        return transaction;
    }

    private static TransactionDto updateCategory(final Transaction transaction, final String newCategoryName) {

        final TransactionDto transactionDto = TransactionConverter.toTransactionDto(transaction);
        transactionDto.setCategory(newCategoryName);
        return transactionDto;
    }
}
