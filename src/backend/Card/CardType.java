package backend.Card;

public enum CardType {
    HEART("♥"), DIAMOND("♦"), CLUB("♣"), SPADE("♠");

    private final String string;

    CardType(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }
}
