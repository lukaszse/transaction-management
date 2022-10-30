package pl.com.seremak.simplebills.intTest.endpoint.utils

import pl.com.seremak.simplebills.model.Bill
import pl.com.seremak.simplebills.model.Metadata

import java.time.Instant

class EndpointSpecData {

    static def TEST_USER = "testuser"
    static def TEST_USER_2 = "testuser2"
    static def TEST_PASSWORD = "12345"
    static def FOOD = "food"
    static def TRAVEL = "travel"
    static def CAR = "CAR"
    static def AUTHORIZATION_HEADER_NAME = "Authorization"
    static def BASIC_TOKEN = "Basic dGVzdHVzZXI6MTIzNDU="
    static def BASIC_TOKEN_TEST_USER_2 = "Basic dGVzdHVzZXIyOjEyMzQ1"
    static def SERVICE_URL_STATISTICS_WITH_CATEGORY_PATTERN = "http://localhost:%d/statistics%s?category=%s"
    static def SERVICE_URL_BILL_CRUD_PATTERN = "http://localhost:%d/bills%s"
    static def SERVICE_URL_CHANGE_PASSWORD_PATTERN = "http://localhost:%d/users/change-password"
    static def FOOD_SHOPPING_DESCRIPTION = "Food shopping"
    static def DESCRIPTION_PATTERN = "%s shopping"


    static def prepareBillForEndpointTest(
            final int billNumber,
            final int amount,
            final String category,
            final Instant date) {

        Bill.builder()
                .amount(BigDecimal.valueOf(amount))
                .billNumber(String.valueOf(billNumber))
                .description(DESCRIPTION_PATTERN.formatted(category))
                .category(category)
                .date(Instant.now())
                .user(TEST_USER)
                .date(date)
                .metadata(prepareMetadataNow())
                .build()
    }

    static def prepareBillForEndpointTest(
            final double amount,
            final String category,
            final Instant date) {

        Bill.builder()
                .amount(BigDecimal.valueOf(amount))
                .category(category)
                .description(FOOD_SHOPPING_DESCRIPTION)
                .build()
    }

    static def prepareMetadataNow() {
        Metadata.builder()
                .createdAt(Instant.now())
                .modifiedAt(Instant.now())
                .version(1)
                .build()
    }

    static def createChangePasswordRequestBody() {
        Map.of(
                "user", TEST_USER,
                "oldPassword", TEST_PASSWORD,
                "newPassword", "12345",
                "confirmNewPassword", "12345"
        )
    }
}
