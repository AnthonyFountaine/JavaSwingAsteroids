import java.awt.Color;
import java.awt.Graphics;

/*
 * Debris.java
 * Anthony Fountaine
 * This class handles the debris objects when other objects die with movement, and drawing
 */

public class Debris {
    private double x, y;
    private Vector2D dir;
    private int type;
    private double length, angle;
    private int speed;
    private int timeOnScreen;
    public final static int LINE = 1, DOT = 2, ARC = 3;
    public final static int DEAD = -2, NULL = -1;


    public Debris(double x, double y, Vector2D dir, int type) {
        //initialize positions
        this.x = x;
        this.y = y;

        //initialize debris properties
        this.dir = dir.copy();
        this.type = type;
        this.length = 20;
        this.angle = Math.toRadians((double)Utilities.randint(0, 360));
        this.speed = Utilities.randint(8, 11);
        this.timeOnScreen = 0; //time debris has been on screen
    }

    public void move() {
        /*
         * This method moves the debris in the direction of its direction vector
         */
        timeOnScreen++;
        x += dir.getxComp() * speed;
        y += dir.getyComp() * speed;
    }

    public void draw(Graphics g) {
        /*
         * This method draws the debris onto the screen based on its type (line, dot, or arc)
         * line: white line -> represents player death
         * dot: orange dot -> represents asteroid death
         * arc: pink arc -> represents UFO death
         */
        if (type == LINE) {
            g.setColor(Color.WHITE);
            g.drawLine((int)x, (int)y, (int)(x + length * Math.cos(angle)), (int)(y + length * Math.sin(angle)));
        }
        else if(type == ARC) {
            g.setColor(Color.PINK);
            //arbitrary values to make arc look better
            g.drawArc((int)x, (int)y, 20, 20, (int)(Math.toDegrees(angle)), 75);
        }
        else{
            g.setColor(Color.ORANGE);
            g.fillOval((int) x, (int) y, 3, 3);
        }
    }

    public boolean checkDead() {
        /*
         * This method checks if the debris has been on screen for 15 frames, if so, it is should be removed, and returns true
         */
        if (timeOnScreen >= 15) {
            return true;
        }
        return false;
    }

    public int update() {
        /*
         * This method updates the debris object
         */
        if (checkDead()) { //if debris time has expired, return DEAD
            return DEAD; 
        }
        move();
        return NULL; //otherwise, return NULL, meaning no change
    }
}
