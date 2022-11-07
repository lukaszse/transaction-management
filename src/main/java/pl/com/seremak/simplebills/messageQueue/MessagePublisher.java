package pl.com.seremak.simplebills.messageQueue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.com.seremak.simplebills.messageQueue.queueDto.BillActionMessage;

import static pl.com.seremak.simplebills.config.RabbitMQConfig.BILL_ACTION_MESSAGE;
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

    public void sendBillActionMessage(final BillActionMessage billActionMessage) {
        rabbitTemplate.convertAndSend(BILL_ACTION_MESSAGE, billActionMessage);
        log.info("Message sent: queue={}, message={}", BILL_ACTION_MESSAGE, billActionMessage);
    }
}
