package products;

import products.exceptions.ArgumentValidateException;

import java.util.Currency;

public abstract class BankProduct {

    protected String name;
    protected Currency currency;
    protected double balance;

    public BankProduct(String name, double balance) {
        this.name = name;
        this.balance = validateBalance(balance);
    }

    public void replenishment(double payment) {
        if (payment > 0) balance += payment;
        else throw new ArgumentValidateException("Payment should be positive");
    }

    public double getBalance() {
        return balance;
    }

    private double validateBalance(double balance) {
        if (balance >= 0) return balance;
        else throw new ArgumentValidateException("Balance should be positive or 0");
    }
}
