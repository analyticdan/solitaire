package frontend.UICard;

import backend.Board;
import backend.Card.Card;
import frontend.Location;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static frontend.Utils.drawCard;

public class WasteCard extends UICard {

    public WasteCard(Board board, int[] metadata) {
        super(board, Location.WASTE, metadata);
    }

    @Override
    public void draw(Graphics2D g2d, double x, double y, double cardHeight, double cardWidth, double marginHeight, double marginWidth) {
        Rectangle2D r = new Rectangle2D.Double(x, y, cardWidth, cardHeight);
        int[] metadata = this.getMetadata();
        Board board = this.getBoard();
        drawCard(g2d, r, board.getWasteCard(metadata[0]));
    }
}
