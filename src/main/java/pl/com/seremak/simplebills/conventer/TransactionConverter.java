package pl.com.seremak.simplebills.conventer;

import pl.com.seremak.simplebills.dto.TransactionDto;
import pl.com.seremak.simplebills.messageQueue.queueDto.TransactionEventDto;
import pl.com.seremak.simplebills.model.Transaction;
import pl.com.seremak.simplebills.util.DateUtils;

import java.math.BigDecimal;

import static pl.com.seremak.simplebills.model.Transaction.Type.EXPENSE;
import static pl.com.seremak.simplebills.model.Transaction.Type.valueOf;
import static pl.com.seremak.simplebills.util.DateUtils.toInstantUTC;

public class TransactionConverter {

    public static Transaction toTransaction(final TransactionDto transactionDto) {
        final Transaction.TransactionBuilder transactionBuilder = Transaction.builder()
                .user(transactionDto.getUser())
                .type(valueOf(transactionDto.getType().toUpperCase()))
                .transactionNumber(transactionDto.getTransactionNumber())
                .description(transactionDto.getDescription())
                .amount(normalizeAmount(transactionDto.getAmount(), transactionDto.getType()))
                .category(transactionDto.getCategory());

        toInstantUTC(transactionDto.getDate())
                .ifPresent(transactionBuilder::date);

        return transactionBuilder.build();
    }

    public static TransactionDto toTransactionDto(final Transaction transaction) {
        final TransactionDto.TransactionDtoBuilder transactionDtoBuilder = TransactionDto.builder()
                .user(transaction.getUser())
                .type(transaction.getType().toString().toUpperCase())
                .transactionNumber(transaction.getTransactionNumber())
                .description(transaction.getDescription())
                .amount(normalizeAmount(transaction.getAmount(), transaction.getType()))
                .category(transaction.getCategory());

        DateUtils.toLocalDate(transaction.getDate())
                .ifPresent(transactionDtoBuilder::date);

        return transactionDtoBuilder.build();
    }

    public static TransactionEventDto toTransactionDto(final Transaction transaction,
                                                       final TransactionEventDto.ActionType actionType) {
        return toTransactionDto(transaction, actionType, transaction.getAmount());
    }

    public static TransactionEventDto toTransactionDto(final Transaction transaction,
                                                       final TransactionEventDto.ActionType actionType,
                                                       final BigDecimal amountDiff) {
        return TransactionEventDto.builder()
                .username(transaction.getUser())
                .categoryName(transaction.getCategory())
                .type(actionType)
                .amount(normalizeAmount(amountDiff, transaction.getType()))
                .date(transaction.getDate())
                .build();
    }

    private static BigDecimal normalizeAmount(final BigDecimal amount, final Transaction.Type transactionType) {
        return EXPENSE.equals(transactionType) ?
                amount.abs().negate() : amount.abs();
    }

    private static BigDecimal normalizeAmount(final BigDecimal amount, final String transactionTypeStr) {
        final Transaction.Type transactionType = valueOf(transactionTypeStr);
        return normalizeAmount(amount, transactionType);
    }
}
