package products.deposits;

import products.BankProduct;

import java.util.Currency;

public class Deposit extends BankProduct {

    private boolean isDepositActive;

    public Deposit(String name, double balance) {
        super(name, balance);
        this.currency = Currency.getInstance("RUR");
        this.isDepositActive = true;
    }

    public void close(){
        if (isDepositActive) isDepositActive = false;
    }
}
