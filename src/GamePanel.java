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
	UFO ufo;
	int lastCalcedShots, shotsRemaining, shotSpawnDelay;
	String gameState;
	int score;
	int level;
	int newLevelDelay;
	Font arcadeClassic = null, arcadeClassicBig = null;
	Image introImage;
	
    public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));	
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
		addMouseListener(this);
        
		timer = new Timer(15, this);
		timer.start();
		
        keys = new boolean[1000];
		gameState = "intro";
		
		arcadeClassic = Utilities.loadFont("assets/arcadeClassic.ttf", 30);
		arcadeClassicBig = Utilities.loadFont("assets/arcadeClassic.ttf", 50);
		
		introImage = Utilities.loadImage("assets/intro.png");
	}
	
	public void paint(Graphics g){
		if (gameState.equals("intro")) {
			g.setColor(Color.GREEN);
			g.drawImage(introImage, 0, 0, null);
			g.setFont(arcadeClassicBig);
			g.drawString("CLICK TO START GAME", 125, 575);
		}
		if (gameState.equals("game")) {
			g.setColor(Color.BLACK);
			g.fillRect(0,0,getWidth(),getHeight());
			player.draw(g);
			if (ufo != null) {
				ufo.draw(g);
			}
			for (Bullet b : bullets) {
				b.draw(g);
			}
			
			for (Asteroid a : asteroids) {
				a.draw(g);
			}
			
			for(Debris d : debris) {
				d.draw(g);
			}
			
			g.setFont(arcadeClassic);
			g.setColor(Color.WHITE);
			g.drawString("SCORE   "+score + "     LEVEL   " + level + "     ASTEROIDS   " + asteroids.size(), 50, 50);
			if (newLevelDelay > 0) {
				g.drawString("LEVEL COMPLETE!", 275, 325);
			}
			
			g.translate(50, GamePanel.HEIGHT - 50);
			for (int i = 0; i < player.getLives() - 1; i++) {
				g.drawPolygon(player.playerShape[0], player.playerShape[1], player.playerShape[0].length);
				g.translate(50, 0);
			}
		}
		
		if (gameState.equals("gameover")) {
			g.setColor(Color.BLACK);
			g.fillRect(0,0,getWidth(),getHeight());
			g.setFont(arcadeClassicBig);
			g.setColor(Color.WHITE);
			g.drawLine(WIDTH/2, 0, WIDTH/2, HEIGHT);
			g.drawString("GAMEOVER", 275, 275);
			g.drawString("SCORE: " + score, 290, 325);
			g.drawString("ENTER TO RETURN HOME", 105, 525);
			g.drawString("CLICK TO RESTART GAME", 110, 575);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e){
		if (gameState.equals("intro")) {
		}
		if (gameState.equals("game")) {
			player.update(keys, asteroids, debris);
			if (ufo != null) {
				if (ufo.update(asteroids, debris, bullets, player) == UFO.DEAD) {
					ufo = null;
				}
			}
			isShooting();
			updateBullets();
			updateAsteroids();
			updateDebris();
			checkGameOver();
			checkLevel();
		}
		if (gameState.equals("gameover")) {
			if (keys[KeyEvent.VK_ENTER]) {
				gameState = "intro";
			}
		}
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
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (gameState.equals("intro")) {
			startnewGame();
			gameState = "game";
		}

		if (gameState.equals("gameover")) {
			startnewGame();
			gameState = "game";
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	private void isShooting() {
		shotSpawnDelay--;
		if (keys[KeyEvent.VK_SPACE] && player.canShoot(lastCalcedShots, keys) > 0) {
			lastCalcedShots = player.canShoot(lastCalcedShots, keys);
			shotsRemaining = lastCalcedShots > 0 ? shotsRemaining + lastCalcedShots : shotsRemaining;
			if (lastCalcedShots > 0) {
				player.shootCooldown = 15;
			}
		}

		if (shotsRemaining > 0 && shotSpawnDelay <= 0) {
			bullets.add(new Bullet(player.getX(), player.getY(), player.getdir(), "Player"));
			shotsRemaining--;
			shotSpawnDelay = 3;
		}
	}

	private void updateBullets() {
		for (int i = 0; i < bullets.size(); i++) {
			int status = bullets.get(i).update(asteroids, debris, player, ufo);
			if (status > 0) {
				bullets.remove(i);
				score += status;
				if (status == 1000) {
					ufo = null;
				}
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
	
	private void checkGameOver() {
		if (player.getLives() == 0) {
			gameState = "gameover";
		}	
	}

	private void checkLevel() {
		if (asteroids.size() == 0) {
			newLevelDelay++;
			if (newLevelDelay > 100) {
				newLevel();
				newLevelDelay = 0;
			}
		}
	}

	private void newLevel() {
		level++;
		if (level == 1) {
			player = new Player(WIDTH/2, HEIGHT/2, 3);
		}
		else {
			player = new Player(WIDTH/2, HEIGHT/2, player.getLives() + 1);
		}
		if (level%3 == 0) {
			ufo = new UFO(UFO.randomSpawnLocation("x"), UFO.randomSpawnLocation("y"));
		}
		for (int i = 0; i < 5 + (int)(level/5); i++) {
			asteroids.add(new Asteroid(Asteroid.randomSpawnLocation("x"), Asteroid.randomSpawnLocation("y"), 0, new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), level));
		}
	}

	private void startnewGame() {
		bullets = new ArrayList<Bullet>();
		asteroids = new ArrayList<Asteroid>();
		debris = new ArrayList<Debris>();

		lastCalcedShots = 0;
		shotSpawnDelay = 0;
		
		score = 0;
		level = 0;

		newLevel();
	}
}
