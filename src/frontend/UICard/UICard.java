package frontend.UICard;

import backend.Board;
import backend.Card.Card;
import frontend.Location;

import java.awt.*;
import java.util.Arrays;

public abstract class UICard {
    private Board board;
    private Location location;
    private int[] metadata;

    public UICard(Board board, Location location, int[] metadata) {
        this.board = board;
        this.location = location;
        this.metadata = metadata;
    }

    public Board getBoard() {
        return board;
    }

    public Location getLocation() {
        return this.location;
    }

    public int[] getMetadata() {
        return this.metadata;
    }

    public boolean equals(Location location, int[] metadata) {
        return this.location == location && Arrays.equals(this.metadata, metadata);
    }

    abstract public void draw(Graphics2D g2d, double x, double y, double cardHeight, double cardWidth, double marginHeight, double marginWidth);
}
