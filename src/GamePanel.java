import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener{
    public final static int WIDTH = 800, HEIGHT = 600;
    Timer timer;
    boolean[] keys;
    Player player;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));	
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
		addMouseListener(this);
        player = new Player();
		
		timer = new Timer(20, this);
		timer.start();

        keys = new boolean[1000];
	}

	public void paint(Graphics g){
		g.setColor(Color.BLACK);
		g.fillRect(0,0,getWidth(),getHeight());
        player.draw(g);
	}

	@Override
	public void actionPerformed(ActionEvent e){
        player.update(keys);
		repaint();
	}
	
	@Override
	public void	keyPressed(KeyEvent e){
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void	keyReleased(KeyEvent e){
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void	keyTyped(KeyEvent e){}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}
