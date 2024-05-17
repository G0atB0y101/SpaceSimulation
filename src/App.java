import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int simWidth = 1500;
        int simHeight = 800;

        JFrame frame = new JFrame("Simulator");
		frame.setSize(simWidth, simHeight);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Simulator spaceSim = new Simulator();
        frame.add(spaceSim);
        frame.pack();
        spaceSim.requestFocus();
        frame.setVisible(true);

    }
}
