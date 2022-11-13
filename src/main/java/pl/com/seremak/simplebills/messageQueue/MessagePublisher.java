package pl.com.seremak.simplebills.messageQueue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.com.seremak.simplebills.commons.dto.queue.TransactionEventDto;

import static pl.com.seremak.simplebills.config.RabbitMQConfig.TRANSACTION_QUEUE;
import static pl.com.seremak.simplebills.config.RabbitMQConfig.USER_CREATION_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendUserCreationMessage(final String username) {
        rabbitTemplate.convertAndSend(USER_CREATION_QUEUE, username);
        log.info("Message sent: queue={}, message={}", USER_CREATION_QUEUE, username);
    }

    public void sendTransactionMessage(final TransactionEventDto transactionEventDto) {
        rabbitTemplate.convertAndSend(TRANSACTION_QUEUE, transactionEventDto);
        log.info("Message sent: queue={}, message={}", TRANSACTION_QUEUE, transactionEventDto);
    }
}
