package backend;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class Board {

    private LinkedList<Card> curStock;
    private LinkedList<Card> nextStock;
    private LinkedList<Card>[] tableau;
    private Card[] waste;
    private Card[] foundation;

    public enum Deck {
        STOCK(0), TABLEAU(7), WASTE(3), FOUNDATION(4);

        private int length;

        Deck(int length) {
            this.length = length;
        }

        public int size() {
            return this.length;
        }
    }

    @SuppressWarnings("unchecked")
    public Board() {
        this.curStock = new LinkedList<>();
        this.nextStock = new LinkedList<>();
        for (Card.Type type : Card.Type.values()) {
            for (int value = 0; value < Card.MAX_VALUE; value += 1) {
                curStock.push(new Card(type, value));
            }
        }
        Collections.shuffle(this.curStock);

        this.tableau = new LinkedList[Deck.TABLEAU.size()];
        for (int i = 0; i < Deck.TABLEAU.size(); i += 1) {
            this.tableau[i] = new LinkedList<>();
            for (int j = 0; j < i + 1; j += 1) {
                this.tableau[i].push(this.curStock.pop());
            }
            this.tableau[i].getLast().setRevealed(true);
        }

        this.waste = new Card[Deck.WASTE.size()];
        this.foundation = new Card[Deck.FOUNDATION.size()];
    }

    public boolean isCurrentStockEmpty() {
        return this.curStock.isEmpty();
    }

    public Iterator<Card> getTableauIterator(int i) {
        return this.tableau[i].listIterator(0);
    }
    public Card getTableau(int i) {
        return this.tableau[i].getLast();
    }

    public Iterator<Card> getWasteIterator() {
        return Arrays.stream(this.waste).iterator();
    }
    public Card getWaste(int i) {
        return this.waste[i];
    }

    public Iterator<Card> getFoundationIterator() {
        return Arrays.stream(this.foundation).iterator();
    }
    public Card getFoundation(int i) {
        return this.foundation[i];
    }

    public void revealStock() {
        Card card;

        for (int i = 0; i < Deck.WASTE.size(); i += 1) {
            card = this.waste[i];
            if (card != null) {
                card.setRevealed(false);
                this.nextStock.addLast(card);
            }
            this.waste[i] = null;
        }

        if (this.curStock.isEmpty()) {
            LinkedList<Card> tmp = this.curStock;
            this.curStock = this.nextStock;
            this.nextStock = tmp;
        }

        for (int i = 0; i < Deck.WASTE.size() && !this.curStock.isEmpty(); i += 1) {
            card = this.curStock.removeFirst();
            card.setRevealed(true);
            this.waste[i] = card;
        }
    }

    public void move(Deck src, int srcIndex, Deck dst, int dstIndex) {
        assert src == Deck.TABLEAU ^ src == Deck.WASTE;
        assert dst == Deck.TABLEAU ^ dst == Deck.FOUNDATION;
        assert srcIndex >= 0 && srcIndex <= src.size();
        assert dstIndex >= 0 && dstIndex <= dst.size();

        Card srcCard = this.getCard(src, srcIndex);
        Card dstCard = this.getCard(dst, dstIndex);
        boolean move = false;

        if (srcCard != null) {
            if (dstCard == null) {
                move = (dst == Deck.TABLEAU || (dst == Deck.FOUNDATION && srcCard.getValue() == 0));
            } else {
                int dstValue = dstCard.getValue();
                int srcValue = srcCard.getValue();
                move = (dst == Deck.TABLEAU && dstCard.getColor() != srcCard.getColor() && dstValue == srcValue + 1) ||
                        (dst == Deck.FOUNDATION && dstCard.getType() == srcCard.getType() && dstValue + 1 == srcValue);
            }
        }

        if (move) {
            if (src == Deck.TABLEAU) {
                this.tableau[srcIndex].removeLast();
                if (!this.tableau[srcIndex].isEmpty()) {
                    this.tableau[srcIndex].getLast().setRevealed(true);
                }
            } else {
                this.waste[srcIndex] = null;
            }

            if (dst == Deck.TABLEAU) {
                this.tableau[dstIndex].addLast(srcCard);
            } else {
                this.foundation[dstIndex] = srcCard;
            }
        }

    }

    private Card getCard(Deck deck, int i) {
        if (deck == Deck.TABLEAU) {
            return this.tableau[i].isEmpty() ? null : this.tableau[i].getLast();
        } else if (deck == Deck.FOUNDATION) {
            return this.foundation[i];
        } else if (deck == Deck.WASTE) {
            return this.waste[i];
        }
        return null;
    }
}
