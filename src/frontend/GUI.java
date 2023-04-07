package frontend;

import backend.Board;
import backend.Card.Card;
import backend.Card.CardType;
import frontend.UICard.TableauCard;
import frontend.UICard.UICard;
import frontend.UICard.WasteCard;

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
    private Rectangle2D[][] tableau;

    private double cardHeight;
    private double cardWidth;
    private double marginHeight;
    private double marginWidth;

    private UICard selectedCard;
    private double offsetX;
    private double offsetY;

    public GUI(Board board) {
        this.board = board;

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
                    if (waste[i].contains(point) && board.getWasteCard(i) != null) {
                        selectedCard = new WasteCard(board, new int[]{i});
                        offsetX = point.getX() - waste[i].getX();
                        offsetY = point.getY() - waste[i].getY();
                        return;
                    }
                }
                for (int i = tableau.length - 1; i >= 0; i -= 1) {
                    Rectangle2D[] column = tableau[i];
                    for (int j = column.length - 1; j >= 0; j -= 1) {
                        if (column[j].contains(point) && board.isTableauCardMovable(i, j)) {
                            selectedCard = new TableauCard(board, new int[]{i, j});
                            offsetX = point.getX() - column[j].getX();
                            offsetY = point.getY() - column[j].getY();
                            return;
                        }
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedCard != null) {
                    for (int i = 0; i < foundation.length; i += 1) {
                        Point point = e.getPoint();
                        if (foundation[i].contains(point)) {
                            int[] metadata = selectedCard.getMetadata();
                            if (selectedCard.getLocation() == Location.WASTE) {
                                board.moveFromWasteToFoundation(metadata[0], i);
                            } else {
                                board.moveFromTableauToFoundation(metadata[0], metadata[1], i);
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
        this.drawSelectedCards(g2d);
    }

    private boolean isSelected(Location location, int[] metadata) {
        return this.selectedCard != null && this.selectedCard.equals(location, metadata);
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
            if (card != null && !this.isSelected(Location.WASTE, new int[]{i})) {
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
        double x = this.cardWidth + 2 * this.marginWidth;
        double y = this.cardHeight + 2 * this.marginHeight;
        double deltaX = 0;
        for (int i = 0; i < this.tableau.length; i += 1) {
            drawPlaceholder(g2d, new Rectangle2D.Double(x + deltaX, y, this.cardWidth, this.cardHeight));
            Card[] cards = this.board.getTableauCards(i);
            Rectangle2D[] column = new Rectangle2D[cards.length];
            double deltaY = 0;
            for (int j = 0; j < cards.length; j += 1) {
                if (this.isSelected(Location.TABLEAU, new int[]{i, j})) {
                    break;
                }
                column[j] = new Rectangle2D.Double(x + deltaX, y + deltaY, this.cardWidth, this.cardHeight);
                deltaY += this.marginHeight;
                drawCard(g2d, column[j], cards[j]);
            }
            this.tableau[i] = column;
            deltaX += this.cardWidth + this.marginWidth;
        }
    }

    private void drawSelectedCards(Graphics2D g2d) {
        if (this.selectedCard != null) {
            Point point = this.getMousePosition();
            double x = point.getX() - this.offsetX;
            double y = point.getY() - this.offsetY;
            this.selectedCard.draw(g2d, x, y, this.cardHeight, this.cardWidth, this.marginHeight, this.marginWidth);
        }
    }
}
