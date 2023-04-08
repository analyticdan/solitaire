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

    public Card getCard(int i) {
        int j = this.hiddenCards.size() - i;
        if (j < 0 || j >= this.revealedCards.size()) {
            return null;
        }
        return this.revealedCards.get(j);
    }

    public Card[] getView() {
        Card[] view = new Card[this.hiddenCards.size() + this.revealedCards.size()];
        for (int i = 0; i < this.revealedCards.size(); i += 1) {
            view[this.hiddenCards.size() + i] = this.revealedCards.get(i);
        }
        return view;
    }

    public Card peek() {
        if (this.revealedCards.size() == 0) {
            return null;
        }
        return this.revealedCards.getLast();
    }

    public Card pop() {
        if (this.revealedCards.size() == 0) {
            return null;
        }
        Card result = this.revealedCards.removeLast();
        if (this.revealedCards.size() == 0 && this.hiddenCards.size() != 0) {
            this.revealedCards.push(this.hiddenCards.pop());
        }
        return result;
    }

    public void push(Card card) {
        this.revealedCards.addLast(card);
    }
}
