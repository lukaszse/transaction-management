package pl.com.seremak.simplebills.intTest.endpoint

import pl.com.seremak.simplebills.model.Bill
import pl.com.seremak.simplebills.model.Metadata
import pl.com.seremak.simplebills.model.User

import java.time.Instant

class EndpointSpecData {

    static def TEST_USER = "testuser"
    static def TEST_PASSWORD = "12345"
    static def FOOD = "food"
    static def TRAVEL = "travel"
    static def AUTHORIZATION_HEADER_NAME = "Authorization"
    static def BASIC_TOKEN = "Basic dGVzdHVzZXI6MTIzNDU="
    static def SERVICE_URL_STATISTICS_WITH_CATEGORY_PATTERN = "http://localhost:%d/statistics/%s?category=%s"


    static def prepareBill(
            final int billNumber,
            final int amount,
            final String category,
            final Instant date) {

        Bill.builder()
                .amount(BigDecimal.valueOf(amount))
                .billNumber(String.valueOf(billNumber))
                .category(category)
                .date(Instant.now())
                .user(TEST_USER)
                .date(date)
                .metadata(prepareMetadataNow())
                .build()
    }

    static def prepareMetadataNow() {
        Metadata.builder()
                .createdAt(Instant.now())
                .modifiedAt(Instant.now())
                .version(1)
                .build()
    }

    static def createTestUser() {
        User.builder()
                .login(TEST_USER)
                .password(TEST_PASSWORD)
                .build()
    }
}
