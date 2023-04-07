package backend.Card;

public class Card {
    private CardType type;
    private CardValue value;

    public Card(CardType type, CardValue value) {
        this.type = type;
        this.value = value;
    }

    public CardType getType() {
        return this.type;
    }

    public CardValue getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value.toString() + this.type.toString();
    }
}
