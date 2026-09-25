package com.banking.transactions;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3306/banking_transactions?createDatabaseIfNotExist=true&serverTimezone=UTC",
        "spring.datasource.username=banking",
        "spring.datasource.password=banking123",
        "spring.jpa.hibernate.ddl-auto=none"
})
class TransactionsApplicationTests {

    @Test
    void contextLoads() {
    }

}
