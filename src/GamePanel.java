import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener{
    public final static int WIDTH = 800, HEIGHT = 600;
    Timer timer;
    boolean[] keys;
    Player player;
	ArrayList<Bullet> bullets;
	ArrayList<Asteroid> asteroids;
	int lastCalcedShots, shotsRemaining;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));	
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
		addMouseListener(this);
        player = new Player();
		bullets = new ArrayList<Bullet>();
		asteroids = new ArrayList<Asteroid>();
		for (int i = 0; i < 5; i++) {
			asteroids.add(new Asteroid(Utilities.randint(-40, GamePanel.WIDTH + 40), Utilities.randint(-40, GamePanel.HEIGHT + 40), 0, new Vector2D(1, Math.toRadians(Utilities.randint(0, 360)))));
		}

		lastCalcedShots = 0;
		
		timer = new Timer(20, this);
		timer.start();

        keys = new boolean[1000];
	}

	public void paint(Graphics g){
		g.setColor(Color.BLACK);
		g.fillRect(0,0,getWidth(),getHeight());
        player.draw(g);
		for (Bullet b : bullets) {
			b.draw(g);
		}

		for (Asteroid a : asteroids) {
			a.draw(g);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e){
        player.update(keys);
		isShooting();
		updateBullets();
		updateAsteroids();
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

	private void isShooting() {
		if (keys[KeyEvent.VK_SPACE] && player.canShoot(lastCalcedShots, keys) > 0) {
			lastCalcedShots = player.canShoot(lastCalcedShots, keys);
			shotsRemaining = lastCalcedShots > 0 ? shotsRemaining + lastCalcedShots : shotsRemaining;
			if (lastCalcedShots > 0) {
				player.shootCooldown = 10;
			}
		}

		if (shotsRemaining > 0) {
			bullets.add(new Bullet(player.getX(), player.getY(), player.getdir()));
			shotsRemaining--;
		}
	}

	private void updateBullets() {
		for (int i = 0; i < bullets.size(); i++) {
			if (bullets.get(i).checkDead()) {
				bullets.remove(i);
				continue;
			}
			bullets.get(i).update();
		}
	}

	private void updateAsteroids() {
		for (int i = 0; i < asteroids.size(); i++) {
			asteroids.get(i).move();
		}
	}
}
