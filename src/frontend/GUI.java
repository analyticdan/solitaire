package frontend;

import backend.Board;
import backend.Card.Card;
import backend.Card.CardType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

import static backend.Board.TABLEAU_SIZE;
import static backend.Board.WASTE_SIZE;


public class GUI extends JPanel implements Runnable {
    static private final Color BACKGROUND_COLOR = Color.WHITE;
    static private final Color OUTLINE_COLOR = Color.GRAY;
    static private final Color HIDDEN_COLOR = Color.GREEN;
    static private final Color EMPTY_COLOR = Color.GRAY;
    static private final Color CARD_COLOR = Color.YELLOW;

    private Board board;

    private double cardHeight;
    private double cardWidth;
    private double marginHeight;
    private double marginWidth;

    private Rectangle2D stock;
    private Rectangle2D[] waste;
    private Rectangle2D[] foundation;
    private Rectangle2D[] tableau;

    private Card selectedCard;

    public GUI(Board board) {
        this.board = board;
        this.board.iterateStock();

        this.waste = new Rectangle2D[WASTE_SIZE];
        this.foundation = new Rectangle2D[CardType.values().length];
        this.tableau = new Rectangle2D[TABLEAU_SIZE];
    }

    @Override
    public void run() {
        JFrame frame = new JFrame();
        frame.setTitle("Solitaire");
        frame.setSize(700, 650);
        frame.setDefaultCloseOperation(WindowConstants. EXIT_ON_CLOSE);
        frame.add(this);
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Recalculate card dimensions and margins based on eyeball heuristics.
                cardHeight = getHeight() / 7.5;
                cardWidth = getWidth() / 11.0;
                marginHeight = cardHeight / 4;
                marginWidth = cardWidth / 3;

                // Recalculate position of stock.
                double x = marginWidth;
                double y = marginHeight;
                stock = new Rectangle2D.Double(x, y, cardWidth, cardHeight);

                // Recalculate position of waste cards.
                x += cardWidth +  marginWidth;
                double deltaX = 0;
                for (int i = 0; i < waste.length; i += 1) {
                    waste[i] = new Rectangle2D.Double(x + deltaX, y, cardWidth, cardHeight);
                    deltaX += 1.5 * marginWidth;
                }

                // Recalculate position of foundation cards.
                x += 3 * (cardWidth +  marginWidth);
                deltaX = 0;
                for (int i = 0; i < foundation.length; i += 1) {
                    foundation[i] = new Rectangle2D.Double(x + deltaX, y, cardWidth, cardHeight);
                    deltaX += cardWidth + marginWidth;
                }

                // Recalculate position of base of tableau cards.
                x = cardWidth + 2 * marginWidth;
                y = cardHeight + 2 * marginHeight;
                deltaX = 0;
                for (int i = 0; i < tableau.length; i += 1) {
                    tableau[i] = new Rectangle2D.Double(x + deltaX, y, cardWidth, cardHeight);
                    deltaX += cardWidth + marginWidth;
                }

            }
        });
        frame.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        this.setBackground(BACKGROUND_COLOR);
        this.drawStock(g2d);
        this.drawWaste(g2d);
        this.drawFoundation(g2d);
        this.drawTableau(g2d);
    }

    private void drawStock(Graphics2D g2d) {
        if (this.board.isStockEmpty()) {
            this.drawEmpty(g2d, this.stock);
        } else {
            this.drawCard(g2d, this.stock, null, false);
        }
    }

    private void drawWaste(Graphics2D g2d) {
        for (int i = 0; i < this.waste.length; i += 1) {
            Card card = this.board.getWasteCard(i);
            if (card != null && card != this.selectedCard) {
                this.drawCard(g2d, waste[i], card, true);
            }
        }
    }

    private void drawFoundation(Graphics2D g2d) {
        for (int i = 0; i < this.foundation.length; i += 1) {
            Card card = this.board.getFoundationCard(i);
            if (card == null) {
                this.drawEmpty(g2d, this.foundation[i]);
            } else {
                this.drawCard(g2d, this.foundation[i], card, true);
            }
        }
    }

    private void drawTableau(Graphics2D g2d) {
        for (int i = 0; i < this.tableau.length; i += 1) {
            this.drawEmpty(g2d, this.tableau[i]);
        }
    }

    private void drawCard(Graphics2D g2d, Rectangle2D r, Card card, boolean isRevealed) {
        if (isRevealed) {
            g2d.setColor(CARD_COLOR);
            g2d.fill(r);
            g2d.setColor(getColor(card));
            g2d.drawString(card.toString(), (float) r.getX(), (float) (r.getY() + 15));
        } else {
            g2d.setColor(HIDDEN_COLOR);
            g2d.fill(r);
        }

        g2d.setColor(OUTLINE_COLOR);
        g2d.draw(r);
    }

    private void drawEmpty(Graphics2D g2d, Rectangle2D r) {
        g2d.setColor(EMPTY_COLOR);
        g2d.fill(r);

        g2d.setColor(OUTLINE_COLOR);
        g2d.draw(r);
    }

    private static Color getColor(Card card) {
        switch (card.getType()) {
            case HEART:
            case DIAMOND:
                return Color.RED;
            case CLUB:
            case SPADE:
                return Color.BLACK;
            default:
                return null;
        }
    }
}
