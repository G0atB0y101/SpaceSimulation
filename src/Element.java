import java.awt.*;
import java.lang.Math;

public class Element {
    private int x, y, dx, dy, radius;

    // Constructor that initializes the x and y positions and the velocity
    public Element(int x, int y, int dx, int dy, int radius) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.radius = radius;
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
    public int getRadius() {
        return radius;
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
    public void setRadius(int radius) {
        this.radius = radius;
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
        System.out.println(this.x + "  "
                        + this.y + "  "
                        + other.getX() + "  "
                        + other.getY() );
        double distance = Math.sqrt(Math.pow(other.getX() - this.x, 2) + Math.pow(other.getY() - this.y, 2));
        if (distance <= (this.radius + other.getRadius())) {
            System.out.println(distance);
            return true;
        } else {
            return false;
        }
    }

}   
