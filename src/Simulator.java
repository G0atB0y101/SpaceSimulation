import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    private double FPS = 1;
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

        simLoop = new Timer((int) (1000/FPS), this);
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
    public void actionPerformed(ActionEvent e) { 
        List<Element> elementsToRemove = new ArrayList<>();
        List<Element> elementsToAdd = new ArrayList<>();
        
        for (List<Element> elements : spatialHashMap.values()) {
            Iterator<Element> iterator = elements.iterator();
            while (iterator.hasNext()) {
                Element element = iterator.next();
                Integer cellIndex = (element.getX() / cellSize) * numCellsX + (element.getY() / cellSize);
                List<Integer> neighborCells = getNeighborCells(cellIndex);
                neighborCells.add(cellIndex);
                
                boolean elementCollided = false;
                for (int neighborCell : neighborCells) {
                    if (spatialHashMap.containsKey(neighborCell)) {
                        for (Element neighborElement : spatialHashMap.get(neighborCell)) {
                            // Collision
                            if (element.collidesWith(neighborElement) && element != neighborElement) {
                                elementsToRemove.add(element);
                                elementsToRemove.add(neighborElement);
                                elementsToAdd.add(combineElements(element, neighborElement));
                                elementCollided = true;
                                break;
                            }
                        }
                    }
                    if (elementCollided) break;
                }
                
                if (!elementCollided) {
                    element.move(); 
                    Integer newCellIndex = (element.getX() / cellSize) * numCellsX + (element.getY() / cellSize);
                    if (!newCellIndex.equals(cellIndex)) {
                        iterator.remove();
                        spatialHashMap.computeIfAbsent(newCellIndex, k -> new ArrayList<>()).add(element);
                    }
                }
            }
        }
        
        for (Element element : elementsToRemove) {
            Integer cellIndex = (element.getX() / cellSize) * numCellsX + (element.getY() / cellSize);
            spatialHashMap.get(cellIndex).remove(element);
        }
        for (Element element : elementsToAdd) {
            Integer cellIndex = (element.getX() / cellSize) * numCellsX + (element.getY() / cellSize);
            spatialHashMap.computeIfAbsent(cellIndex, k -> new ArrayList<>()).add(element);
        }
        
        repaint();
    }    
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

    private static Element combineElements(Element self, Element other) {
        self.setX((self.getX() + other.getX()) / 2);
        self.setY((self.getY() + other.getY()) / 2);
        self.setdX(self.getdX() - other.getdX());
        self.setdY(self.getdY() - other.getdY());
        self.setRadius((int) Math.sqrt(self.getRadius() * self.getRadius() + other.getRadius() * other.getRadius()));
        self.setMass(self.getMass() + other.getMass());
        return self;
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