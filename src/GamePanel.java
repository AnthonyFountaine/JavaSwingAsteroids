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
	ArrayList<Debris> debris;
	int lastCalcedShots, shotsRemaining;
	String gameState;
	int score;
	Font HyperSpaceBold = null;
	Image introImage;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));	
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
		addMouseListener(this);
        player = new Player();
		bullets = new ArrayList<Bullet>();
		asteroids = new ArrayList<Asteroid>();
		debris = new ArrayList<Debris>();
		for (int i = 0; i < 5; i++) {
			asteroids.add(new Asteroid(Utilities.randint(-40, GamePanel.WIDTH + 40), Utilities.randint(-40, GamePanel.HEIGHT + 40), 0, new Vector2D(1, Math.toRadians(Utilities.randint(0, 360)))));
		}

		lastCalcedShots = 0;
		
		score = 0;

		timer = new Timer(20, this);
		timer.start();

        keys = new boolean[1000];
		gameState = "intro";

		HyperSpaceBold = new Font("HyperSpace", Font.BOLD, 30);

		introImage = Utilities.loadImage("assets/intro.png");
	}

	public void paint(Graphics g){
		if (gameState == "intro") {
			g.drawImage(introImage, 0, 0, null);
		}
		if (gameState == "game") {
			g.setColor(Color.BLACK);
			g.fillRect(0,0,getWidth(),getHeight());
			player.draw(g);
			for (Bullet b : bullets) {
				b.draw(g);
			}
	
			for (Asteroid a : asteroids) {
				a.draw(g);
			}
	
			for(Debris d : debris) {
				d.draw(g);
			}

			g.setFont(HyperSpaceBold);
			g.setColor(Color.WHITE);
			g.drawString(""+score, 50, 50);
			for (int i = 0; i < player.getLives(); i++) {
				continue;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e){
		if (gameState == "intro") {
			if (keys[KeyEvent.VK_ENTER]) {
				gameState = "game";
			}
		}
		if (gameState == "game") {
			if (checkGameOver()) {
				return;
			}
			player.update(keys, asteroids, debris);
			isShooting();
			updateBullets();
			updateAsteroids();
			updateDebris();
			repaint();
		}
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
	public void mouseClicked(MouseEvent e) {
		if (gameState == "intro") {
			gameState = "game";
		}
	}

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
				player.shootCooldown = 15;
			}
		}

		if (shotsRemaining > 0) {
			bullets.add(new Bullet(player.getX(), player.getY(), player.getdir()));
			shotsRemaining--;
		}
	}

	private void updateBullets() {
		for (int i = 0; i < bullets.size(); i++) {
			int status = bullets.get(i).update(asteroids, debris);
			if (status > 0) {
				bullets.remove(i);
				score += status;
			}
			if (status == Bullet.DEAD) {
				bullets.remove(i);
			}
		}
	}

	private void updateAsteroids() {
		for (int i = 0; i < asteroids.size(); i++) {
			asteroids.get(i).update();
		}
	}

	private void updateDebris() {
		for (int i = 0; i < debris.size(); i++) {
			int state = debris.get(i).update();
			if (state == Debris.DEAD) {
				debris.remove(i);
			}
		}
	}
	
	private boolean checkGameOver() {
		if (player.getLives() == 0) {
			gameState = "gameover";
			return true;
		}
		return false;
	}
}
