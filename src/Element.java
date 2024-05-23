import java.awt.*;
import java.lang.Math;

public class Element {
    private int x, y, dx, dy, radius, mass, density;

    // Constructor that initializes the x and y positions and the velocity
    public Element(int x, int y, int dx, int dy, int radius, int mass, int density) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.radius = radius;
        this.mass = mass;
        this.density = density;
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
    public int getMass() {
        return mass;
    }
    public int getDensity() {
        return density;
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
    public void setMass(int mass) {
        this.mass = mass;
    }
    public void setDensity(int density) {
        this.density = density;
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
        int difx = this.getX() - other.getX();
        int dify = this.getY() - other.getY();
        double distance = Math.sqrt(difx * difx + dify * dify);        
        if (distance <= (this.radius + other.getRadius())) {
            return true;
        } else {
            return false;
        }
    }

}   
