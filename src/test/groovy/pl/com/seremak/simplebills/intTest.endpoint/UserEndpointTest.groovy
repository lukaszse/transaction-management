package pl.com.seremak.simplebills.intTest.endpoint

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity

import static pl.com.seremak.simplebills.intTest.endpoint.utils.EndpointSpecData.*

class UserEndpointTest extends EndpointIntSpec {

    def 'should change password'() {

        given: 'prepare password change request'
        def request =
                RequestEntity.put(SERVICE_URL_CHANGE_PASSWORD_PATTERN.formatted(port))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER_NAME, BASIC_TOKEN)
                        .body(createChangePasswordRequestBody())

        when: 'should fetch bill'
        def response = client.exchange(request, Void.class)

        then: 'should return response 200'
        response != null
        response.getStatusCode() == HttpStatus.OK

    }
}
