package backend;

import java.awt.*;

import static java.awt.Color.RED;
import static java.awt.Color.BLACK;

public class Card {

    /* Cards go from A, 2, ..., 10, J, Q, K. */
    static final int MAX_VALUE = 13;

    public enum Type {
        HEART(RED), DIAMOND(RED), CLUB(BLACK), SPADE(BLACK);

        private Color color;

        Type(Color color) {
            this.color = color;
        }

        private Color getColor() {
            return this.color;
        }
    }

    private Type type;
    private int value;
    private boolean revealed;

    Card(Type type, int value) {
        this.type = type;
        this.value = value;
        this.revealed = false;
    }

    public Type getType() {
        if (this.revealed) {
            return this.type;
        }
        return null;
    }

    public Color getColor() {
        if (this.revealed) {
            return this.type.getColor();
        }
        return null;
    }

    public int getValue() {
        if (this.revealed) {
            return this.value;
        }
        return -1;
    }

    public boolean isRevealed() {
        return this.revealed;
    }

    void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    @Override
    public String toString() {
        String value;
        switch (this.value) {
            case 0:
                value = "A";
                break;
            case 10:
                value = "J";
                break;
            case 11:
                value = "Q";
                break;
            case 12:
                value = "K";
                break;
            default:
                value = Integer.toString(this.value + 1);
        }
        return this.type.name().charAt(0) + value;
    }
}
