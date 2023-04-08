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
import static frontend.Utils.BACKGROUND_COLOR;
import static frontend.Utils.drawCard;
import static frontend.Utils.drawPlaceholder;

public class GUI extends JPanel implements Runnable {
    private Board board;

    private Rectangle2D stock;
    private Rectangle2D[] waste;
    private Rectangle2D[] foundation;
    private Rectangle2D[] tableauBase;
    private Rectangle2D[][] tableau;

    private double cardHeight;
    private double cardWidth;
    private double marginHeight;
    private double marginWidth;

    private SelectedCard selectedCard;

    public GUI(Board board) {
        this.board = board;

        this.waste = new Rectangle2D[WASTE_SIZE];
        this.foundation = new Rectangle2D[CardType.values().length];
        this.tableauBase = new Rectangle2D[TABLEAU_SIZE];
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
                x += cardWidth + marginWidth;
                double deltaX = 0;
                for (int i = 0; i < waste.length; i += 1) {
                    waste[i] = new Rectangle2D.Double(x + deltaX, y, cardWidth, cardHeight);
                    deltaX += 1.5 * marginWidth;
                }

                // Recalculate position of foundation cards.
                x += 3 * (cardWidth + marginWidth);
                deltaX = 0;
                for (int i = 0; i < foundation.length; i += 1) {
                    foundation[i] = new Rectangle2D.Double(x + deltaX, y, cardWidth, cardHeight);
                    deltaX += cardWidth + marginWidth;
                }

                // Recalculate position of tableau column bases.
                x = cardWidth + 2 * marginWidth;
                y = cardHeight + 2 * marginHeight;
                deltaX = 0;
                for (int i = 0; i < tableauBase.length; i += 1) {
                    tableauBase[i] = new Rectangle2D.Double(x + deltaX, y, cardWidth, cardHeight);
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
                    Rectangle2D r = waste[i];
                    if (r.contains(point) && board.getWasteCard(i) != null) {
                        double offsetX = point.getX() - r.getX();
                        double offsetY = point.getY() - r.getY();
                        selectedCard = new SelectedCard(Location.WASTE, i, offsetX, offsetY);
                        return;
                    }
                }
                for (int i = 0; i < tableau.length; i += 1) {
                    Rectangle2D[] column = tableau[i];
                    if (column.length != 0) {
                        Rectangle2D r = column[column.length - 1];
                        if (r.contains(point)) {
                            double offsetX = point.getX() - r.getX();
                            double offsetY = point.getY() - r.getY();
                            selectedCard = new SelectedCard(Location.TABLEAU, i, offsetX, offsetY);
                            return;
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedCard != null) {
                    Point point = e.getPoint();
                    for (int i = 0; i < foundation.length; i += 1) {
                        if (foundation[i].contains(point)) {
                            if (selectedCard.getLocation() == Location.WASTE) {
                                board.moveFromWasteToFoundation(selectedCard.getIndex(), i);
                            } else {
                                board.moveFromTableauToFoundation(selectedCard.getIndex(), i);
                            }
                        }
                    }
                    for (int i = 0; i < tableau.length; i += 1) {
                        Rectangle2D[] column = tableau[i];
                        Rectangle2D r;
                        if (column == null || column.length == 0) {
                            r = tableauBase[i];
                        } else {
                            r = column[column.length - 1];
                        }
                        if (r.contains(point)) {
                            if (selectedCard.getLocation() == Location.WASTE) {
                                board.moveFromWasteToTableau(selectedCard.getIndex(), i);
                            } else {
                                board.moveFromTableauToTableau(selectedCard.getIndex(), i);
                            }
                        }
                    }
                    selectedCard = null;
                }
                repaint();
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedCard != null) {
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
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        this.drawSelectedCards(g2d);
    }

    private boolean isSelectedCard(Location location, int index) {
        return this.selectedCard != null && this.selectedCard.getLocation() == location
                && this.selectedCard.getIndex() == index;
    }

    private void drawStock(Graphics2D g2d) {
        if (this.board.isStockEmpty()) {
            drawPlaceholder(g2d, this.stock);
        } else {
            drawCard(g2d, this.stock, null);
        }
    }

    private void drawWaste(Graphics2D g2d) {
        for (int i = 0; i < this.waste.length; i += 1) {
            Card card = this.board.getWasteCard(i);
            if (card != null && !this.isSelectedCard(Location.WASTE, i)) {
                drawCard(g2d, waste[i], card);
            }
        }
    }

    private void drawFoundation(Graphics2D g2d) {
        for (int i = 0; i < this.foundation.length; i += 1) {
            Card card = this.board.getFoundationCard(i);
            if (card == null) {
                drawPlaceholder(g2d, this.foundation[i]);
            } else {
                drawCard(g2d, this.foundation[i], card);
            }
        }
    }

    private void drawTableau(Graphics2D g2d) {
        for (int i = 0; i < this.tableauBase.length; i += 1) {
            Rectangle2D base = this.tableauBase[i];
            drawPlaceholder(g2d, base);

            Card[] cards = this.board.getTableauCards(i);
            Rectangle2D[] column = new Rectangle2D[cards.length];
            this.tableau[i] = column;

            double x = base.getX();
            double y = base.getY();
            double deltaY = 0;
            for (int j = 0; j < cards.length; j += 1) {
                column[j] = new Rectangle2D.Double(x, y + deltaY, this.cardWidth, this.cardHeight);
                if (j == cards.length - 1 && isSelectedCard(Location.TABLEAU, i)) {
                    break;
                } else {
                    drawCard(g2d, column[j], cards[j]);
                }
                deltaY += this.marginHeight;
            }
        }
    }

    private void drawSelectedCards(Graphics2D g2d) {
        if (this.selectedCard != null) {
            Point point = this.getMousePosition();
            double x = point.getX() - this.selectedCard.getOffsetX();
            double y = point.getY() - this.selectedCard.getOffsetY();
            Rectangle2D r = new Rectangle2D.Double(x, y, this.cardWidth, this.cardHeight);

            Card card;
            int i = this.selectedCard.getIndex();
            if (this.selectedCard.getLocation() == Location.WASTE) {
                card = this.board.getWasteCard(i);
            } else {
                int j = this.tableau[i].length - 1;
                card = this.board.getTableauCard(i, j);
            }

            drawCard(g2d, r, card);
        }
    }
}
