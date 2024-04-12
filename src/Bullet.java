import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Bullet {
    private double x, y;
    private Vector2D dir;
    private int timeOnScreen;
    private final int SPEED = 20;
    public final static int DEAD = -2, NO_ENEMIES_HIT = -1;

    public Bullet(double x, double y, Vector2D dir) {
        this.dir = dir.copy();
        this.x = x + dir.getxComp() * 5;
        this.y = y + dir.getxComp() * 5;
        this.timeOnScreen = 0;
    }

    public void move() {
        timeOnScreen++;
        x += dir.getxComp() * SPEED;
        y += dir.getyComp() * SPEED;

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
        Rectangle bRect = getRect();
        for (int i = 0; i < asteroids.size(); i++) {
            if (bRect.intersects(asteroids.get(i).getRect())) {
                return i;
            }
        }

        return -1;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval((int)x, (int)y, 4, 4);
    }

    public int update(ArrayList<Asteroid> asteroids, ArrayList<Debris> debris) {
        if (checkDead()) {
            return DEAD;
        }
        move();
        int collideIndex  = checkAsteroids(asteroids);
        if (collideIndex  >= 0) {
            for (int j = 0; j < Utilities.randint(6, 8); j++) {
                debris.add(new Debris(asteroids.get(collideIndex).getX(),
                asteroids.get(collideIndex).getY(),new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), Debris.DOT));
            }
            if (asteroids.get(collideIndex).getchildNum() < 2) {
                for (int i = 0; i < 2; i++) {
                    asteroids.add(new Asteroid(asteroids.get(collideIndex).getX() + Utilities.randint(-5, 5),
                    asteroids.get(collideIndex).getY() + Utilities.randint(-5, 5),
                    asteroids.get(collideIndex).getchildNum() + 1,
                    new Vector2D(1, Math.toRadians(Utilities.randint(0, 360)))));
                }
            }
            asteroids.remove(collideIndex);
            return DEAD;
        }
        return NO_ENEMIES_HIT;
    }

    public boolean checkDead() {
        if (timeOnScreen >= 25) {
            return true;
        }
        return false;
    }

    public Rectangle getRect() {
        return new Rectangle((int)x, (int)y, 4, 4);
    }
}
