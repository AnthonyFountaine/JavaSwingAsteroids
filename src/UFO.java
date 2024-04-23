import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;

/*
 * UFO.java
 * Anthony Fountaine
 * This class handles the UFO enemy with shooting, movement, collision detection, and drawing
 */


public class UFO {
    private double x,y; 
    private int destx, desty;
    private Vector2D dir;
    private final int SPEED = 3;
    private int shootCooldown;
    private final int[] polygonXShape =  new int[] {-40, -20, -20, -15, 0, 15, 20, 20, 40, 30, 0, -30, -40}, polygonYShape = new int[] {21, 8, 0, -15, -20, -15, 0, 8, 21, 28, 36, 28, 21}; //shape of UFO polygon
    private final int[] polygonX, polygonY; //actual polygon
    public static final int DEAD = -2; //Constant return value for update method

    public UFO(double x, double y) {
        /*
         * This constructor initializes the UFO object with a random destination and direction and set position
         */
        //initialize positions
        this.x = x;
        this.y = y;
        this.destx = Utilities.randint(0, GamePanel.WIDTH);
        this.desty = Utilities.randint(0, GamePanel.HEIGHT);

        //initialize direction from UFO to destination
        this.dir = new Vector2D(new double[] {destx - x, desty - y});
        dir.normalize();

        //create polygon
        this.polygonX = new int[polygonXShape.length];
        this.polygonY = new int[polygonYShape.length];

        this.shootCooldown = 240;
    }

    public void move() {
        /*
         * This method moves the UFO towards its destination
         */
        x += dir.getxComp() * SPEED;
        y += dir.getyComp() * SPEED;
    }
    public void draw(Graphics g) {
        /*
         * This method draws the UFO onto the screen
         */
        g.setColor(Color.PINK);

        //arbitrary values to make UFO look better
        g.drawArc((int)(x-10), (int)(y-10), 20, 20, -20, 220);
        g.drawOval((int)(x-20), (int)(y+3), 40, 15);
    }

    public void checkDest() {
        /*
         * This method checks if the UFO has reached its destination and sets a new one if it has
         */
        if (Math.abs(x - destx) < 10 && Math.abs(y - desty) < 10) {
            destx = Utilities.randint(0, GamePanel.WIDTH);
            desty = Utilities.randint(0, GamePanel.HEIGHT);
            dir = new Vector2D(new double[] {destx - x, desty - y});
            dir.normalize();
        }
    }

    public void updatePolygon() {
        /*
         * This method updates the UFO's polygon based on its position using the static shape
         */
        for (int i = 0; i < polygonXShape.length; i++) {
            polygonX[i] = (int) x + polygonXShape[i];
            polygonY[i] = (int) y + polygonYShape[i];
        }
    }

    public Polygon getPolygon() {
        /*
         * This method returns the UFO's polygon
         */
        return new Polygon(polygonX, polygonY, polygonX.length);
    }

    public void shoot(ArrayList<Bullet> bullets, Player player) {
        /*
         * This method handles the UFO's shooting if the cooldown has expired
         */
        shootCooldown--;
        if (shootCooldown > 0) {
            return;
        }

        // 1 in 60 chance of shooting after 4 seconds, decrease as time goes
        if (Utilities.randint(1, 60 + shootCooldown/60) == 1) {
            //shoot at player
            double dx = player.getX() - this.x;
            double dy = player.getY() - this.y;

            //initialize direction
            Vector2D direction = new Vector2D(new double[] {dx, dy});

            //create bullet
            bullets.add(new Bullet(x, y, direction, "UFO"));

            //handle cooldown
            shootCooldown = 240;
        }
    }

    private boolean checkPlayer(Player player, ArrayList<Debris> debris) {
        /*
         * This method checks if the UFO has collided with the player and handles the collision
         */
        Area intersectArea = new Area(getPolygon()); //create area of UFO
        intersectArea.intersect(new Area(player.getPolygon())); //intersect with player
        if (intersectArea.isEmpty() == false){ //if not empty, collision has occured
            //create debris
            for (int j = 0; j < Utilities.randint(6, 8); j++) {
                debris.add(new Debris(x,
                y,
                Utilities.randomdir(), 
                Debris.DOT));
            }
            for (int j = 0; j < 4; j++) {
                debris.add(new Debris(x,
                y,
                Utilities.randomdir(), 
                Debris.LINE));
            }
            for (int i = 0; i < 5; i++) {
                debris.add(new Debris(x,
                y, 
                Utilities.randomdir(), 
                Debris.ARC));
            }

            //reset player
            player.newLife();
            return true;
        }
        return false;
    }

    public int update(ArrayList<Asteroid> asteroids, ArrayList<Debris> debris, ArrayList<Bullet> bullets, Player player) {
        /*
         * This method updates the UFO's position, checks for collisions, and shoots
         * It returns DEAD if the UFO should be killed
         * -1 if nothing has occured
         */
        checkDest();
        move();
        if (checkPlayer(player, debris)) {
            return DEAD;
        }
        updatePolygon();
        shoot(bullets, player);

        return -1;
    }

    public static int randomSpawnLocation(int avoid) {
        /*
         * This method creates a random spawn location that is not near the player
         * avoid will always be the full width or full height of the screen
         * player spawns in the middle of those values
         * -> avoid/2
         */
        int randomLocation = avoid/2; //purposefully set it to the bad location
        while (true) {
            if (avoid/2 - 100 < randomLocation && randomLocation < avoid/2 + 100) { //if near player
                randomLocation = Utilities.randint(-40, avoid + 40); //reset value
            }
            else {
                break;
            }
        }
        return randomLocation;
    }

    public double getX() {
        //return x position
        return x;
    }

    public double getY() {
        //return y position
        return y;
    }
}
