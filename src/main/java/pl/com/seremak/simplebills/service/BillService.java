package pl.com.seremak.simplebills.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.seremak.simplebills.dto.BillDto;
import pl.com.seremak.simplebills.dto.BillQueryParams;
import pl.com.seremak.simplebills.exceptions.NotFoundException;
import pl.com.seremak.simplebills.messageQueue.MessagePublisher;
import pl.com.seremak.simplebills.messageQueue.queueDto.TransactionDto;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.repository.BillCrudRepository;
import pl.com.seremak.simplebills.repository.BillSearchRepository;
import pl.com.seremak.simplebills.util.OperationType;
import pl.com.seremak.simplebills.util.VersionedEntityUtils;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static pl.com.seremak.simplebills.conventer.BillConverter.*;
import static pl.com.seremak.simplebills.messageQueue.queueDto.TransactionDto.ActionType;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillService {

    public static final String OPERATION_ERROR_MESSAGE = "Cannot {} bill with billNumber={} for user={}. Error={}";
    private static final String NOT_FOUND_ERROR_MESSAGE = "Bill with billNumber=%s not found.";
    private final BillCrudRepository billCrudRepository;
    private final SequentialIdService sequentialIdRepository;
    private final BillSearchRepository billSearchRepository;
    private final MessagePublisher messagePublisher;


    public Mono<Bill> createBill(final String username, final BillDto billDto) {
        final Bill bill = toBill(billDto);
        return sequentialIdRepository.generateId(username)
                .map(id -> setBillNumber(bill, id, username))
                .map(BillService::setCurrentDateIfMissing)
                .map(VersionedEntityUtils::setMetadata)
                .flatMap(billCrudRepository::save)
                .doOnSuccess(createdBill -> prepareAndSendBillActionMessage(createdBill, ActionType.CREATION));
    }

    public Mono<Bill> findBillByBillNumber(final String username, final Integer billNumber) {
        return billCrudRepository.findByUserAndBillNumber(username, billNumber)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND_ERROR_MESSAGE.formatted(billNumber))))
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE, OperationType.READ, billNumber, username, error.getMessage()));
    }

    public Mono<Tuple2<List<Bill>, Long>> findBillsByCategory(final String username, final BillQueryParams params) {
        return billSearchRepository.find(username, params)
                .collectList()
                .zipWith(countBillsByCategory(username, params));
    }

    public Mono<Long> countBillsByCategory(final String username, final BillQueryParams params) {
        return billSearchRepository.count(username, params);
    }

    public Mono<Bill> deleteBillByBillNumber(final String username, final Integer billNumber) {
        return billCrudRepository.deleteByUserAndBillNumber(username, billNumber)
                .doOnSuccess(deletedBill -> prepareAndSendBillActionMessage(deletedBill, ActionType.DELETION))
                .doOnError(error -> log.error(OPERATION_ERROR_MESSAGE, OperationType.DELETE, billNumber, username, error.getMessage()));
    }

    public Mono<Bill> updateBill(final String username, final BillDto billDto) {
        final Bill bill = toBill(billDto);
        bill.setMetadata(null);
        return findBillByBillNumber(username, bill.getBillNumber())
                .zipWith(billSearchRepository.updateBill(username, bill))
                .doOnSuccess(billTuple -> prepareAndSendBillUpdateActionMessage(billTuple.getT1(), billTuple.getT2()))
                .map(Tuple2::getT2)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND_ERROR_MESSAGE.formatted(bill.getBillNumber()))));
    }

    public void changeBillCategory(final String username,
                                   final String oldCategoryName,
                                   final String newCategoryName) {
        billCrudRepository.findByUserAndCategory(username, oldCategoryName)
                .map(bill -> setCategory(bill, newCategoryName))
                .flatMap(billWithNewCategory -> updateBill(username, billWithNewCategory))
                .doOnNext(updatedBill -> log.info("A bill with billNumber={} category changed from {} to {}", updatedBill.getBillNumber(), oldCategoryName, updatedBill.getCategory()))
                .subscribe();
    }

    private void prepareAndSendBillActionMessage(final Bill bill, final ActionType actionType) {
        final TransactionDto billActionMessage = toTransactionDto(bill, actionType);
        messagePublisher.sendBillActionMessage(billActionMessage);
    }

    private void prepareAndSendBillUpdateActionMessage(final Bill oldBill, final Bill newBill) {
        final BigDecimal amountDifference = newBill.getAmount().subtract(oldBill.getAmount());
        final TransactionDto billActionMessage = toTransactionDto(newBill, ActionType.UPDATE, amountDifference);
        messagePublisher.sendBillActionMessage(billActionMessage);
    }

    private static Bill setBillNumber(final Bill bill, final Integer id, final String username) {
        bill.setUser(username);
        bill.setBillNumber(id);
        return bill;
    }

    private static Bill setCurrentDateIfMissing(final Bill bill) {
        if (bill.getDate() == null) {
            bill.setDate(Instant.now());
        }
        return bill;
    }

    private static BillDto setCategory(final Bill bill, final String newCategoryName) {

        final BillDto billDto = toBillDto(bill);
        billDto.setCategory(newCategoryName);
        return billDto;
    }
}
