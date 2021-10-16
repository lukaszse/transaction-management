package pl.com.seremak.simplebills.intTest.repository;

import pl.com.seremak.simplebills.model.bill.Bill;

public class BillCrudRepositoryTestData {

    public static final String BIEDRONKA_SHOPPING = "shoppoing in Biedronka";
    public static final String GROCERY_CATEGORY = "grocery";

    public static Bill prepareBill(final String id) {
        return Bill.builder()
                .id(id)
                .description(BIEDRONKA_SHOPPING)
                .category(GROCERY_CATEGORY)
                .build();
    }
}
