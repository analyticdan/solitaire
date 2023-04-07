package frontend;

import backend.Board;
import backend.Card.Card;
import backend.Card.CardType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import static backend.Board.TABLEAU_SIZE;
import static backend.Board.WASTE_SIZE;


public class GUI extends JPanel implements Runnable {
    static private final Color BACKGROUND_COLOR = Color.WHITE;
    static private final Color OUTLINE_COLOR = Color.GRAY;
    static private final Color HIDDEN_COLOR = Color.GREEN;
    static private final Color PLACEHOLDER_COLOR = Color.GRAY;
    static private final Color CARD_COLOR = Color.YELLOW;

    private Board board;

    private double cardHeight;
    private double cardWidth;
    private double marginHeight;
    private double marginWidth;

    private Rectangle2D stock;
    private Rectangle2D[] waste;
    private Rectangle2D[] foundation;
    private Rectangle2D[][] tableau;

    private Card[] selectedCards;
    private double offsetX;
    private double offsetY;

    public GUI(Board board) {
        this.board = board;
        this.board.iterateStock();

        this.waste = new Rectangle2D[WASTE_SIZE];
        this.foundation = new Rectangle2D[CardType.values().length];
        this.tableau = new Rectangle2D[TABLEAU_SIZE][];

        this.addComponentListener(new ComponentAdapter() {
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
            }
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                if (stock.contains(point)) {
                    board.iterateStock();
                    return;
                }
                for (int i = waste.length - 1; i >= 0; i -= 1) {
                    if (waste[i].contains(point)) {
                        selectedCards = new Card[]{ board.getWasteCard(i) };
                        offsetX = point.getX() - waste[i].getX();
                        offsetY = point.getY() - waste[i].getY();
                        return;
                    }
                }

                for (int i = tableau.length - 1; i >= 0; i -= 1) {
                    Rectangle2D[] column = tableau[i];
                    for (int j = column.length - 1; j >= 0; j -= 1) {
                        Card[] cards = board.getTableauCards(i);
                        if (cards[j] == null) {
                            break;
                        }
                        if (column[j].contains(point)) {
                            selectedCards = Arrays.copyOfRange(cards, j, cards.length);
                            offsetX = point.getX() - column[j].getX();
                            offsetY = point.getY() - column[j].getY();
                            return;
                        }
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                selectedCards = null;
                repaint();
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedCards != null) {
                    repaint();
                }
            }
        });
    }

    @Override
    public void run() {
        JFrame frame = new JFrame();
        frame.setTitle("Solitaire");
        frame.setSize(700, 650);
        frame.setDefaultCloseOperation(WindowConstants. EXIT_ON_CLOSE);
        frame.add(this);
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
        this.drawSelectedCard(g2d);
    }

    private boolean isSelectedCard(Card card) {
        return this.selectedCards != null && card == this.selectedCards[0];
    }

    private void drawStock(Graphics2D g2d) {
        if (this.board.isStockEmpty()) {
            this.drawPlaceholder(g2d, this.stock);
        } else {
            this.drawCard(g2d, this.stock, null);
        }
    }

    private void drawWaste(Graphics2D g2d) {
        for (int i = 0; i < this.waste.length; i += 1) {
            Card card = this.board.getWasteCard(i);
            if (card != null && !isSelectedCard(card)) {
                this.drawCard(g2d, waste[i], card);
            }
        }
    }

    private void drawFoundation(Graphics2D g2d) {
        for (int i = 0; i < this.foundation.length; i += 1) {
            Card card = this.board.getFoundationCard(i);
            if (card == null) {
                this.drawPlaceholder(g2d, this.foundation[i]);
            } else {
                this.drawCard(g2d, this.foundation[i], card);
            }
        }
    }

    private void drawTableau(Graphics2D g2d) {
        double x = cardWidth + 2 * marginWidth;
        double y = cardHeight + 2 * marginHeight;
        double deltaX = 0;
        for (int i = 0; i < this.tableau.length; i += 1) {
            Card[] cards = this.board.getTableauCards(i);
            Rectangle2D[] column = new Rectangle2D[cards.length];
            double deltaY = 0;
            for (int j = 0; j < cards.length; j += 1) {
                Rectangle2D r = new Rectangle2D.Double(x + deltaX, y + deltaY, this.cardWidth, this.cardHeight);
                Card card = cards[j];
                if (isSelectedCard((card))) {
                    break;
                } else {
                    this.drawCard(g2d, r, card);
                }
                column[j] = r;
                deltaY += this.marginHeight;
            }
            this.tableau[i] = column;
            deltaX += this.cardWidth + this.marginWidth;
        }
    }

    private void drawSelectedCard(Graphics2D g2d) {
        if (this.selectedCards != null) {
            Point point = this.getMousePosition();
            double x = point.getX() - this.offsetX;
            double y = point.getY() - this.offsetY;
            double deltaY = 0;
            for (Card selectedCard : this.selectedCards) {
                Rectangle2D r = new Rectangle2D.Double(x, y + deltaY, this.cardWidth, this.cardHeight);
                drawCard(g2d, r, selectedCard);
                deltaY += marginHeight;
            }
        }
    }

    private void drawCard(Graphics2D g2d, Rectangle2D r, Card card) {
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

    private void drawPlaceholder(Graphics2D g2d, Rectangle2D r) {
        g2d.setColor(PLACEHOLDER_COLOR);
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
