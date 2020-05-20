package frontend;

import backend.Board;
import backend.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

public class GUI extends JPanel implements Runnable {

    static private final Color BACKGROUND_COLOR = Color.WHITE;
    static private final Color OUTLINE_COLOR = Color.GRAY;
    static private final Color HIDDEN_COLOR = Color.GREEN;
    static private final Color EMPTY_COLOR = Color.GRAY;
    static private final Color CARD_COLOR = Color.YELLOW;

    private Board board;

    /* Fields to track which deck is selected to be acted upon.  */
    private Board.Deck selectedDeck;
    private int selectedIndex;
    private Card selectedCard;

    /* Fields to ensure that drag and drop looks and functions correctly. */
    private double selectedOffsetX;
    private double selectedOffsetY;
    private Thread selectedRenderer;

    /* Rectangles tracking the position of all relevant spaces on the board. */
    private Rectangle2D stock;
    private Rectangle2D[] tableau;
    private Rectangle2D[] waste;
    private Rectangle2D[] foundation;

    public GUI(Board board) {
        this.board = board;

        this.stock = null;
        this.tableau = new Rectangle2D[Board.Deck.TABLEAU.size()];
        this.waste = new Rectangle2D[Board.Deck.WASTE.size()];
        this.foundation = new Rectangle2D[Board.Deck.FOUNDATION.size()];

        /* Mouse listener to implement drag-and-drop features. */
        addMouseListener(new MouseAdapter() {
                             @Override
                             public void mousePressed(MouseEvent e) {
                                 GUI.this.handlePressed(e.getPoint());
                                 GUI.this.selectedRenderer = new Thread(() -> {
                                     while (GUI.this.selectedCard != null) {
                                         GUI.this.repaint();
                                     }
                                 });
                                 GUI.this.selectedRenderer.start();
                             }
                             @Override
                             public void mouseReleased(MouseEvent e) {
                                 GUI.this.handleReleased(e.getPoint());
                                 repaint();
                             }
                         });
    }

    /** Set up internal state to handle dragging. */
    private void handlePressed(Point point) {
        for (int i = Board.Deck.TABLEAU.size() - 1; i >= 0; i -= 1) {
            if (this.tableau[i].contains(point)) {
                this.select(Board.Deck.TABLEAU, i);
                this.selectedCard = this.board.getTableau(i);
                this.selectedOffsetX = this.tableau[i].getX() - point.getX();
                this.selectedOffsetY = this.tableau[i].getY() - point.getY();
                return;
            }
        }
        for (int i = Board.Deck.WASTE.size() - 1; i >= 0; i -= 1) {
            if (this.waste[i].contains(point)) {
                this.select(Board.Deck.WASTE, i);
                this.selectedCard = this.board.getWaste(this.selectedIndex);
                this.selectedOffsetX = this.waste[i].getX() - point.getX();
                this.selectedOffsetY = this.waste[i].getY() - point.getY();
                return;
            }
        }
        this.select(null, -1);
    }

    /** Perform drag-and-drop action. */
    private void handleReleased (Point point) {
        if (this.stock.contains(point)) {
            this.board.revealStock();
            return;
        }
        for (int i = Board.Deck.TABLEAU.size() - 1; i >= 0; i -= 1) {
            if (this.tableau[i].contains(point)) {
                this.select(Board.Deck.TABLEAU, i);
                return;
            }
        }
        for (int i = Board.Deck.WASTE.size() - 1; i >= 0; i -= 1) {
            if (this.waste[i].contains(point)) {
                this.select(Board.Deck.WASTE, i);
                return;
            }
        }
        for (int i = Board.Deck.FOUNDATION.size() - 1; i >= 0; i -= 1) {
            if (this.foundation[i].contains(point)) {
                this.select(Board.Deck.FOUNDATION, i);
                return;
            }
        }
        this.select(null, -1);
    }

    /** Set tracking fields upon click.  */
    private void select(Board.Deck deck, int i) {
        if (this.selectedDeck != null) {
            this.board.move(this.selectedDeck, this.selectedIndex, deck, i);
            this.selectedDeck = null;
            this.selectedIndex = -1;
            this.selectedCard = null;
        } else if (deck == Board.Deck.TABLEAU) {
            this.selectedDeck = deck;
            this.selectedIndex = i;
        } else if (deck == Board.Deck.WASTE) {
            this.selectedDeck = deck;
            this.selectedIndex = i;
        }
    }

    @Override
    public void run() {
        JFrame frame = new JFrame("Solitaire");
        frame.setSize(700, 650);
        frame.setDefaultCloseOperation(WindowConstants. EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        /* Calculate some heuristics for good looking widths and heights
           from the current window size. */
        double separationWidth = this.getWidth() / 33.0;
        double cardWidth = separationWidth * 3;

        double separationHeight = this.getHeight() / 30.0;
        double cardHeight = separationHeight * 4;

        this.setBackground(BACKGROUND_COLOR);

        /* Draw the stock. */
        this.drawStock(g2d, separationWidth, separationHeight,
                       cardWidth, cardHeight);

        /* Calculate the location of the tableau and draw it. */
        double tableauX = separationWidth + cardWidth + separationWidth;
        double tableauY = separationHeight + cardHeight + separationHeight;
        this.drawTableau(g2d, tableauX, tableauY, cardWidth, cardHeight,
                         separationWidth, separationHeight);

        /* Calculate the location of the waste and draw it. */
        double wasteX = separationWidth + cardWidth + separationWidth;
        this.drawWaste(g2d, wasteX, separationHeight, cardWidth, cardHeight,
                       separationWidth);

        /* Calculate the location of the foundation and draw it. */
        double foundationX = 4 * (separationWidth + cardWidth) + separationWidth;
        this.drawFoundation(g2d, foundationX, separationHeight,
                            cardWidth, cardHeight, separationWidth);

        /* Draw the currently selected card being dragged. */
        if (this.selectedCard != null) {
            Point point = this.getMousePosition();
            double selectedX = this.selectedOffsetX + point.getX();
            double selectedY = this.selectedOffsetY + point.getY();
            Rectangle2D r = new Rectangle2D.Double(selectedX, selectedY,
                                                   cardWidth, cardHeight);
            this.drawCard(g2d, this.selectedCard, r);
        }
    }

    /* Draw the stock using G2D when the stock
       begins at the coordinates (X, Y),
       and each card has a width of CARDWIDTH and a height of CARDHEIGHT. */
    private void drawStock(Graphics2D g2d, double x, double y,
                           double cardWidth, double cardHeight) {

        /* Set the location of the stock. */
        this.stock = new Rectangle2D.Double(x, y, cardWidth, cardHeight);

        if (this.board.isCurrentStockEmpty()) {
            g2d.setColor(EMPTY_COLOR);
        } else {
            g2d.setColor(HIDDEN_COLOR);
        }
        g2d.fill(this.stock);
        g2d.setColor(OUTLINE_COLOR);
        g2d.draw(this.stock);
    }

    /** Draw the tableau cards using G2D when the leftmost tableau
        begins at the coordinates (X, Y),
        each card has a width of CARDWIDTH and a height of CARDHEIGHT,
        and there is a separation of SEPARATIONWIDRTH between tableaus and
        a separation of SEPARATIONHEIGHT between cards of the same tableau. */
    private void drawTableau(Graphics2D g2d, double x, double y,
                             double cardWidth, double cardHeight,
                             double separationWidth, double separationHeight) {
        for (int i = 0; i < Board.Deck.TABLEAU.size(); i += 1) {
            Iterator tableauIterator = this.board.getTableauIterator(i);

            /* Draw base tableau card. */
            Rectangle2D tableau =
                new Rectangle2D.Double(x + i * (cardWidth + separationWidth), y,
                                       cardWidth, cardHeight);
            g2d.setColor(EMPTY_COLOR);
            g2d.fill(tableau);
            g2d.setColor(OUTLINE_COLOR);
            g2d.draw(tableau);

            /* Draw the other tableau cards on top of that tabelau card. */
            for (int j = 0; tableauIterator.hasNext(); j += 1) {
                Card card = (Card) tableauIterator.next();
                tableau = new Rectangle2D.Double(tableau.getX(),
                                                 y + j * separationHeight,
                                                 cardWidth, cardHeight);
                if (card != selectedCard) {
                    this.drawCard(g2d, card, tableau);
                }
            }

            /* Set the location of the top card of this tableau. */
            this.tableau[i] = tableau;
        }
    }

    /** Draw the waste cards using G2D when the leftmost waste card
        begins at the coordinates (X, Y),
        each card has a width of CARDWIDTH and a height of CARDHEIGHT,
        and there is a separation of SEPARATIONWIDRTH between cards. */
    private void drawWaste(Graphics2D g2d, double x, double y,
                           double cardWidth, double cardHeight,
                           double separationWidth) {
        Iterator<Card> wasteIterator = this.board.getWasteIterator();
        for (int i = 0; wasteIterator.hasNext(); i += 1) {
            Card card = wasteIterator.next();

            /* Set the location of this waste card. */
            this.waste[i] =
                new Rectangle2D.Double(x + (1.5 * i) * separationWidth, y,
                                       cardWidth, cardHeight);

            /* Draw the card if it exists and is not being dragged. */
            if (card != null && card != this.selectedCard) {
                this.drawCard(g2d, card, this.waste[i]);
            }
        }
    }

    /** Draw the foundation cards using G2D when the foundation
        begins at the coordinates (X, Y),
        each card has a width of CARDWIDTH and a height of CARDHEIGHT,
        and there is a separation of SEPARATIONWIDRTH between cards. */
    private void drawFoundation(Graphics2D g2d, double x, double y,
                                double cardWidth, double cardHeight,
                                double separationWidth) {
        Iterator<Card> foundationIterator = this.board.getFoundationIterator();
        for (int i = 0; foundationIterator.hasNext(); i += 1) {
            Card card = foundationIterator.next();

            /* Set the location of this foundation card. */
            this.foundation[i] =
                new Rectangle2D.Double(x + (cardWidth + separationWidth) * i, y,
                                       cardWidth, cardHeight);

            /* Draw the card if it exists, otherwise leave an empty space
               to signify that this foundation column is empty. */
            if (card != null) {
                this.drawCard(g2d, card, this.foundation[i]);
            } else {
                g2d.setColor(EMPTY_COLOR);
                g2d.fill(this.foundation[i]);
                g2d.setColor(OUTLINE_COLOR);
                g2d.draw(this.foundation[i]);
            }
        }
    }

    /** Macro to draw CARD using G2D at the location specified by R. */
    private void drawCard(Graphics2D g2d, Card card, Rectangle2D r) {
        if (card.isRevealed()) {
            /* Fill the background of the card. */
            g2d.setColor(CARD_COLOR);
            g2d.fill(r);

            /* Draw the string that identifies the card at approximately
               the top left corner. */
            g2d.setColor(card.getColor());
            g2d.drawString(this.cardToString(card),
                           (float) r.getX(), (float) (r.getY() + 15));
        } else {
            g2d.setColor(HIDDEN_COLOR);
            g2d.fill(r);
        }
        g2d.setColor(OUTLINE_COLOR);
        g2d.draw(r);
    }

    /** Get the string that uniquely identifies CARD for this GUI. */
    private String cardToString(Card card) {
        StringBuilder result = new StringBuilder();
        if (card.isRevealed()) {
            switch (card.getValue()) {
                case 0:
                    result.append('A');
                    break;
                case 10:
                    result.append('J');
                    break;
                case 11:
                    result.append('Q');
                    break;
                case 12:
                    result.append('K');
                    break;
                default:
                    result.append(card.getValue() + 1);
            }
            switch (card.getType()) {
                case HEART:
                    result.append("♥");
                    break;
                case DIAMOND:
                    result.append("♦");
                    break;
                case CLUB:
                    result.append("♣");
                    break;
                case SPADE:
                    result.append("♠");
                    break;
            }
        }
        return result.toString();
    }
}
