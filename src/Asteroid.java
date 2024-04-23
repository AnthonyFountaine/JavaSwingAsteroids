import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.Arrays;

/*
 * Asteroid.java
 * Anthony Fountaine
 * This class handles the asteroid objects with movement, collision detection, and drawing
 */

public class Asteroid {
    private double x, y;
    private int childNum; //# representing the number of previous splits
    private Vector2D dir;
    private int speed;
    private int timeOutOfBounds; //time asteroid has been out of bounds
    private final int WIDTH_HEIGHT = 9;
    private final int[][] polygonXShapes = new int[][] {{1,3,6,9,10,9,9,8,5,3,2,0,2,0,1}, {1,3,5,7,9,10,9,9,4,2,1,1,0,1}, {1,3,3,6,8,9,10,8,8,9,5,3,2,0,0,1,1}, {1,3,5,8,8,10,8,6,7,4,2,0,1,1}}; //shapes of asteroid polygons
    private final int[][] polygonYShapes = new int[][] {{1,1,0,1,5,6,8,9,10,10,8,7,4,4,1}, {2,2,0,2,3,6,7,9,11,9,9,8,5,2}, {1,2,0,1,0,2,4,6,7,8,10,8,9,8,5,3,1}, {1,1,0,1,3,6,9,8,10,10,9,6,4,1}}; //shapes of asteroid polygons
    private final int[] polygonX, polygonY;
    private int randomshape; //random shape of asteroid
    private final int score;
    private int level; //level of asteroid
 
    public Asteroid(double x, double y, int childNum, Vector2D dir, int level) {    
        //initialize positions
        this.x = x;
        this.y = y;

        //initialize asteroid properties
        this.childNum = childNum;
        this.dir = dir.copy();
        this.speed = Utilities.randint(1, 1) + (level/10 + 1); //speed of asteroid relative to level
        this.randomshape = Utilities.randint(0, polygonXShapes.length - 1); //generate random shape
        this.polygonX = Arrays.copyOf(polygonXShapes[randomshape], polygonXShapes[randomshape].length);
        this.polygonY = Arrays.copyOf(polygonYShapes[randomshape], polygonYShapes[randomshape].length);

        this.timeOutOfBounds = 0;
        
        //initialize score
        this.score = 600/(childNum + 1);
        this.level = level;
    }

    public int getchildNum() {
        //return the number of times the asteroid has been split
        return childNum;
    }

    public void move() {
        /*
         * This method moves the asteroid in the direction of its direction vector
         */
        x += dir.getxComp() * speed;
        y += dir.getyComp() * speed;

        //wrap around screen
        if (x < -80) {
            x = GamePanel.WIDTH + 80;
        }
        else if (x > GamePanel.WIDTH + 80) {
            x = -80 ;
        }
        if (y < - 80) {
            y = GamePanel.HEIGHT + 80;
        }
        else if (y > GamePanel.HEIGHT + 80) {
            y = -80;
        }

        //if asteroid has been out of bounds for 3 seconds, change direction
        //this is to avoid sitatuions where it is spawned off screen and has a direction that will never bring it back on screen
        if (x + getPolygon().getBounds().width < 0 || x > GamePanel.WIDTH || y + getPolygon().getBounds().height< 0 || y > GamePanel.HEIGHT) {
            timeOutOfBounds++;
        }
        else {
            timeOutOfBounds = 0;
        }
        if (timeOutOfBounds > 300) {
            dir = Utilities.randomdir();
        }
    }

    public void draw(Graphics g) {
        /*
         * This method draws the asteroid onto the screen using a polygon
         */
        g.setColor(Color.MAGENTA);
        g.drawPolygon(getPolygon());
    }

    public void updatePolygon() {
        /*
         * This method updates the asteroid's polygon based on its position using the random shape
         */
        for (int i = 0; i < polygonX.length; i++) {
            polygonX[i] = (int) (polygonXShapes[randomshape][i] * (WIDTH_HEIGHT / (childNum + 1)) + x);
            polygonY[i] = (int) (polygonYShapes[randomshape][i] * (WIDTH_HEIGHT / (childNum + 1)) + y);
        }
    }

    public Polygon getPolygon() {
        //return the asteroid's polygon
        return new Polygon(polygonX, polygonY, polygonX.length);
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
        //return direction vector
        return dir;
    }

    public void update() {
        /*
         * This method updates the asteroid's position and polygon
         */
        move();
        updatePolygon();
    }

    public int getScore() {
        //return score associated with asteroid
        return score;
    }

    public int getLevel() {
        //return level asteroid was spawned at
        return level;
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
}
