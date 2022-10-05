package products;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import products.cards.Card;
import products.cards.CreditCard;
import products.cards.CurrencyDebitCard;
import products.cards.DebitCard;
import products.deposits.Deposit;
import products.exceptions.ArgumentValidateException;

import java.lang.reflect.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BankProductsTests {


    @Nested
    class ClassStructureTests {

        @Test
        void hierarchyTest() {

            assertAll("Hierarchy",
                    () -> assertEquals(CreditCard.class.getSuperclass(), Card.class),
                    () -> assertEquals(DebitCard.class.getSuperclass(), Card.class),
                    () -> assertEquals(CurrencyDebitCard.class.getSuperclass(), Card.class),
                    () -> assertEquals(Card.class.getSuperclass(), BankProduct.class),
                    () -> assertEquals(Deposit.class.getSuperclass(), BankProduct.class));
        }

        @Test
        void classPropertiesTest() {
            assertAll("Class has property",
                    () -> assertAll("BankProduct",
                            () -> assertTrue(classHasMember(BankProduct.class, "name",
                                    Modifier.PROTECTED, MemberType.PROPERTY)),
                            () -> assertTrue(classHasMember(BankProduct.class, "currency",
                                    Modifier.PROTECTED, MemberType.PROPERTY)),
                            () -> assertTrue(classHasMember(BankProduct.class, "balance",
                                    Modifier.PROTECTED, MemberType.PROPERTY))),
                    () -> assertAll("Credit card",
                            () -> assertTrue(classHasMember(CreditCard.class, "interestRate",
                                    Modifier.PRIVATE, MemberType.PROPERTY))),
                    () -> assertAll("Deposit",
                            () -> assertTrue(classHasMember(Deposit.class, "isDepositActive",
                                    Modifier.PRIVATE, MemberType.PROPERTY))));

        }

        @Test
        void classMethodsTest() {
            assertAll("Class has methods",
                    () -> assertAll("BankProduct",
                            () -> assertTrue(classHasMember(BankProduct.class, "replenishment",
                                    Modifier.PUBLIC, MemberType.METHOD)),
                            () -> assertTrue(classHasMember(BankProduct.class, "getBalance",
                                    Modifier.PUBLIC, MemberType.METHOD))),
                    () -> assertAll("Card",
                            () -> assertTrue(classHasMember(Card.class, "writingOff",
                                    Modifier.PUBLIC, MemberType.METHOD))),
                    () -> assertAll("CreditCard",
                            () -> assertTrue(classHasMember(CreditCard.class, "getDebt",
                                    Modifier.PUBLIC, MemberType.METHOD))),
                    () -> assertAll("Deposit",
                            () -> assertTrue(classHasMember(Deposit.class, "close",
                                    Modifier.PUBLIC, MemberType.METHOD)))
            );
        }

        private boolean classHasMember(Class<? extends BankProduct> clazz,
                                       String memberName, int modifier, MemberType memberType) {
            Member[] members = MemberType.PROPERTY.equals(memberType) ? clazz.getDeclaredFields() : clazz.getDeclaredMethods();
            return Arrays.stream(members)
                    .anyMatch(field -> field.getName().equals(memberName) && field.getModifiers() == modifier);
        }

        private enum MemberType {
            METHOD, PROPERTY
        }
    }


    @Nested
    class CommonProductTests {

        private BankProduct product;
        private final double balance = 100.0;

        @BeforeEach
        void createNewProduct() {
            product = new DebitCard("Debit card", balance);
        }

        @Test
        void getBalanceTest() {
            assertEquals(balance, product.getBalance(), () -> "Values is not consistent");
        }

        @ParameterizedTest
        @ValueSource(doubles = {1, 10.99, 1000, 99, 0, -10.55, -1000})
        void replenishmentTest(double payment) {
            if (payment > 0) {
                product.replenishment(payment);
                assertEquals(product.getBalance(), balance + payment);
            } else {
                ArgumentValidateException exception = assertThrows(ArgumentValidateException.class, () -> {
                    product.replenishment(payment);
                });
                assertEquals(exception.getMessage(), "Payment should be positive");
            }
        }

        @ParameterizedTest
        @ValueSource(doubles = {-10, 0, 100})
        void validateBalanceArg(double balance) {
            try {
                Method balanceValidateMethod = BankProduct.class.getDeclaredMethod("validateBalance", double.class);
                balanceValidateMethod.setAccessible(true);
                if (balance >= 0) {
                    assertEquals(balanceValidateMethod.invoke(product, balance), balance);
                } else {
                    Throwable ex = assertThrows(Throwable.class,
                            () -> balanceValidateMethod.invoke(product, balance)).getCause();
                    assertTrue(ex instanceof ArgumentValidateException);
                    assertEquals(ex.getMessage(), "Balance should be positive or 0");
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }


        @AfterEach
        void tearDown() {
            product = null;
        }

    }

    @Nested
    class CardTests {

        @ParameterizedTest
        @ValueSource(doubles = {-100, 0, 50, 100, 100.01})
        void writingOffTest(double amount) {
            double cardBalance = 100.00;
            Card card = new DebitCard("Debit card", cardBalance);
            if (amount <= 0 || amount > cardBalance) {
                ArgumentValidateException ex = assertThrows(ArgumentValidateException.class, () -> {
                    card.writingOff(amount);
                });
                if (amount <= 0) assertEquals(ex.getMessage(), "Amount should be positive");
                else assertEquals(ex.getMessage(), "There is no enough money in your account");
            } else {
                card.writingOff(amount);
                assertEquals(card.getBalance(), cardBalance - amount);
            }
        }
    }

    @Nested
    class CreditCardTests {
        @Test
        void getDebtTest() {
            CreditCard card = new CreditCard("Credit card", 100, 5);
            assertEquals(card.getDebt(), 0.0);
        }

        @ParameterizedTest
        @ValueSource(doubles = {-10, 0, 2, 100, 101})
        void validateInterestRateTest(double interestRate) {
            CreditCard card = new CreditCard("Credit card", 100, 10);
            try {
                Method validateInterestRateMethod = card.getClass()
                        .getDeclaredMethod("validateInterestRate", double.class);
                validateInterestRateMethod.setAccessible(true);
                if (interestRate >= 0 && interestRate < 100) {
                    assertEquals(validateInterestRateMethod.invoke(card, interestRate), interestRate);
                } else {
                    Throwable ex = assertThrows(Throwable.class,
                            () -> validateInterestRateMethod.invoke(card, interestRate)).getCause();
                    assertTrue(ex instanceof ArgumentValidateException);
                    assertEquals(ex.getMessage(), "Interest rate should be 0<interestRate<100");
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

    @Nested
    class DepositTests {
        @Test
        void closeMethodTest() {
            Deposit deposit = new Deposit("deposit", 100);
            Class<?> clazz = deposit.getClass();

            try {
                Field isActive = clazz.getDeclaredField("isDepositActive");
                isActive.setAccessible(true);
                assertTrue(isActive.getBoolean(deposit));
                deposit.close();
                assertFalse(isActive.getBoolean(deposit));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}