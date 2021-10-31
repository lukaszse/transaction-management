package pl.com.seremak.simplebills.intTest.repository;

import pl.com.seremak.simplebills.model.Bill;

public class RepositoryTestData {

    public static final String BIEDRONKA_SHOPPING = "shoppoing in Biedronka";
    public static final String GROCERY_CATEGORY = "grocery";
    public static final String TEST_USER = "testuser";

    public static Bill prepareBill(final String billNumber) {
        return Bill.builder()
                .user(TEST_USER)
                .billNumber(billNumber)
                .description(BIEDRONKA_SHOPPING)
                .category(GROCERY_CATEGORY)
                .build();
    }
}
