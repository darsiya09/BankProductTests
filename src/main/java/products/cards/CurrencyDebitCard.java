package products.cards;

import java.util.Currency;

public class CurrencyDebitCard extends Card {

    public CurrencyDebitCard(String name, Currency currency, double balance) {
        super(name, balance);
        this.currency = currency;
    }
}
