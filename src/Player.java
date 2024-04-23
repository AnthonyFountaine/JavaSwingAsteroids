import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.util.ArrayList;

/*
 * Player.java
 * Anthony Fountaine
 * This class handles the player object with movement, collision detection, shooting, and drawing
 */

public class Player {
    private double x, y;
    private double xVelo, yVelo;
    private Vector2D dir;
    private final double FRICTION_CONSTANT = 0.95, ROTATIONAL_CONSTANT = 4.5; //friction and rotational constants for movement
    private int[] polygonX, polygonY;
    private boolean thrust = false;
    private int invulnerableTime, deadPauseTime;
    public int shootCooldown;
    private int lives;
    public final int[][] playerShape = new int[][] {new int[] {0, (int)(30 * -Math.cos(-Math.PI/2 -.3)), (int)(30 * -Math.cos(-Math.PI/2 - .3)) * 3/4, (int)(30 * -Math.cos(-Math.PI/2 + .3)) * 3/4, (int)(30 * -Math.cos(-Math.PI/2 + .3)), 0}, 
                                                           new int[] {0, (int)(30 * -Math.sin(-Math.PI/2 - .3)), (int)(30 * -Math.sin(-Math.PI/2 - .3)) * 3/4, (int)(30 * -Math.sin(-Math.PI/2 +.3)) * 3/4, (int)(30 * -Math.sin(-Math.PI/2 +.3)), 0}};
    
    public Player(int x, int y, int lives) {
        //initialize positions
        this.x = x;
        this.y = y;

        //initialize direction and velocity
        this.dir = new Vector2D(1, Math.toRadians(90));
        this.xVelo = 0;
        this.yVelo = 0;

        //initialize player properties
        this.shootCooldown = 0;
        this.invulnerableTime = 40;
        this.polygonX = new int[6]; //initialize polygon
        this.polygonY = new int[6]; //initialize polygon
        this.lives = lives;
    }

    public void move(boolean[] keys) {
        /*
         * This method moves the player in the direction of its velocity
         */

        //update velocity based on direction
        veloUpdate(keys);

        //update position based on velocity
        x+= xVelo;
        y+= yVelo;

        //wrap around screen
        if (x < -40) {
            x = GamePanel.WIDTH + 40;
        }
        else if (x > GamePanel.WIDTH + 40) {
            x = -40;
        }
        if (y < -40) {
            y = GamePanel.HEIGHT + 40;
        }
        else if (y > GamePanel.HEIGHT + 40) {
            y = -40;
        }
    }

    private void veloUpdate(boolean[] keys) {
        /*
         * This method updates the velocity of the player based on the keys pressed
         */

        dir.normalize();
        
        //update velocity based on w key
        if (keys[KeyEvent.VK_W]) { //if w key is pressed
            thrust = !thrust; //for drawing thrust trail

            //update velocity based on direction
            //add arbitrary portion of direction vector to velocity to make it feel smooth
            xVelo += dir.getxComp()/4;
            yVelo += dir.getyComp()/4;
        }
        else {
            //if w key is not pressed, stop thrusting
            thrust = false;
        }

        //handle friction and accelerated movement feel
        xVelo*=FRICTION_CONSTANT;
        yVelo*=FRICTION_CONSTANT;
    }

    public void draw(Graphics g) {
        /*
         * This method draws the player onto the screen
         */
        g.setColor(Color.WHITE);
        if ((invulnerableTime / 4) % 2 == 0) { //flash player if invulnerable
            //draw player
            g.drawPolygon(polygonX, polygonY, polygonX.length);
            if (thrust) {
                //draw thrust trail if thrusting, it also flashes
                g.drawLine((int)(x + (30 * -Math.cos(dir.getAngle() - .2)) * 3/4), (int)(y + (30 * -Math.sin(dir.getAngle() - .2)) * 3/4), (int)(x + (32 * -Math.cos(dir.getAngle()))), (int)(y + (32 * -Math.sin(dir.getAngle()))));
                g.drawLine((int)(x + (30 * -Math.cos(dir.getAngle() + .2)) * 3/4), (int)(y + (30 * -Math.sin(dir.getAngle() + .2)) * 3/4), (int)(x + (32 * -Math.cos(dir.getAngle()))), (int)(y + (32 * -Math.sin(dir.getAngle()))));
            }
        }
    }

    public void updatePolygon() {
        /*
         * This method updates the polygon of the player based on its position and direction, very inefficiently unfortunately :(
         */
        polygonX[0] = (int)x;
        polygonY[0] = (int)y;
        polygonX[1] = (int)(x + 30 * -Math.cos(dir.getAngle() - .3));
        polygonY[1] = (int)(y + 30 * -Math.sin(dir.getAngle() - .3));
        polygonX[2] = (int)(x + (30 * -Math.cos(dir.getAngle() - .3)) * 3/4);
        polygonY[2] = (int)(y + (30 * -Math.sin(dir.getAngle() - .3)) * 3/4);
        polygonX[3] = (int)(x + (30 * -Math.cos(dir.getAngle() + .3)) * 3/4);
        polygonY[3] = (int)(y + (30 * -Math.sin(dir.getAngle() + .3)) * 3/4);
        polygonX[4] = (int)(x + 30 * -Math.cos(dir.getAngle() + .3));
        polygonY[4] = (int)(y + 30 * -Math.sin(dir.getAngle() + .3));
        polygonX[5] = (int)x;
        polygonY[5] = (int)y;
    }

    public void rotate(boolean[] keys) {
        /*
         * This method rotates the player based on the arrow key being pressed
         */

        if (keys[KeyEvent.VK_A]) {
            dir.changeAngle(-Math.toRadians(ROTATIONAL_CONSTANT)); //rotates by rotational constant, arbitrary value for smoothness
        }
        if (keys[KeyEvent.VK_D]) {
            dir.changeAngle(Math.toRadians(ROTATIONAL_CONSTANT));
        }
    }

    public void update(boolean[] keys, ArrayList<Asteroid> asteroids, ArrayList<Debris> debris) {
        /*
         * This method updates the player object, and interacts with the asteroids and debris
         */
        //if player is dead, pause for a bit
        if (deadPauseTime > 0) {
            deadPauseTime--;
            return;
        }

        //update shoot cooldown
        shootCooldown--;

        //update player
        rotate(keys);
        move(keys);
        updatePolygon();

        //check for collisions, unless invulnerable then return early
        if (invulnerableTime > 0) {
            invulnerableTime--;
            return;
        }

        int collideIndex = checkAsteroids(asteroids);
        if (collideIndex >= 0) { //if player collides with an asteroid
            newObjects(collideIndex, asteroids, debris); //create new asteroids and debris
            newLife(); //lose a life
        }
    }

    public double getX() {
        //return x position
        return x;
    }

    public double getY() {
        //return y position
        return y;
    }

    public Vector2D getdir() {
        //return direction
        return dir;
    }

    public int canShoot(int lastShot, boolean[] keys) {
        /*
         * This method checks if the player can shoot, and returns the number of bullets it should shoot
         */
        if (shootCooldown > 0) {
            //if on cooldown, return -1
            return -1;
        }
        if (lastShot != 3 && !keys[KeyEvent.VK_W]) {
            /*
             * This handles alternating between single and triple shot
             * In the case of single shot, it will only shoot if the last shot was a triple shot
             * Vice versa for triple shot
             * This occurs when the player is not thrusting
             */
            return 3;
        }
        //if not on cooldown and not alternating, return 1, meaning shoot one bullet
        return 1;
    }

    public Polygon getPolygon() {
        //return polygon
        return new Polygon(polygonX, polygonY, polygonX.length);
    }

    public int checkAsteroids(ArrayList<Asteroid> asteroids) {
        /*
         * This method checks if the player collides with an asteroid, and returns the index of the asteroid it collides with in the ArrayList
         */
        Polygon pPoly = getPolygon(); //retrieve player polygon
        for (int i = 0; i < asteroids.size(); i++) { //iterate through asteroids
            Area intersectArea = new Area(pPoly); //create area of intersection
            intersectArea.intersect(new Area(asteroids.get(i).getPolygon())); //intersect with asteroid polygon
            if (!intersectArea.isEmpty()) { //if intersection is not empty
                return i;
            }
        }

        return -1; //if no collision, return -1
    }

    public void deadReset() {
        /*
         * This method resets the player to the center of the screen, and resets its velocity if it dies
         */
        this.x = GamePanel.WIDTH / 2;
        this.y = GamePanel.HEIGHT / 2;
        this.xVelo = 0;
        this.yVelo = 0;
    }

    public int getLives() {
        //return number of lives
        return lives;
    }

    private void newObjects(int collideIndex, ArrayList<Asteroid> asteroids, ArrayList<Debris> debris) {
        /* 
         * This method creates new asteroids and debris when the player collides with an asteroid
        */

        //create debris
        //random number of dots (6-8)
        for (int j = 0; j < Utilities.randint(6, 8); j++) {
            debris.add(new Debris(asteroids.get(collideIndex).getX(),
            asteroids.get(collideIndex).getY(),
            Utilities.randomdir(), 
            Debris.DOT));
        }

        //create debris lines (4 for player)
        for (int j = 0; j < 4; j++) {
            debris.add(new Debris(asteroids.get(collideIndex).getX(),
            asteroids.get(collideIndex).getY(),
            Utilities.randomdir(), 
            Debris.LINE));
        }

        //create new asteroids if the asteroid has not already split apart too much (2 times max)
        if (asteroids.get(collideIndex).getchildNum() < 2) {
            for (int i = 0; i < 2; i++) {
                //create new asteroid with random direction and position slightly off from original asteroid
                asteroids.add(new Asteroid(asteroids.get(collideIndex).getX() + Utilities.randint(-5, 5),
                asteroids.get(collideIndex).getY() + Utilities.randint(-5, 5),
                asteroids.get(collideIndex).getchildNum() + 1,
                Utilities.randomdir(), asteroids.get(collideIndex).getLevel()));
            }
        }
        //remove collided asteroid
        asteroids.remove(collideIndex);
    }

    public void newLife() {
        /*
         * This method handles the player losing a life
         */
        lives--;
        deadPauseTime = 5; //set pause time
        invulnerableTime = 45; //set invulnerable time, which does not count down while paused
        deadReset();
    }
}
