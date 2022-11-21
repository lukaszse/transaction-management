package pl.com.seremak.simplebills.transactionmanagement.intTest.repository;

import pl.com.seremak.simplebills.model.Transaction;

public class RepositoryTestData {

    public static final String BIEDRONKA_SHOPPING = "shoppoing in Biedronka";
    public static final String GROCERY_CATEGORY = "grocery";
    public static final String TEST_USER = "testuser";

    public static Transaction prepareBill(final Integer billNumber) {
        return Transaction.builder()
                .user(TEST_USER)
                .transactionNumber(billNumber)
                .description(BIEDRONKA_SHOPPING)
                .category(GROCERY_CATEGORY)
                .build();
    }
}
