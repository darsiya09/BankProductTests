package products.cards;

import java.util.Currency;

public class DebitCard extends Card {

    public DebitCard(String name, double balance) {
        super(name, balance);
        this.currency = Currency.getInstance("RUR");
    }
}
