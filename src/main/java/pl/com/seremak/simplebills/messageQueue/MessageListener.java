package pl.com.seremak.simplebills.messageQueue;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.com.seremak.simplebills.messageQueue.queueDto.CategoryDeletionMessage;
import pl.com.seremak.simplebills.service.BillService;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    public static final String USER_CREATION_QUEUE = "userCreation";
    private final BillService billService;


    @RabbitListener(queues = USER_CREATION_QUEUE)
    public void listenCategoryDeletionQueue(final CategoryDeletionMessage categoryDeletionMessage) {
        log.info("Category deletion message received: {}", categoryDeletionMessage);
//        billService.createStandardCategoriesForUserIfNotExists(username);
    }
}
