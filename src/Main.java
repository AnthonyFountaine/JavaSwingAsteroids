import javax.swing.*;

/*
 * Main.java
 * Anthony Fountaine
 * This class handles creating the main JFrame (window) for the game
 */

public class Main extends JFrame{
	GamePanel game = new GamePanel();
		
    public Main() {
		super("Asteroids");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		add(game);
		pack();  // set the size of my Frame exactly big enough to hold the contents
		setVisible(true);
    }    
    public static void main(String[] arguments) {
		new Main();		
    }
}
