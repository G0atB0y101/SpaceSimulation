import java.awt.*;


public class Element {
    private int x, y, dx, dy;

    // Constructor that initializes the x and y positions and the velocity
    public Element(int x, int y, int dx, int dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

//#region //Getters for private variables 
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getdX() {
        return dx;
    }
    public int getdY() {
        return dy;
    }
//#endregion

//#region //Setters for private variables 
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setdX(int dx) {
        this.dx = dx;
    }
    public void setdY(int dy) {
        this.dy = dy;
    }
//#endregion


    public boolean isOffScreen(Dimension size) {
        return x < 0 || x > size.width || y < 0 || y > size.height;
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public boolean collidesWith(Element other) {
        return this.x == other.x && this.y == other.y;
    }

}   
