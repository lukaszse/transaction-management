package pl.com.seremak.simplebills.conventer;

import pl.com.seremak.simplebills.dto.BillDto;
import pl.com.seremak.simplebills.messageQueue.queueDto.TransactionDto;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.util.DateUtils;

import java.math.BigDecimal;

import static pl.com.seremak.simplebills.util.DateUtils.toInstantUTC;

public class BillConverter {

    public static Bill toBill(final BillDto billDto) {
        final Bill.BillBuilder billBuilder = Bill.builder()
                .user(billDto.getUser())
                .billNumber(billDto.getBillNumber())
                .description(billDto.getDescription())
                .amount(billDto.getAmount())
                .category(billDto.getCategory());

        toInstantUTC(billDto.getDate())
                .ifPresent(billBuilder::date);

        return billBuilder.build();
    }

    public static BillDto toBillDto(final Bill bill) {
        final BillDto.BillDtoBuilder billDtoBuilder = BillDto.builder()
                .user(bill.getUser())
                .billNumber(bill.getBillNumber())
                .description(bill.getDescription())
                .amount(bill.getAmount())
                .category(bill.getCategory());

        DateUtils.toLocalDate(bill.getDate())
                .ifPresent(billDtoBuilder::date);

        return billDtoBuilder.build();
    }

    public static TransactionDto toTransactionDto(final Bill bill,
                                                  final TransactionDto.ActionType actionType) {
        return toTransactionDto(bill, actionType, bill.getAmount());
    }

    public static TransactionDto toTransactionDto(final Bill bill,
                                                  final TransactionDto.ActionType actionType,
                                                  final BigDecimal amount) {
        return TransactionDto.builder()
                .username(bill.getUser())
                .categoryName(bill.getCategory())
                .type(actionType)
                .amount(amount)
                .date(bill.getDate())
                .build();

    }
}
