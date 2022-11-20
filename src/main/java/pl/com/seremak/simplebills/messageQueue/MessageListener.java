package pl.com.seremak.simplebills.messageQueue;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.com.seremak.simplebills.commons.dto.queue.CategoryEventDto;
import pl.com.seremak.simplebills.service.TransactionService;

import static pl.com.seremak.simplebills.commons.constants.MessageQueue.CATEGORY_EVENT_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final TransactionService transactionService;


    @RabbitListener(queues = CATEGORY_EVENT_QUEUE)
    public void receiveCategoryDeletionMessage(final Message<CategoryEventDto> message) {
        final CategoryEventDto categoryEvenMessage = message.getPayload();
        log.info("Category deletion message received: {}", categoryEvenMessage);
        transactionService.handleCategoryDeletion(categoryEvenMessage)
                .subscribe();
    }
}
