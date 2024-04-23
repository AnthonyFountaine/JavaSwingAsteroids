import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

/*
 * Bullet.java
 * Anthony Fountaine
 * This class handles the player and UFO bullets. It moves the bullets, checks for collisions with asteroids and the player, and draws the bullets.
 */

public class Bullet {
    private double x, y;
    private Vector2D dir;
    private int timeOnScreen;
    private final int speed;
    private String bulletType;
    public final static int DEAD = -2, NO_ENEMIES_HIT = -1;

    public Bullet(double x, double y, Vector2D dir, String bulletOrigin) {
        //initialize direction
        this.dir = dir.copy();
        this.dir.normalize();

        //initialize position, slightly offset from the object of origin
        this.x = x + this.dir.getxComp() * 5;
        this.y = y + this.dir.getyComp() * 5;

        //initialize bullet properties
        this.timeOnScreen = 0;
        if (bulletOrigin == "Player") { //type of bullet (where it came from)
            bulletType = "PlayerBullet";
        }
        else if (bulletOrigin == "UFO") {
            bulletType = "UFOBullet";
        }

        //speed of bullet depends on where it came from
        this.speed = bulletType == "PlayerBullet" ? 12 : 7;
    }

    public void move() {
        /*
         * This method moves the bullet in the direction of the vector dir. It also checks if the bullet has gone off the screen and moves it to the opposite side of the screen if it has.
         */
        timeOnScreen++;
        x += this.dir.getxComp() * speed;
        y += this.dir.getyComp() * speed;

        //wrap around screen
        if (x < -10) {
            x = GamePanel.WIDTH + 10;
        }
        else if (x > GamePanel.WIDTH + 10) {
            x = -10;
        }
        if (y < -10) {
            y = GamePanel.HEIGHT + 10;
        }
        else if (y > GamePanel.HEIGHT + 10) {
            y = -10;
        }
    }

    public int checkAsteroids(ArrayList<Asteroid> asteroids) {
        /*
         * This method checks if the bullet has collided with any asteroids. If it has, it returns the index of the asteroid that was hit.
         */
        Rectangle bRect = getRect(); //get the rectangle of the bullet
        for (int i = 0; i < asteroids.size(); i++) {
            if (asteroids.get(i).getPolygon().intersects(bRect)) { //check if the asteroid intersects the bullet
                return i;
            }
        }

        return -1;
    }

    public void draw(Graphics g) {
        /*
         * This method draws the bullet on the screen. The color of the bullet depends on the type of bullet (player or UFO).
         */

        //set color of bullet
        if (bulletType == "PlayerBullet") {
            g.setColor(Color.GREEN);
        }
        else if (bulletType == "UFOBullet") {
            g.setColor(Color.RED);
        }

        //draw bullet
        g.fillOval((int)x, (int)y, 4, 4);
    }

    public int update(ArrayList<Asteroid> asteroids, ArrayList<Debris> debris, Player player, UFO ufo) {
        /*
         * This method updates the bullet. It moves the bullet, checks for collisions with asteroids and the player, and returns the score if an asteroid was hit.
         * If the bullet is a UFO bullet, it checks for collisions with the player.
         * If the bullet has been on the screen for too long, it returns DEAD.
         * If the bullet has not hit any enemies, it returns NO_ENEMIES_HIT.
         */
        if (checkDead()) {
            return DEAD;
        }
        move();


        if (bulletType == "PlayerBullet") {
            int collideIndex  = checkAsteroids(asteroids); //check if the bullet has collided with an asteroid
            if (collideIndex  >= 0) { //if the bullet has collided with an asteroid based on the returned index

                //retreive the score value of the asteroid that was hit
                int score = asteroids.get(collideIndex).getScore();

                //create debris
                for (int j = 0; j < Utilities.randint(6, 8); j++) {
                    debris.add(new Debris(asteroids.get(collideIndex).getX(),
                    asteroids.get(collideIndex).getY(),new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), Debris.DOT));
                }

                //create new asteroids if the asteroid that was hit was not the smallest asteroid
                if (asteroids.get(collideIndex).getchildNum() < 2) {
                    for (int i = 0; i < 2; i++) {
                        asteroids.add(new Asteroid(asteroids.get(collideIndex).getX() + Utilities.randint(-15, 15),
                        asteroids.get(collideIndex).getY() + Utilities.randint(-15, 15),
                        asteroids.get(collideIndex).getchildNum() + 1,
                        new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), asteroids.get(collideIndex).getLevel()));
                    }
                }

                //remove the asteroid that was hit
                asteroids.remove(collideIndex);
                return score; //return score of the asteroid that was hit
            }
            if (ufo != null) { //if the UFO exists
                if (ufo.getPolygon().intersects(getRect())) { //check if the bullet has collided with the UFO

                    //create debris
                    for (int j = 0; j < Utilities.randint(6, 8); j++) {
                        debris.add(new Debris(ufo.getX(), ufo.getY(), new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), Debris.DOT));
                    }
                    for (int i = 0; i < 5; i++) {
                        debris.add(new Debris(ufo.getX(), ufo.getY(), new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), Debris.ARC));
                    }

                    //return 1000, score associated with hitting the UFO
                    return 1000;
                }
                
            }
        }
        else if (bulletType == "UFOBullet") {
            Polygon pPoly = player.getPolygon(); //get the polygon of the player
            if (pPoly.intersects(getRect())) { //check if the bullet has collided with the player

                //create debris
                for (int j = 0; j < Utilities.randint(6, 8); j++) {
                    debris.add(new Debris(player.getX(), player.getY(), new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), Debris.DOT));
                }
                for (int j = 0; j < 4; j++) {
                    debris.add(new Debris(player.getX(), player.getY(), new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), Debris.LINE));
                }

                //player loses a life
                player.newLife();
                return DEAD; //return DEAD, meaning the bullet should be removed
            }
        }
        //if the bullet has not hit any enemies
        return NO_ENEMIES_HIT;
    }

    public boolean checkDead() {
        /*
         * This method checks if the bullet has been on the screen for too long. If it has, it returns true.
         */
        if (timeOnScreen >= 35 && bulletType == "PlayerBullet") {
            return true;
        }
        if (timeOnScreen >= 60 && bulletType == "UFOBullet") {
            return true;
        }
        return false;
    }

    public Rectangle getRect() {
        //return the rectangle of the bullet
        return new Rectangle((int)x, (int)y, 4, 4);
    }
}
