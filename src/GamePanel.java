import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

/*
 * Gamepanel.java
 * Anthony Fountaine
 * This class handles all game logic and rendering
 */

class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener{
    public final static int WIDTH = 800, HEIGHT = 600;
    Timer timer;
    boolean[] keys;
    Player player;
	ArrayList<Bullet> bullets;
	ArrayList<Asteroid> asteroids;
	ArrayList<Debris> debris;
	UFO ufo;
	int lastCalcedShots, shotsRemaining, shotSpawnDelay; //describe in isShooting() method
	String gameState; //screen displayed
	int score;
	int level;
	int newLevelDelay; //handles delay when creating new level
	Font atarian = null, atarianBig = null;
	Image introImage;
	
    public GamePanel() {
		//basic JPanel setup
		setPreferredSize(new Dimension(WIDTH, HEIGHT));	
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
		addMouseListener(this);
        
		timer = new Timer(15, this); //handles FPS
		timer.start();
		
        keys = new boolean[1000];
		gameState = "intro"; //handles screen being displayed
		
		//fonts
		atarian = Utilities.loadFont("assets/Atarian.ttf", 30);
		atarianBig = Utilities.loadFont("assets/Atarian.ttf", 50);
		
		//loading images
		introImage = Utilities.loadImage("assets/intro.png");
	}
	
	public void paint(Graphics g){
		/*
		 * This method draws graphics onto the JPanel
		 */
		if (gameState.equals("intro")) {
			//setting up font & colors
			g.setFont(atarianBig);
			g.setColor(Color.GREEN);

			//drawing
			g.drawImage(introImage, 0, 0, null);
			g.drawString("CLICK TO START GAME", 200, 575);
		}
		if (gameState.equals("game")) {
			//reset screen every frame
			g.setColor(Color.BLACK);
			g.fillRect(0,0,getWidth(),getHeight());
			
			//set up
			g.setFont(atarian);
			g.setColor(Color.WHITE);

			//draw all sprites
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
			
			//draw text
			g.setColor(Color.WHITE);
			g.drawString("SCORE  "+score + "    LEVEL  " + level + "    ASTEROIDS  " + asteroids.size(), 50, 50);
			if (newLevelDelay > 0) {
				g.setColor(Utilities.randomColor());
				g.drawString("LEVEL COMPLETE!", 305, 325);
			}
			
			//drawing lives icons
			g.setColor(Color.WHITE);
			g.translate(50, GamePanel.HEIGHT - 50); //drawing from (50, 550)
			for (int i = 0; i < player.getLives() - 1; i++) {
				g.drawPolygon(player.playerShape[0], player.playerShape[1], player.playerShape[0].length);
				g.translate(50, 0); //move 50 x every iteration
			}
			
		}
		
		if (gameState.equals("gameover")) {
			//reset screen
			g.setColor(Color.BLACK);
			g.fillRect(0,0,getWidth(),getHeight());
			
			//set up
			g.setFont(atarianBig);
			g.setColor(Color.WHITE);

			//draw  text
			g.drawString("GAME OVER", 298, 275);
			g.drawString("SCORE: " + score, 320, 325);
			g.drawString("ENTER TO RETURN HOME", 200, 525);
			g.drawString("CLICK TO RESTART GAME", 195, 575);
		}
		
		if (gameState.equals("pause")) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, WIDTH, HEIGHT);
			
			g.setFont(atarianBig);
			g.setColor(Color.WHITE);
			
			g.drawString("PAUSED", 333, 300);
			g.drawString("CLICK TO RETURN TO GAME", 175, 525);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e){
		/*
		 * This method handles all game logic and is called every frame
		 * by the timer.
		 */
		if (gameState.equals("intro")) {
		}
		if (gameState.equals("game")) {

			//updates all sprites (player, asteroids, etc)
			player.update(keys, asteroids, debris);
			if (ufo != null) {
				//update will return UFO.DEAD if the ufo should be killed on this frame
				if (ufo.update(asteroids, debris, bullets, player) == UFO.DEAD) {
					ufo = null;
				}
			}
			updateBullets();
			updateAsteroids();
			updateDebris();

			isShooting(); // checks if player holding space

			//check game states (new level, no more lives)
			checkGameOver();
			checkLevel();
			checkPause();
		}
		if (gameState.equals("gameover")) {
			if (keys[KeyEvent.VK_ENTER]) {
				gameState = "intro";
			}

		if (gameState.equals("pause")) {
			checkPause();
		}
		}

		//redraw on the screen
		repaint();
	}
	
	@Override
	public void	keyPressed(KeyEvent e){
		/*
		 * This method handles key presses
		 * It is called by the KeyListener
		 */
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void	keyReleased(KeyEvent e){
		/* 
		 * This method handles key releases
		 * It is called by the KeyListener
		*/
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void	keyTyped(KeyEvent e){}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		/* 
		 * This method handles mouse inputs
		 * It is called by the MouseListener
		*/
		if (gameState.equals("intro")) {
			//start new game if player clicks on intro screen
			startnewGame();
			gameState = "game";
		}

		if (gameState.equals("gameover")) {
			//start new game if player clicks on game over screen
			startnewGame();
			gameState = "game";
		}

		if (gameState.equals("pause")) {
			//return to game if player clicks on pause screen
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
		/* 
		 * This method checks if the player is holding space
		 * and if shooting can occur
		*/

		//shotSpawnDelay handles the timing for spawning bullets when 3 bullets should be spawned
		shotSpawnDelay--;

		//checking space & whether the player can shoot
		if (keys[KeyEvent.VK_SPACE] && player.canShoot(lastCalcedShots, keys) > 0) {
			lastCalcedShots = player.canShoot(lastCalcedShots, keys); //for 1 - 3 - 1 - 3 alternating
			shotsRemaining = lastCalcedShots > 0 ? shotsRemaining + lastCalcedShots : shotsRemaining; // number of shots left to be spawned

			//introduce cooldown
			if (lastCalcedShots > 0) {
				player.shootCooldown = 15;
			}
		}

		//checking if a bullet should be spawned
		if (shotsRemaining > 0 && shotSpawnDelay <= 0) {
			bullets.add(new Bullet(player.getX(), player.getY(), player.getdir(), "Player"));

			//handle delay and decreases remaining bullets
			shotsRemaining--;
			shotSpawnDelay = 3;
		}
	}

	private void updateBullets() {
		/*
		 * This method updates all of the bullets that are rendered
		 */

		//looping all bullets
		for (int i = 0; i < bullets.size(); i++) {

			//status
			// > 0 -> increase in score -2 -> kill bullet
			int status = bullets.get(i).update(asteroids, debris, player, ufo);
			if (status > 0) {
				bullets.remove(i);
				score += status; //increase score

				//kill ufo if the ufo was hit
				//ufo returns 1000 score
				if (status == 1000) {
					ufo = null;
				}
			}

			//killing bullet through time
			if (status == Bullet.DEAD) {
				bullets.remove(i);
			}
		}
	}

	private void updateAsteroids() {
		/*
		 * This method updates all of the asteroids that are rendered
		 */
		for (int i = 0; i < asteroids.size(); i++) {
			asteroids.get(i).update();
		}
	}

	private void updateDebris() {
		/*
		 * This method updates all of the asteroids that are rendered
		 */

		//looping all debris
		for (int i = 0; i < debris.size(); i++) {

			//state returns DEAD if it should be killed by time
			int state = debris.get(i).update();
			if (state == Debris.DEAD) {
				debris.remove(i);
			}
		}
	}
	
	private void checkGameOver() {
		/*
		 * This method checks if the game should be ended (the player has run out of lives)
		 */
		if (player.getLives() == 0) {
			//change game state
			gameState = "gameover";
		}	
	}

	private void checkLevel() {
		/*
		 * This method if the level should increase (all enemies have been killed)
		 */
		if (asteroids.size() == 0 && ufo == null) {
			//newLevelDelay handles the time where "Level Complete!" is displayed
			newLevelDelay++;

			//after 100 frames go to new level
			if (newLevelDelay > 100) {
				newLevel();
				newLevelDelay = 0;
			}
		}
	}

	private void checkPause() {
		if (keys[KeyEvent.VK_ESCAPE]) {
			gameState = "pause";
		}
	}

	private void newLevel() {
		/*
		 * This method handles creating a new level
		 */
		//increase level
		level++;


		if (level == 1) {
			//if level is 1 player.getLives() will not work because tehre is no current player
			player = new Player(WIDTH/2, HEIGHT/2, 3);
		}
		else {
			//create new player in the cetnre with +1 lives
			player = new Player(WIDTH/2, HEIGHT/2, player.getLives() + 1);
		}
		if (level%3 == 0) {
			//ufo every 3 levels
			ufo = new UFO(UFO.randomSpawnLocation(WIDTH), UFO.randomSpawnLocation(HEIGHT));
		}
		for (int i = 0; i < 5 + (int)(level/3); i++) {
			//create new asteroids (5 base, +1 every 3 levels)
			asteroids.add(new Asteroid(Asteroid.randomSpawnLocation(WIDTH), Asteroid.randomSpawnLocation(HEIGHT), 0, Utilities.randomdir(), level));
		}
	}

	private void startnewGame() {
		/*
		 * This method handles starting a new game
		 */

		//setup ArrayLists
		bullets = new ArrayList<Bullet>();
		asteroids = new ArrayList<Asteroid>();
		debris = new ArrayList<Debris>();

		//reset shot integers (delay, bullets to be spawned)
		lastCalcedShots = 0;
		shotSpawnDelay = 0;
		
		//reset score and level
		//level will increase to 1 when newLevel() is called -> set it to 0
		score = 0;
		level = 0;

		newLevel();
	}
}
