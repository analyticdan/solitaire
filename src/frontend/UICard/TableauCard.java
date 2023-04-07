package frontend.UICard;

import backend.Board;
import backend.Card.Card;
import frontend.Location;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static frontend.Utils.drawCard;


public class TableauCard extends UICard {
    public TableauCard(Board board, int[] metadata) {
        super(board, Location.TABLEAU, metadata);
    }

    @Override
    public void draw(Graphics2D g2d, double x, double y, double cardHeight, double cardWidth, double marginHeight, double marginWidth) {
        int[] metadata = this.getMetadata();
        Board board = this.getBoard();
        Card[] cards = board.getTableauCards(metadata[0]);

        double deltaY = 0;
        for (int i = metadata[1]; i < cards.length; i += 1) {
            Rectangle2D r = new Rectangle2D.Double(x, y + deltaY, cardWidth, cardHeight);
            drawCard(g2d, r, cards[i]);
            deltaY += marginHeight;
        }
    }
}
