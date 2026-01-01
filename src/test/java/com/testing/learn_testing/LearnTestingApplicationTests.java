package com.testing.learn_testing;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Slf4j
class LearnTestingApplicationTests {

    @BeforeEach
    void setUp() {
        log.info("Starting the method, setting up config");
    }

    @AfterEach
    void tearDown() {
        log.info("Tearing down the method");
    }

    @BeforeAll
    static void setUpOnce() {
        log.info("Setup Once...");
    }

    @AfterAll
    static void tearDownOnce() {
        log.info("Tearning down all...");
    }

    @Test
    void testNumberOne() {
        int a = 5;
        int b = 3;

        int result = addTwoNumbers(a, b);

        Assertions.assertEquals(8, result);

        assertThat("Apple")
                .isEqualTo("Apple")
                .startsWith("App")
                .endsWith("e")
                .hasSize(5);
    }

    @Test
//	@DisplayName("displayTestNameTwo")
    void testDivideTwoNumbers_whenDenominatorIsZero_ThenArithmeticException() {

        int a = 5;
        int b = 0;

        assertThatThrownBy(() -> divideTwoNumbers(a, b))
                .isInstanceOf(ArithmeticException.class)
                .hasMessage("Tried to divide by zero");

    }

    double divideTwoNumbers(int a, int b) {
        try {
            return a/b;
        } catch (ArithmeticException e) {
            log.error("Arithmentic excepiton occured: "+e.getLocalizedMessage());
            throw new ArithmeticException("Tried to divide by zero");
        }
    }

    int addTwoNumbers(int a, int b) {
        return a+b;
    }

}
