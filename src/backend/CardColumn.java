package backend;

import backend.Card.Card;
import backend.Card.CardType;
import backend.Card.CardValue;

import java.util.LinkedList;

public class CardColumn {
    private LinkedList<Card> hiddenCards;
    private LinkedList<Card> revealedCards;

    CardColumn(LinkedList<Card> hiddenCards, LinkedList<Card> revealedCards) {
        this.hiddenCards = hiddenCards;
        this.revealedCards = revealedCards;
    }

    public int getSize() {
        return this.hiddenCards.size() + this.revealedCards.size();
    }

    public Card[] getView() {
        Card[] view = new Card[this.hiddenCards.size() + this.revealedCards.size()];
        for (int i = 0; i < this.revealedCards.size(); i += 1) {
            view[this.hiddenCards.size() + i] = this.revealedCards.get(i);
        }
        return view;
    }

    public boolean isMovableFrom(int i) {
        int j = i - this.hiddenCards.size();

        if (j < 0) {
            return false;
        }

        Card start = this.revealedCards.get(j);
        CardType type = start.getType();
        CardValue value = start.getValue();
        for (int k = j + 1; k < this.revealedCards.size(); k += 1) {
            Card card = this.revealedCards.get(k);
            if (card.getType() != type || card.getValue().next() != value) {
                return false;
            }
            value = card.getValue().next();
        }
        return true;
    }

    public void removeFrom(int i) {
        int j = i - this.hiddenCards.size();

        while (this.revealedCards.size() > j) {
            this.revealedCards.removeLast();
        }

        if (this.revealedCards.size() == 0 && this.hiddenCards.size() != 0) {
            this.revealedCards.push(this.hiddenCards.pop());
        }
    }
}
