package backend.Card;

public enum CardValue {
    A("A"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("J"),
    QUEEN("Q"),
    KING("K");

    private final String string;

    CardValue(String string) {
        this.string = string;
    }

    public CardValue prev() {
        if (this == A) {
            return null;
        } else {
            return CardValue.values()[this.ordinal() - 1];
        }
    }

    public CardValue next() {
        if (this == KING) {
            return null;
        } else {
            return CardValue.values()[this.ordinal() - 1];
        }
    }

    @Override
    public String toString() {
        return this.string;
    }
}
