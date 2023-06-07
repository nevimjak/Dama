package cz.nevimjak.client.model;

/**
 * Class to save position coordinates
 */
public class Point {
    private int x;
    private int y;

    /**
     * Constructor to create new instance of position
     * @param x x position coordinate
     * @param y y position coordinate
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Getter for x coordinate
     * @return
     */
    public int getX() {
        return x;
    }

    /**
     * Getter for y coordinate
     * @return
     */
    public int getY() {
        return y;
    }
}