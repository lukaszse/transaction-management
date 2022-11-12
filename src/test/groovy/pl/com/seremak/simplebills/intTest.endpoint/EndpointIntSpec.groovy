package pl.com.seremak.simplebills.intTest.endpoint

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestTemplate
import pl.com.seremak.simplebills.endpoint.StatisticsEndpoint
import pl.com.seremak.simplebills.repository.TransactionCrudRepository
import pl.com.seremak.simplebills.service.SequentialIdService
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.stream.IntStream

import static pl.com.seremak.simplebills.intTest.endpoint.utils.EndpointSpecData.*

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EndpointIntSpec extends Specification {

    @LocalServerPort
    protected int port

    @Autowired
    StatisticsEndpoint statisticsEndpoint

    @Autowired
    TransactionCrudRepository billCrudRepository

    @Autowired
    SequentialIdService sequentialIdService

    @Shared
    def client = new RestTemplate()

    def conditions = new PollingConditions(timeout: 5, initialDelay: 1)


    def setup() {
        // populate database for tests
        billCrudRepository.deleteAll().block()
        tryToCleanSequentialIdRepository()
        IntStream.range(1, 10)
                .forEach(i ->
                        billCrudRepository.save(prepareBillForEndpointTest(i, 10 * i, FOOD, Instant.now().minus(1 * i, ChronoUnit.DAYS))).block())
        IntStream.range(10, 12)
                .forEach(i ->
                        billCrudRepository.save(prepareBillForEndpointTest(i, 200 * i, TRAVEL, Instant.now().minus(360 * i, ChronoUnit.DAYS))).block())

    }

    def cleanup() {
        billCrudRepository.deleteAll().block()
        tryToCleanSequentialIdRepository()
    }

    def tryToCleanSequentialIdRepository() {
        try {
            sequentialIdService.deleteUser(TEST_USER).block()
        } catch (Exception ex) {
            log.info("Error occurred while cleaning up: {}, {}", ex.getMessage(), ex.getCause())
        }
        try {
            sequentialIdService.deleteUser(TEST_USER_2).block()
        } catch (Exception ex) {
            log.info("Error occurred while cleaning up: {}, {}", ex.getMessage(), ex.getCause())
        }
    }
}
