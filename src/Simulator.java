import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Simulator extends JPanel implements ActionListener, KeyListener{
    private final int FWidth = 1000;
    private final int FHeight = 1000;

    private int[] cellSizes = {100, 300, 500};
    private Map<Integer, List<Element>> spatialHashMapLOD1 = new HashMap<>();
    private Map<Integer, List<Element>> spatialHashMapLOD2 = new HashMap<>();
    private Map<Integer, List<Element>> spatialHashMapLOD3 = new HashMap<>();

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

        for (int cellSize : cellSizes) {
            for (int i = 0; i < 10; i++) {
                int x = new Random().nextInt(FWidth);
                int y = new Random().nextInt(FHeight);
                int dx = new Random().nextInt(highSpeedBound - lowSpeedBound + 1)
                        * (new Random().nextInt(2) * 2 - 1) + lowSpeedBound;
                int dy = new Random().nextInt(highSpeedBound - lowSpeedBound + 1)
                        * (new Random().nextInt(2) * 2 - 1) + lowSpeedBound;
                Element element = new Element(x, y, dx, dy, elementSize, 1, 1);
                if (cellSize == cellSizes[0]) {
                    spatialHashMapLOD1.computeIfAbsent(getCellIndex(element, cellSize), k -> new ArrayList<>()).add(element);
                } else if (cellSize == cellSizes[1]) {
                    spatialHashMapLOD2.computeIfAbsent(getCellIndex(element, cellSize), k -> new ArrayList<>()).add(element);
                } else if (cellSize == cellSizes[2]) {
                    spatialHashMapLOD3.computeIfAbsent(getCellIndex(element, cellSize), k -> new ArrayList<>()).add(element);
                }
            }
        }


    simLoop = new Timer((int) (1000/FPS), this);
    simLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        for (Map<Integer, List<Element>> spatialHashMap : Arrays.asList(spatialHashMapLOD1, spatialHashMapLOD2, spatialHashMapLOD3)) {
            for (List<Element> elements : spatialHashMap.values()) {
                for (Element element : elements) {
                    g.fillOval(element.getX(), element.getY(), element.getRadius(), element.getRadius());
                }
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        List<Element> elementsToRemove = new ArrayList<>();
        List<Element> elementsToAdd = new ArrayList<>();

        for (Map<Integer, List<Element>> spatialHashMap : Arrays.asList(spatialHashMapLOD1, spatialHashMapLOD2, spatialHashMapLOD3)) {
            for (List<Element> elements : spatialHashMap.values()) {
                for (Element element : elements) {
                    int cellSize = determineCellSize(element.getRadius());
                    Integer cellIndex = getCellIndex(element, cellSize);
                    List<Integer> neighborCells = getNeighborCells(cellIndex, cellSize);
                    neighborCells.add(cellIndex);

                    if (handleCollisions(element, neighborCells, spatialHashMap, elementsToRemove, elementsToAdd)) {
                        elementsToRemove.add(element);
                    } else {
                        element.move();
                        Integer newCellIndex = getCellIndex(element, cellSize);
                        if (!newCellIndex.equals(cellIndex)) {
                            elementsToRemove.add(element);
                            elementsToAdd.add(element);
                        }
                    }
                }
            }
        }

        removeElements(elementsToRemove);
        addElements(elementsToAdd);

        repaint();
    }

    private int determineCellSize(int radius) {
        if (radius > 100) return cellSizes[2];
        if (radius > 50) return cellSizes[1];
        return cellSizes[0];
    }

    private Integer getCellIndex(Element element, int cellSize) {
        return (element.getX() / cellSize) * (FWidth / cellSize) + (element.getY() / cellSize);
    }

    private boolean handleCollisions(Element element, List<Integer> neighborCells, Map<Integer, List<Element>> spatialHashMap, 
                                    List<Element> elementsToRemove, List<Element> elementsToAdd) {
        for (int neighborCell : neighborCells) {
            if (spatialHashMap.containsKey(neighborCell)) {
                for (Element neighborElement : spatialHashMap.get(neighborCell)) {
                    if (element.collidesWith(neighborElement) && element != neighborElement) {
                        elementsToRemove.add(element);
                        elementsToRemove.add(neighborElement);
                        elementsToAdd.add(combineElements(element, neighborElement));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void removeElements(List<Element> elementsToRemove) {
        for (Element element : elementsToRemove) {
            int cellSize = determineCellSize(element.getRadius());
            Integer cellIndex = getCellIndex(element, cellSize);
            if (cellSize == cellSizes[0]) {
                List<Element> elements = spatialHashMapLOD1.get(cellIndex);
                if (elements!= null) {
                    elements.remove(element);
                    if (elements.isEmpty()) {
                        spatialHashMapLOD1.remove(cellIndex);
                    }
                }
            } else if (cellSize == cellSizes[1]) {
                List<Element> elements = spatialHashMapLOD2.get(cellIndex);
                if (elements!= null) {
                    elements.remove(element);
                    if (elements.isEmpty()) {
                        spatialHashMapLOD2.remove(cellIndex);
                    }
                }
            } else if (cellSize == cellSizes[2]) {
                List<Element> elements = spatialHashMapLOD3.get(cellIndex);
                if (elements!= null) {
                    elements.remove(element);
                    if (elements.isEmpty()) {
                        spatialHashMapLOD3.remove(cellIndex);
                    }
                }
            }
        }
    }
    
    private void addElements(List<Element> elementsToAdd) {
        for (Element element : elementsToAdd) {
            int cellSize = determineCellSize(element.getRadius());
            Integer cellIndex = getCellIndex(element, cellSize);
            if (cellSize == cellSizes[0]) {
                spatialHashMapLOD1.computeIfAbsent(cellIndex, k -> new ArrayList<>()).add(element);
            } else if (cellSize == cellSizes[1]) {
                spatialHashMapLOD2.computeIfAbsent(cellIndex, k -> new ArrayList<>()).add(element);
            } else if (cellSize == cellSizes[2]) {
                spatialHashMapLOD3.computeIfAbsent(cellIndex, k -> new ArrayList<>()).add(element);
            }
        }
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
    public List<Integer> getNeighborCells(int cellIndex, int cellSize) {
        List<Integer> neighborCells = new ArrayList<>();
        int numCells = (FWidth / cellSize) * (FHeight / cellSize);
        int x = cellIndex % (FWidth / cellSize);
        int y = cellIndex / (FWidth / cellSize);
        neighborCells.add(cellIndex - 1);
        neighborCells.add(cellIndex + 1);
        neighborCells.add(cellIndex - (FWidth / cellSize) - 1);
        neighborCells.add(cellIndex - (FWidth / cellSize));
        neighborCells.add(cellIndex - (FWidth / cellSize) + 1);
        neighborCells.add(cellIndex + (FWidth / cellSize) - 1);
        neighborCells.add(cellIndex + (FWidth / cellSize));
        neighborCells.add(cellIndex + (FWidth / cellSize) + 1);
        neighborCells.removeIf(
            a -> a < 0 || a >= numCells || ((a % (FWidth / cellSize) == 0 || (FWidth / cellSize) == (FHeight / cellSize) - 1) && x!= 0 && x!= (FWidth / cellSize) - 1)
            || ((a / (FWidth / cellSize) == 0 || a / (FWidth / cellSize) == (FHeight / cellSize) - 1) && y!= 0 && y!= (FHeight / cellSize) - 1)
        );
        return neighborCells;
    }

    private static Element combineElements(Element self, Element other) {
        self.setX((self.getX() + other.getX()) / 2);
        self.setY((self.getY() + other.getY()) / 2);
        self.setdX(self.getdX() - other.getdX());
        self.setdY(self.getdY() - other.getdY());
        self.setRadius(self.getRadius() + other.getRadius());
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