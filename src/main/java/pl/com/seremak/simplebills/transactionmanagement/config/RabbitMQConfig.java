package pl.com.seremak.simplebills.transactionmanagement.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static pl.com.seremak.simplebills.commons.constants.MessageQueue.*;

@Slf4j
@EnableRabbit
@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {


    private final CachingConnectionFactory cachingConnectionFactory;
    private final ObjectMapper objectMapper;


    @Bean
    public RabbitTemplate rabbitTemplate() {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * Required for executing administration functions against an AMQP Broker
     */
    @Bean
    public AmqpAdmin rabbitAdmin() {
        return new RabbitAdmin(cachingConnectionFactory);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(SIMPLE_BILLS_EXCHANGE);
    }

    @Bean
    public Queue userCreationSimpleBillsQueue() {
        return new Queue(USER_CREATION_SIMPLE_BILLS_QUEUE, false);
    }

    @Bean
    public Queue categoryDeletionPlaningQueue() {
        return new Queue(CATEGORY_EVENT_SIMPLE_BILLS_QUEUE, false);
    }

    @Bean
    public Queue transactionEventPlanningQueue() {
        return new Queue(TRANSACTION_EVENT_BILLS_PLANING_QUEUE, false);
    }

    @Bean
    public Queue transactionEventAssetManagementQueue() {
        return new Queue(TRANSACTION_EVENT_ASSETS_MANAGEMENT_QUEUE, false);
    }

    @Bean
    Binding userCreationSimpleBillsBinding(final Queue userCreationSimpleBillsQueue,
                                           final DirectExchange exchange) {
        return BindingBuilder
                .bind(userCreationSimpleBillsQueue)
                .to(exchange)
                .with(USER_CREATION_SIMPLE_BILLS_QUEUE);
    }

    @Bean
    Binding categoryDeletionPlaningBinding(final Queue categoryDeletionPlaningQueue,
                                           final DirectExchange exchange) {
        return BindingBuilder
                .bind(categoryDeletionPlaningQueue)
                .to(exchange)
                .with(CATEGORY_EVENT_SIMPLE_BILLS_QUEUE);
    }

    @Bean
    Binding transactionsEventsPlanningBinding(final Queue transactionEventPlanningQueue,
                                              final DirectExchange exchange) {
        return BindingBuilder
                .bind(transactionEventPlanningQueue)
                .to(exchange)
                .with(TRANSACTION_EVENT_BILLS_PLANING_QUEUE);
    }

    @Bean
    Binding transactionsEventsAssetsManagementBinding(final Queue transactionEventAssetManagementQueue,
                                                      final DirectExchange exchange) {
        return BindingBuilder
                .bind(transactionEventAssetManagementQueue)
                .to(exchange)
                .with(TRANSACTION_EVENT_ASSETS_MANAGEMENT_QUEUE);
    }
}
