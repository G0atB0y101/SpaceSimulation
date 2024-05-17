import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Simulator extends JPanel implements ActionListener, KeyListener{
    final int FWidth = 1600;
    final int FHeight = 1000;

    Timer simLoop;
    ArrayList<Element> elements = new ArrayList<>();

    Simulator() {
        setPreferredSize(new Dimension(FWidth, FHeight));
        setFocusable(true);
        addKeyListener(this);

        for(int i = 0; i < 50; i++) {
            elements.add(new Element(new Random().nextInt(FWidth)
                                  , new Random().nextInt(FHeight)
                                  , new Random().nextInt(10)
                                  , new Random().nextInt(10)));
        }

        simLoop = new Timer(1000/10, this);
        simLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        for (Element element : elements) {
            g.fillOval(element.getX(), element.getY(), 10, 10);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) { //called every 1000 milliseconds by gameLoop timer
        for (Element element : elements) {
            for (Element iemlement : elements) {
                if (element!= iemlement) {
                    if (element.collidesWith(iemlement)) {
                        element.setdX(element.getdX() * -1);
                        element.setdY(element.getdY() * -1);
                    }
                }
            }
            element.move();
        }
        repaint();
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