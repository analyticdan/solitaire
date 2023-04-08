package frontend;

import backend.Card.Card;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static backend.Card.CardColor.BLACK;

public class Utils {
    static public final Color BACKGROUND_COLOR = Color.WHITE;
    static public final Color OUTLINE_COLOR = Color.GRAY;
    static public final Color HIDDEN_COLOR = Color.GREEN;
    static public final Color PLACEHOLDER_COLOR = Color.GRAY;
    static public final Color CARD_COLOR = Color.YELLOW;

    static public void drawCard(Graphics2D g2d, Rectangle2D r, Card card) {
        if (card == null) {
            g2d.setColor(HIDDEN_COLOR);
            g2d.fill(r);
        } else {
            g2d.setColor(CARD_COLOR);
            g2d.fill(r);
            g2d.setColor(getColor(card));
            g2d.drawString(card.toString(), (float) r.getX(), (float) (r.getY() + 15));
        }

        g2d.setColor(OUTLINE_COLOR);
        g2d.draw(r);
    }

    static public void drawPlaceholder(Graphics2D g2d, Rectangle2D r) {
        g2d.setColor(PLACEHOLDER_COLOR);
        g2d.fill(r);

        g2d.setColor(OUTLINE_COLOR);
        g2d.draw(r);
    }

    static public Color getColor(Card card) {
        return card.getColor() == BLACK ? Color.BLACK : Color.RED;
    }
}
