package backend.Card;

import static backend.Card.CardColor.BLACK;
import static backend.Card.CardColor.RED;

public enum CardType {
    CLUB(BLACK, "♣"), DIAMOND(RED, "♦"), HEART(RED, "♥"), SPADE(BLACK, "♠");

    private final CardColor color;
    private final String string;

    CardType(CardColor color, String string) {
        this.color = color;
        this.string = string;
    }

    CardColor getColor() {
        return this.color;
    }

    @Override
    public String toString() {
        return this.string;
    }
}
