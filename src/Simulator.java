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

public class Simulator extends JPanel implements ActionListener{
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

        for (int cellSize : cellSizes) {
            for (int i = 0; i < 10; i++) {
                int x = new Random().nextInt(FWidth);
                int y = new Random().nextInt(FHeight);
                int dx = new Random().nextInt(highSpeedBound - lowSpeedBound + 1)
                        * (new Random().nextInt(2) * 2 - 1) + lowSpeedBound;
                int dy = new Random().nextInt(highSpeedBound - lowSpeedBound + 1)
                        * (new Random().nextInt(2) * 2 - 1) + lowSpeedBound;
                Element element = new Element(x, y, dx, dy, elementSize, 1, 1);
    
                spatialHashMapLOD1.computeIfAbsent(getCellIndex(element, cellSizes[0]), k -> new ArrayList<>()).add(element);
                spatialHashMapLOD2.computeIfAbsent(getCellIndex(element, cellSizes[1]), k -> new ArrayList<>()).add(element);
                spatialHashMapLOD3.computeIfAbsent(getCellIndex(element, cellSizes[2]), k -> new ArrayList<>()).add(element);
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
    
        for (List<Element> elements : spatialHashMapLOD1.values()) {
            for (Element element : elements) {
                int cellSize = determineCellSize(element.getRadius());
                Integer cellIndex = getCellIndex(element, cellSize);
    
                if (handleCollisions(element, elementsToRemove, elementsToAdd)) {
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

    private boolean handleCollisions(Element element, List<Element> elementsToRemove, List<Element> elementsToAdd) {
        Map<Integer, List<Element>> spatialHashMap;
        int cellSize;
    
        if (element.getRadius() < 50) {
            spatialHashMap = spatialHashMapLOD1;
            cellSize = cellSizes[0];
        } else if (element.getRadius() < 150) {
            spatialHashMap = spatialHashMapLOD2;
            cellSize = cellSizes[1];
        } else {
            spatialHashMap = spatialHashMapLOD3;
            cellSize = cellSizes[2];
        }
    
        Integer cellIndex = getCellIndex(element, cellSize);
        List<Integer> neighborCells = getNeighborCells(cellIndex, cellSize);
    
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
            for (int cellSize : cellSizes) {
                Integer cellIndex = getCellIndex(element, cellSize);
                if (spatialHashMapLOD1.containsKey(cellIndex)) {
                    List<Element> elements = spatialHashMapLOD1.get(cellIndex);
                    if (elements != null) {
                        elements.remove(element);
                        if (elements.isEmpty()) {
                            spatialHashMapLOD1.remove(cellIndex);
                        }
                    }
                }
                if (spatialHashMapLOD2.containsKey(cellIndex)) {
                    List<Element> elements = spatialHashMapLOD2.get(cellIndex);
                    if (elements != null) {
                        elements.remove(element);
                        if (elements.isEmpty()) {
                            spatialHashMapLOD2.remove(cellIndex);
                        }
                    }
                }
                if (spatialHashMapLOD3.containsKey(cellIndex)) {
                    List<Element> elements = spatialHashMapLOD3.get(cellIndex);
                    if (elements != null) {
                        elements.remove(element);
                        if (elements.isEmpty()) {
                            spatialHashMapLOD3.remove(cellIndex);
                        }
                    }
                }
            }
        }
    }
    
    private void addElements(List<Element> elementsToAdd) {
        for (Element element : elementsToAdd) {
            Integer cellIndex1 = getCellIndex(element, cellSizes[0]);
            Integer cellIndex2 = getCellIndex(element, cellSizes[1]);
            Integer cellIndex3 = getCellIndex(element, cellSizes[2]);
    
            spatialHashMapLOD1.computeIfAbsent(cellIndex1, k -> new ArrayList<>()).add(element);
            spatialHashMapLOD2.computeIfAbsent(cellIndex2, k -> new ArrayList<>()).add(element);
            spatialHashMapLOD3.computeIfAbsent(cellIndex3, k -> new ArrayList<>()).add(element);
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
        neighborCells.add(cellIndex);
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

}