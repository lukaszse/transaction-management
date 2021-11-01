package pl.com.seremak.simplebills.intTest.endpoint

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestTemplate
import pl.com.seremak.simplebills.endpoint.StatisticsEndpoint
import pl.com.seremak.simplebills.repository.BillCrudRepository
import pl.com.seremak.simplebills.repository.UserCrudRepository
import pl.com.seremak.simplebills.service.UserCrudService
import spock.lang.Shared
import spock.lang.Specification

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.stream.IntStream

import static pl.com.seremak.simplebills.intTest.endpoint.utils.EndpointSpecData.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EndpointIntSpec extends Specification {

    @LocalServerPort
    protected int port

    @Autowired
    StatisticsEndpoint statisticsEndpoint

    @Autowired
    BillCrudRepository billCrudRepository

    @Autowired
    UserCrudRepository userCrudRepository

    @Autowired
    UserCrudService userCrudService

    @Shared
    RestTemplate client = new RestTemplate()

    def setup() {
        // populate database for tests
        billCrudRepository.deleteAll().block()
        IntStream.range(1, 10)
                .forEach(i ->
                        billCrudRepository.save(prepareBillForEndpointTest(i, 10 * i, FOOD, Instant.now().minus(1 * i, ChronoUnit.DAYS))).block())
        IntStream.range(10, 12)
                .forEach(i ->
                        billCrudRepository.save(prepareBillForEndpointTest(i, 200 * i, TRAVEL, Instant.now().minus(360 * i, ChronoUnit.DAYS))).block())
        userCrudService.createUser(createTestUser()).block()
    }

    def cleanup() {
        billCrudRepository.deleteAll().block()
        userCrudRepository.deleteAll().block()
    }
}
