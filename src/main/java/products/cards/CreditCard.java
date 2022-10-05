package products.cards;

import products.exceptions.ArgumentValidateException;

import java.util.Currency;

public class CreditCard extends Card {

    private double interestRate;


    public CreditCard(String name, double balance, double interestRate) {
        super(name, balance);
        this.interestRate = validateInterestRate(interestRate);
        this.currency = Currency.getInstance("RUR");
    }

    public double getDebt() {
        return 0.0; // TODO: 03.10.2022 return theMethodReturningTheDebt()
    }

    private double validateInterestRate(double interestRate) {
        if (interestRate >= 0 && interestRate < 100) return interestRate;
        else throw new ArgumentValidateException("Interest rate should be 0<interestRate<100");
    }
}
