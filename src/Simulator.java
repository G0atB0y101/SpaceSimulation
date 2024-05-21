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
    
    private int speedBound = 2;
    private int FPS = 60;

    Timer simLoop;
    Map<Integer, List<Element>> spatialHashMap = new HashMap<>();

    Simulator() {
        setPreferredSize(new Dimension(FWidth, FHeight));
        setFocusable(true);
        addKeyListener(this);

        for(int i = 0; i < 25; i++) {
            int x = new Random().nextInt(FWidth);
            int y = new Random().nextInt(FHeight);
            int dx = new Random().nextInt(speedBound);
            int dy = new Random().nextInt(speedBound);
            Element element = new Element(x, y, dx, dy, 10);
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
            for (Element element : elements) {
                int cellIndex = (element.getX() / cellSize) * numCellsX + (element.getY() / cellSize);
                List<Integer> neighborCells = getNeighborCells(cellIndex);
                neighborCells.add(cellIndex);
                for (int neighborCell : neighborCells) {
                    if (spatialHashMap.containsKey(neighborCell)) {
                        for (Element neighborElement : spatialHashMap.get(neighborCell)) {
                            if (element.collidesWith(neighborElement) && element != neighborElement && !element.equals(neighborElement)) {
                                element.setdX(element.getdX() * -1);
                                element.setdY(element.getdY() * -1);
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