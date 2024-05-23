import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.*;

public class Simulator extends JPanel implements ActionListener, KeyListener{
    final int FWidth = 1000;
    final int FHeight = 1000;
    final int cellSize = 100;
    final int numCellsX = FWidth / cellSize;
    final int numCellsY = FHeight / cellSize;
    private final double GRAVITATIONAL_CONSTANT = 0.01;
    private final int GRAVITY_RANGE = 500;
    
    private int lowSpeedBound = -2;
    private int highSpeedBound = 2;
    private int FPS = 120;
    private int elementSize = 10;

    Timer simLoop;
    Map<Integer, List<Element>> spatialHashMap = new HashMap<>();

    Simulator() {
        setPreferredSize(new Dimension(FWidth, FHeight));
        setFocusable(true);
        addKeyListener(this);

        for(int i = 0; i < 500; i++) {
            int x = new Random().nextInt(FWidth);
            int y = new Random().nextInt(FHeight);
            int dx = new Random().nextInt(highSpeedBound - lowSpeedBound + 1) 
                    * (new Random().nextInt(2) * 2 - 1) + lowSpeedBound;
            int dy = new Random().nextInt(highSpeedBound - lowSpeedBound + 1) 
                    * (new Random().nextInt(2) * 2 - 1) + lowSpeedBound;
            Element element = new Element(x, y, dx, dy, elementSize, 1, 1);
            int cellIndex = (x / cellSize) * numCellsX + (y / cellSize);
            if (!spatialHashMap.containsKey(cellIndex)) {
                spatialHashMap.put(cellIndex, new ArrayList<>());
            }
            spatialHashMap.get(cellIndex).add(element);
        }

        simLoop = new Timer(1000/FPS, this);
        simLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        for (List<Element> elements : spatialHashMap.values()) {
            for (Element element : elements) {
                g.fillOval(element.getX(), element.getY(), element.getRadius(), element.getRadius());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) { //called every 1000 milliseconds by gameLoop timer
        for (List<Element> elements : spatialHashMap.values()) {
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                int cellIndex = (element.getX() / cellSize) * numCellsX + (element.getY() / cellSize);
                List<Integer> neighborCells = getNeighborCells(cellIndex);
                neighborCells.add(cellIndex);
                for (int neighborCell : neighborCells) {
                    if (spatialHashMap.containsKey(neighborCell)) {
                        for (Element neighborElement : spatialHashMap.get(neighborCell)) {
                            // Collision 
                            if (element.collidesWith(neighborElement) && element != neighborElement) {
                                    elements.set(i, combineElements(element, neighborElement));
                                    spatialHashMap.get(neighborCell).remove(neighborElement);
                                    break;
                            // Gravity
                            /*    int difx = element.getX() - neighborElement.getX();
                                int dify = element.getY() - neighborElement.getY();
                                double distance = Math.sqrt(difx * difx + dify * dify);
                                 if (distance < GRAVITY_RANGE) {
                                    double force = GRAVITATIONAL_CONSTANT * element.getMass() * neighborElement.getMass() / (distance * distance);
                                    double angle = Math.atan2(dify, difx);
                                    int ddx = (int) (force * Math.cos(angle));
                                    int ddy = (int) (force * Math.sin(angle));
                                    element.setdX(element.getdX() + ddx);
                                    element.setdY(element.getdY() + ddy);
                                } */
                            }
                        }
                    }
                }
                element.move();
            }
        }
        repaint();
    }

    public List<Integer> getNeighborCells(int cellIndex) {
        List<Integer> neighborCells = new ArrayList<>();
        int numCells = numCellsX * numCellsY;
        int x = cellIndex % numCellsX;
        int y = cellIndex / numCellsX;
        neighborCells.add(cellIndex - 1);
        neighborCells.add(cellIndex + 1);
        neighborCells.add(cellIndex - numCellsX - 1);
        neighborCells.add(cellIndex - numCellsX);
        neighborCells.add(cellIndex - numCellsX + 1);
        neighborCells.add(cellIndex + numCellsX - 1);
        neighborCells.add(cellIndex + numCells);
        neighborCells.add(cellIndex + numCellsX + 1);
        neighborCells.removeIf(
            a -> a < 0 || a >= numCells || ((a % numCellsX == 0 || numCellsX == numCellsY -1) && x != 0 && x != numCellsX - 1)
            || ((a / numCellsX == 0 || a / numCellsX == numCellsY - 1) && y != 0 && y != numCellsY - 1)
        );
        return neighborCells;

    }

    private Element combineElements(Element e1, Element e2) {
        int x = (e1.getX() + e2.getX()) / 2;
        int y = (e1.getY() + e2.getY()) / 2;
        int dx = e1.getdX() + e2.getdX();
        int dy = e1.getdY() + e2.getdY();
        int radius = (int) Math.sqrt(e1.getRadius() * e1.getRadius() + e2.getRadius() * e2.getRadius());
        int mass = e1.getMass() + e2.getMass();
        return new Element(x, y, dx, dy, radius, mass, 1);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {

        }
    }

    //not needed
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

}