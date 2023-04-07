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

    @Override
    public String toString() {
        return this.string;
    }
}
