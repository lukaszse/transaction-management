package pl.com.seremak.simplebills.intTest.endpoint

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity

import static pl.com.seremak.simplebills.intTest.endpoint.utils.EndpointSpecData.*

class StatisticsEndpointIntSpec extends EndpointIntSpec {


    def 'should retrieve (repository level initial test)'() {
        when:
        def bill = billCrudRepository.findByUserAndTransactionNumber(TEST_USER, "1").block()

        then:
        bill != null
        bill.getTransactionNumber() == "1"
        bill.getCategory() == FOOD
    }

    def 'should calculate sum for bills with given category'() {
        given: 'prepare request'
        def request =
                RequestEntity.get(SERVICE_URL_STATISTICS_WITH_CATEGORY_PATTERN.formatted(port, "/sum", category))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER_NAME, BASIC_TOKEN)
                        .build()

        when: 'call to service to to get mean value'
        ResponseEntity<BigDecimal> response = client.exchange(request, BigDecimal.class)

        then: 'result should match'
        response != null
        response.getStatusCode() == HttpStatus.OK
        response.getBody() == BigDecimal.valueOf(expectedSumValue)

        where: 'test data are'
        category | expectedSumValue
        FOOD     | 450
        TRAVEL   | 4200
        CAR      | 0
    }

    def 'should calculate mean for bills with given category'() {
        given: 'prepare request'
        def request =
                RequestEntity.get(SERVICE_URL_STATISTICS_WITH_CATEGORY_PATTERN.formatted(port, "/mean", category))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER_NAME, BASIC_TOKEN)
                        .build()

        when: 'call to service to to get mean value'
        ResponseEntity<BigDecimal> response = client.exchange(request, BigDecimal.class)

        then: 'result should match'
        response != null
        response.getStatusCode() == HttpStatus.OK
        response.getBody() == BigDecimal.valueOf(expectedSumValue)

        where: 'test data are'
        category | expectedSumValue
        FOOD     | 50
        TRAVEL   | 2100
        CAR      | 0
    }

    def 'should fetch statistics for bills with given category'() {
        given: 'prepare request'
        def request =
                RequestEntity.get(SERVICE_URL_STATISTICS_WITH_CATEGORY_PATTERN.formatted(port, "/user-statistics", category))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER_NAME, BASIC_TOKEN)
                        .build()

        when: 'call to service to to get mean value'
        ResponseEntity<StatisticsDto> response = client.exchange(request, StatisticsDto.class)

        then: 'result should match'
        response != null
        response.getStatusCode() == HttpStatus.OK
        response.getBody().getCategory() == category
        response.getBody().getSum() == expectedSum
        response.getBody().getMean() == expectedMean


        where: 'test data are'
        category | expectedMean | expectedSum
        FOOD     | 50           | 450
        TRAVEL   | 2100         | 4200
        CAR      | 0            | 0
    }
}
