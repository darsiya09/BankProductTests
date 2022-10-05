package products.cards;

import products.BankProduct;
import products.exceptions.ArgumentValidateException;

public abstract class Card extends BankProduct {
    public Card(String name, double balance) {
        super(name, balance);
    }

    public void writingOff(double amount) {
        if (amount <= 0)
            throw new ArgumentValidateException("Amount should be positive");
        else if (amount <= balance) balance -= amount;
        else throw new ArgumentValidateException("There is no enough money in " +
                    "your account");
    }
}