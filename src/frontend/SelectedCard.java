package frontend;

public class SelectedCard {
    private Location location;
    private int index;
    private double offsetX;
    private double offsetY;

    public SelectedCard(Location location, int index, double offsetX, double offsetY) {
        this.location = location;
        this.index = index;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public Location getLocation() {
        return this.location;
    }

    public int getIndex() {
        return this.index;
    }

    public double getOffsetX() {
        return this.offsetX;
    }

    public double getOffsetY() {
        return this.offsetY;
    }
}
