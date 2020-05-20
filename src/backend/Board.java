package backend;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class Board {

	/* Stock cards (face down cards in the upper left corner).
	   After removing cards from curStock, add them back to nextStock.
	   When curStock is empty, switch to nextStock.

	   Using two stocks allows us to have an empty deck when we reveal
	   the last three stock cards without needing filler cards. */
    private LinkedList<Card> curStock;
    private LinkedList<Card> nextStock;

    /* Tableaus (the bottom row of cards, which are prepopulated). */
    private LinkedList<Card>[] tableau;

    /* Waste cards (the up-to three cards revealed from the stock). */
    private Card[] waste;

    /* Foundation cards (the four piles representing cards which are done). */
    private Card[] foundation;

    /* A Deck class, which provides helpful constants. */
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

    /** Initializes a Solitaire board. */
    @SuppressWarnings("unchecked")
    public Board() {
    	/* Initialize stock to be a shuffled 52-card deck. */
        this.curStock = new LinkedList<>();
        this.nextStock = new LinkedList<>();
        for (Card.Type type : Card.Type.values()) {
            for (int value = 0; value < Card.MAX_VALUE; value += 1) {
                curStock.push(new Card(type, value));
            }
        }
        Collections.shuffle(this.curStock);

        /* Deal out cards to the tableau from the stock. */
        this.tableau = new LinkedList[Deck.TABLEAU.size()];
        for (int i = 0; i < Deck.TABLEAU.size(); i += 1) {
            this.tableau[i] = new LinkedList<>();
            for (int j = 0; j < i + 1; j += 1) {
                this.tableau[i].push(this.curStock.pop());
            }
            this.tableau[i].getLast().setRevealed(true);
        }

        /* Initialize an empty waste and foundation. */
        this.waste = new Card[Deck.WASTE.size()];
        this.foundation = new Card[Deck.FOUNDATION.size()];
    }

    /** Returns true iff curStock is empty. */
    public boolean isCurrentStockEmpty() {
        return this.curStock.isEmpty();
    }

    /** Returns an iterator for the tableaus,
        starting from the leftmost tableau. */
    public Iterator<Card> getTableauIterator(int i) {
        return this.tableau[i].listIterator(0);
    }

    /** Returns the last card of the i-th tableau from the left. */
    public Card getTableau(int i) {
        return this.tableau[i].getLast();
    }

    /** Returns an iterator that iterates through the waste
        from left to right. */
    public Iterator<Card> getWasteIterator() {
        return Arrays.stream(this.waste).iterator();
    }

    /** Returns the i-th waste card from the left. */
    public Card getWaste(int i) {
        return this.waste[i];
    }

    /** Returns an iterator that iterates through the top card
        of each foundation pile, from left to right. */
    public Iterator<Card> getFoundationIterator() {
        return Arrays.stream(this.foundation).iterator();
    }

    /** Returns the top card of the i-th foundation pile from the left. */
    public Card getFoundation(int i) {
        return this.foundation[i];
    }

    /** Puts the current waste cards back into the stock.
        Then, takes up to three cards from the stock to
        repopulate the waste. */
    public void revealStock() {
        Card card;

        /* Push waste cards onto nextStock. */
        for (int i = 0; i < Deck.WASTE.size(); i += 1) {
            card = this.waste[i];
            if (card != null) {
                card.setRevealed(false);
                this.nextStock.addLast(card);
            }
            this.waste[i] = null;
        }

        /* If curStock is empty, swap it with nextStock. */
        if (this.curStock.isEmpty()) {
            LinkedList<Card> tmp = this.curStock;
            this.curStock = this.nextStock;
            this.nextStock = tmp;
        }

        /* Put three cards into the waste pile. */
        for (int i = 0; i < Deck.WASTE.size() && !this.curStock.isEmpty(); i += 1) {
            card = this.curStock.removeFirst();
            card.setRevealed(true);
            this.waste[i] = card;
        }
    }

    /** Moves the top card a tableau row or a waste card to
        either a tableau row or a foundation pile. */
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
                move = ((dst == Deck.TABLEAU  && srcCard.getValue() == Card.MAX_VALUE - 1) ||
                        (dst == Deck.FOUNDATION && srcCard.getValue() == 0));
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

    /* Returns the top i-th from the left card from DECK. */
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
