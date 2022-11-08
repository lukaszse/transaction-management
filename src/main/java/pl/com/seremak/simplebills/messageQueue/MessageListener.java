package pl.com.seremak.simplebills.messageQueue;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.com.seremak.simplebills.messageQueue.queueDto.CategoryDeletionDto;
import pl.com.seremak.simplebills.service.BillService;

import static pl.com.seremak.simplebills.config.RabbitMQConfig.CATEGORY_DELETION_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final BillService billService;


    @RabbitListener(queues = CATEGORY_DELETION_QUEUE)
    public void listenCategoryDeletionQueue(final Message<CategoryDeletionDto> message) {
        final CategoryDeletionDto categoryDeletionMessage = message.getPayload();
        log.info("Category deletion message received: {}", categoryDeletionMessage);
        billService.changeBillCategory(categoryDeletionMessage.getUsername(), categoryDeletionMessage.getDeletedCategory(),
                categoryDeletionMessage.getReplacementCategory());
    }
}
