package pl.com.seremak.simplebills.intTest.endpoint


import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity

import static pl.com.seremak.simplebills.EndpointSpecData.*

class StatisticsEndpointIntSpec extends EndpointIntSpec {


    def 'should retrieve (repository level initial test)'() {
        when:
        def bill = billCrudRepository.findByUserAndBillNumber(TEST_USER, "1").block()

        then:
        bill != null
        bill.getBillNumber() == "1"
        bill.getCategory() == FOOD
    }

    def 'should calculate sum for bills'() {
        given: 'prepare request'
        def request =
                RequestEntity.get(SERVICE_URL_STATISTICS_WITH_CATEGORY_PATTERN.formatted(port, "sum", FOOD))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER_NAME, BASIC_TOKEN)
                        .build()

        when: 'call to service to to get mean value'
        ResponseEntity<BigDecimal> response = client.exchange(request, BigDecimal.class)

        then:
        response != null
        response.getStatusCode() == HttpStatus.OK
        response.getBody() == BigDecimal.valueOf(450)
    }
}
