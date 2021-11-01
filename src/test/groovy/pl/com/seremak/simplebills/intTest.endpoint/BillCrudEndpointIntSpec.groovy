package pl.com.seremak.simplebills.intTest.endpoint

import org.apache.commons.lang3.StringUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import pl.com.seremak.simplebills.model.Bill

import java.time.Instant

import static pl.com.seremak.simplebills.intTest.endpoint.EndpointSpecData.*

class BillCrudEndpointIntSpec extends EndpointIntSpec {

    def 'should create bill for user and then fetch created bill'() {

        given: 'prepare request for bill creation'
        def request =
                RequestEntity.post(SERVICE_URL_BILL_CRUD_PATTERN.formatted(port, StringUtils.EMPTY))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER_NAME, BASIC_TOKEN)
                        .body(prepareBillForEndpointTest(amount, category, Instant.now()))

        when: 'make request to crate bill'
        def creationResponse = client.exchange(request, String.class)

        then: 'should return correct creation response'
        creationResponse != null
        creationResponse.getStatusCode() == HttpStatus.CREATED

        where:
        amount | category
        5d     | FOOD
        69.99d | FOOD
        9999d  | TRAVEL
    }

    def 'should fetch bill'() {

        given: 'prepare request to get bill'
        def request =
                RequestEntity.get(SERVICE_URL_BILL_CRUD_PATTERN.formatted(port, "/%d".formatted(billNumber)))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER_NAME, BASIC_TOKEN)
                        .build()

        when: 'should fetch bill'
        def fetchResponse = client.exchange(request, Bill.class)

        then:
        fetchResponse != null
        fetchResponse.getStatusCode() == HttpStatus.OK
        fetchResponse.getBody().getCategory() == category
        fetchResponse.getBody().getAmount() == amount

        where:
        billNumber | category | amount
        1          | FOOD     | 10
        2          | FOOD     | 20
        5          | FOOD     | 50
        9          | FOOD     | 90
        11         | TRAVEL   | 2200
    }
}
