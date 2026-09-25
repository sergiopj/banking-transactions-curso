package com.banking.transactions;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test Suite global que ejecuta todos los tests del proyecto.
 * Al darle 'Run' a esta clase en el IDE, lanzará la suite completa all tests.
 */
@Suite
@SuiteDisplayName("Banking Transactions - Full Test Suite")
@SelectPackages("com.banking.transactions")
public class AllTestsSuite {
}
