package pl.com.seremak.simplebills.messageQueue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.com.seremak.simplebills.commons.dto.queue.TransactionEventDto;
import pl.com.seremak.simplebills.commons.model.Category;

import static pl.com.seremak.simplebills.commons.constants.MessageQueue.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendUserCreationMessage(final String username) {
        sendRabbitMessage(USER_CREATION_SIMPLE_BILLS_QUEUE, username);
    }

    public void sendTransactionEventMessage(final TransactionEventDto transactionEventDto) {
        sendRabbitMessage(TRANSACTION_EVENT_BILLS_PLANING_QUEUE, transactionEventDto);
        if (StringUtils.endsWithIgnoreCase(Category.Type.ASSET.toString(), transactionEventDto.getCategoryName())) {
            sendRabbitMessage(TRANSACTION_EVENT_ASSETS_MANAGEMENT_QUEUE, transactionEventDto);
        }
    }

    private <T> void sendRabbitMessage(final String routingKey, final T rabbitMessage) {
        rabbitTemplate.convertAndSend(SIMPLE_BILLS_EXCHANGE, routingKey, rabbitMessage);
        log.info("Message sent: queue={}, message={}", TRANSACTION_EVENT_BILLS_PLANING_QUEUE, rabbitMessage);
    }
}
