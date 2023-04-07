package backend;

import backend.Card.Card;

import java.util.LinkedList;

public class CardColumn {
    private LinkedList<Card> hiddenCards;
    private LinkedList<Card> revealedCards;

    CardColumn(LinkedList<Card> hiddenCards, LinkedList<Card> revealedCards) {
        this.hiddenCards = hiddenCards;
        this.revealedCards = revealedCards;
    }

    public Card[] getView() {
        Card[] view = new Card[this.hiddenCards.size() + this.revealedCards.size()];
        for (int i = 0; i < this.revealedCards.size(); i += 1) {
            view[this.hiddenCards.size() + i] = this.revealedCards.get(i);
        }
        return view;
    }

    public Card getCard(int i) {
        if (i < this.hiddenCards.size()) {
            return null;
        } else {
            return this.revealedCards.get(i - this.hiddenCards.size());
        }
    }
}
