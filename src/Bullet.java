import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Bullet {
    private double x, y;
    private Vector2D dir;
    private int timeOnScreen;
    private final int speed;
    private String bulletType;
    public final static int DEAD = -2, NO_ENEMIES_HIT = -1;

    public Bullet(double x, double y, Vector2D dir, String bulletOrigin) {
        this.dir = dir.copy();
        this.dir.normalize();
        this.x = x + this.dir.getxComp() * 5;
        this.y = y + this.dir.getyComp() * 5;
        this.timeOnScreen = 0;
        if (bulletOrigin == "Player") {
            bulletType = "PlayerBullet";
        }
        else if (bulletOrigin == "UFO") {
            bulletType = "UFOBullet";
        }
        this.speed = bulletType == "PlayerBullet" ? 12 : 7;
    }

    public void move() {
        timeOnScreen++;
        x += this.dir.getxComp() * speed;
        y += this.dir.getyComp() * speed;

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
            if (asteroids.get(i).getPolygon().intersects(bRect)) {
                return i;
            }
        }

        return -1;
    }

    public void draw(Graphics g) {
        if (bulletType == "PlayerBullet") {
            g.setColor(Color.GREEN);
        }
        else if (bulletType == "UFOBullet") {
            g.setColor(Color.RED);
        }
        g.fillOval((int)x, (int)y, 4, 4);
    }

    public int update(ArrayList<Asteroid> asteroids, ArrayList<Debris> debris, Player player, UFO ufo) {
        if (checkDead()) {
            return DEAD;
        }
        move();
        if (bulletType == "PlayerBullet") {
            int collideIndex  = checkAsteroids(asteroids);
            if (collideIndex  >= 0) {
                int score = asteroids.get(collideIndex).getScore();
                for (int j = 0; j < Utilities.randint(6, 8); j++) {
                    debris.add(new Debris(asteroids.get(collideIndex).getX(),
                    asteroids.get(collideIndex).getY(),new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), Debris.DOT));
                }
                if (asteroids.get(collideIndex).getchildNum() < 2) {
                    for (int i = 0; i < 2; i++) {
                        asteroids.add(new Asteroid(asteroids.get(collideIndex).getX() + Utilities.randint(-15, 15),
                        asteroids.get(collideIndex).getY() + Utilities.randint(-15, 15),
                        asteroids.get(collideIndex).getchildNum() + 1,
                        new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), asteroids.get(collideIndex).getLevel()));
                    }
                }
                asteroids.remove(collideIndex);
                return score;
            }
            if (ufo != null) {
                if (ufo.getPolygon().intersects(getRect())) {
                    for (int j = 0; j < Utilities.randint(6, 8); j++) {
                        debris.add(new Debris(ufo.getX(), ufo.getY(), new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), Debris.DOT));
                    }
                    for (int i = 0; i < 5; i++) {
                        debris.add(new Debris(ufo.getX(), ufo.getY(), new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), Debris.ARC));
                    }
                    return 1000;
                }
                
            }
        }
        else if (bulletType == "UFOBullet") {
            Polygon pPoly = player.getPolygon();
            if (pPoly.intersects(getRect())) {
                for (int j = 0; j < Utilities.randint(6, 8); j++) {
                    debris.add(new Debris(player.getX(), player.getY(), new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), Debris.DOT));
                }
                for (int j = 0; j < 4; j++) {
                    debris.add(new Debris(player.getX(), player.getY(), new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), Debris.LINE));
                }
                player.newLife();
                return DEAD;
            }
        }
        return NO_ENEMIES_HIT;
    }

    public boolean checkDead() {
        if (timeOnScreen >= 35 && bulletType == "PlayerBullet") {
            return true;
        }
        if (timeOnScreen >= 60 && bulletType == "UFOBullet") {
            return true;
        }
        return false;
    }

    public Rectangle getRect() {
        return new Rectangle((int)x, (int)y, 4, 4);
    }
}
