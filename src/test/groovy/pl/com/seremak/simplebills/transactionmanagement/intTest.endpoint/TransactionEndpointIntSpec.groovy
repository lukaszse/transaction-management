package pl.com.seremak.simplebills.transactionmanagement.intTest.endpoint

import org.apache.commons.lang3.StringUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import pl.com.seremak.simplebills.model.Transaction

import java.time.Instant

import static pl.com.seremak.simplebills.transactionmanagement.intTest.endpoint.utils.EndpointSpecData.*

class TransactionEndpointIntSpec extends EndpointIntSpec {

    def 'should create bill for user and then fetch created bill'() {

        given: 'prepare request for bill creation'
        def creationRequest =
                RequestEntity.post(SERVICE_URL_BILL_CRUD_PATTERN.formatted(port, StringUtils.EMPTY))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER_NAME, BASIC_TOKEN_TEST_USER_2)
                        .body(prepareBillForEndpointTest(100, FOOD, Instant.now()))


        when: 'make request to crate bill'
        def billNumber = client.exchange(creationRequest, String.class)


        then: 'should return correct creation response and retrieve created bill'
        conditions.eventually {

            def fetchResponse = client.exchange(
                    RequestEntity.get(SERVICE_URL_BILL_CRUD_PATTERN.formatted(port, "/%s".formatted(billNumber.getBody())))
                            .accept(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION_HEADER_NAME, BASIC_TOKEN)
                            .build(),
                    Transaction.class)

            assert fetchResponse != null
            assert fetchResponse.getStatusCode() == HttpStatus.OK
            assert fetchResponse.getBody().getCategory() == FOOD
        }
    }

    def 'should fetch bill'() {

        given: 'prepare request to get bill'
        def request =
                RequestEntity.get(SERVICE_URL_BILL_CRUD_PATTERN.formatted(port, "/%d".formatted(billNumber)))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER_NAME, BASIC_TOKEN)
                        .build()

        when: 'should fetch bill'
        def response = client.exchange(request, Transaction.class)

        then:
        response != null
        response.getStatusCode() == HttpStatus.OK
        response.getBody().getCategory() == category
        response.getBody().getAmount() == BigDecimal.valueOf(amount)

        where:
        billNumber | category | amount
        1          | FOOD     | 10
        2          | FOOD     | 20
        5          | FOOD     | 50
        9          | FOOD     | 90
        11         | TRAVEL   | 2200
    }
}
