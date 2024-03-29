package backend;

import backend.Card.Card;
import backend.Card.CardType;
import backend.Card.CardValue;

import java.util.Collections;
import java.util.LinkedList;

public class Board {
    public static final int TABLEAU_SIZE = 7;
    public static final int WASTE_SIZE = 3;

    /* Stock cards are the face down cards in the upper left corner. */
    private LinkedList<Card> stock;

    /* Waste cards are the (up-to) three revealed cards from the stock. */
    private Card[] waste;

    /* Foundation cards are the four "finished" piles in the top right corner. */
    private Card[] foundation;

    /* The tableau is the main grid of cards that are still in play. */
    private CardColumn[] tableau;

    public Board() {
        /* Initialize a shuffled deck of cards. */
        LinkedList<Card> deck = new LinkedList<>();
        for (CardType type : CardType.values()) {
            for (CardValue value : CardValue.values()) {
                deck.push(new Card(type, value));
            }
        }
        Collections.shuffle(deck);

        /* Initialize an empty foundation. */
        this.foundation = new Card[CardType.values().length];

        /* Deal out cards to the tableau from the deck. */
        this.tableau = new CardColumn[TABLEAU_SIZE];
        for (int i = 0; i < this.tableau.length; i += 1) {
            LinkedList<Card> hiddenCards = new LinkedList<>();
            LinkedList<Card> revealedCards = new LinkedList<>();
            for (int j = 0; j < i; j += 1) {
                hiddenCards.push(deck.pop());
            }
            revealedCards.push(deck.pop());
            tableau[i] = new CardColumn(hiddenCards, revealedCards);
        }

        /*
         * Initialize the stock with the remaining cards from the deck, adding a
         * null-terminator.
         */
        this.stock = deck;
        this.stock.addLast(null);

        /* Initialize an empty waste. */
        this.waste = new Card[WASTE_SIZE];
    }

    /**
     * Put the current waste cards at the end of the stock.
     * Then:
     * - if the next element of the stock is null, put the null-terminator at the
     * end of the stock.
     * - if the next element of the stock is not null, pop up to three elements from
     * the stock onto the waste.
     */
    public void iterateStock() {
        for (int i = 0; i < this.waste.length; i += 1) {
            Card wasteCard = this.waste[i];
            if (wasteCard != null) {
                this.stock.addLast(wasteCard);
            }
            this.waste[i] = null;
        }

        if (this.stock.peekFirst() == null) {
            this.stock.addLast(this.stock.removeFirst());
        } else {
            for (int i = 0; i < this.waste.length && this.stock.peekFirst() != null; i += 1) {
                this.waste[i] = this.stock.removeFirst();
            }
        }
    }

    public boolean isStockEmpty() {
        return this.stock.peekFirst() == null;
    }

    public Card getWasteCard(int i) {
        return this.waste[i];
    }

    public Card getFoundationCard(int i) {
        return this.foundation[i];
    }

    public Card[] getTableauCards(int column) {
        return this.tableau[column].getView();
    }

    public Card getTableauCard(int column, int row) {
        return this.tableau[column].getCard(row);
    }

    public boolean isMovableToFoundation(Card card, int foundation) {
        Card foundationCard = this.foundation[foundation];
        if (foundationCard == null) {
            return card.getValue() == CardValue.A;
        } else {
            return card.getType() == foundationCard.getType() && card.getValue() == foundationCard.getValue().next();
        }
    }

    public void moveFromWasteToFoundation(int waste, int foundation) {
        Card wasteCard = this.waste[waste];
        if (wasteCard == null) {
            return;
        }
        if (this.isMovableToFoundation(wasteCard, foundation)) {
            this.foundation[foundation] = wasteCard;
            this.waste[waste] = null;
        }
    }

    public void moveFromTableauToFoundation(int column, int foundation) {
        Card tableauCard = this.tableau[column].peek();
        if (tableauCard == null) {
            return;
        }
        if (this.isMovableToFoundation(tableauCard, foundation)) {
            this.foundation[foundation] = this.tableau[column].pop();
        }
    }

    public boolean isMovableToTableau(Card card, int column) {
        Card tableauCard = this.tableau[column].peek();
        if (tableauCard == null) {
            return true;
        } else {
            return card.getColor() != tableauCard.getColor();
        }
    }

    public void moveFromWasteToTableau(int waste, int column) {
        Card wasteCard = this.waste[waste];
        if (wasteCard == null) {
            return;
        }
        if (isMovableToTableau(wasteCard, column)) {
            this.tableau[column].push(wasteCard);
            this.waste[waste] = null;
        }
    }

    public void moveFromTableauToTableau(int from, int to) {
        Card card = this.tableau[from].peek();
        if (card == null) {
            return;
        }
        if (isMovableToTableau(card, to)) {
            this.tableau[to].push(this.tableau[from].pop());
        }
    }

}
