import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Player {
    private double x, y;
    private double xVelo, yVelo;
    private Vector2D dir;
    private final double FRICTION_CONSTANT = 0.95, ROTATIONAL_CONSTANT = 9;
    private int[] polygonX, polygonY;
    private boolean thrust = false;
    private int invulnerableTime, deadPauseTime;
    public int shootCooldown;

    public Player() {
        this.x = GamePanel.WIDTH/2;
        this.y = GamePanel.HEIGHT/2;
        this.dir = new Vector2D(1, Math.toRadians(90));
        this.xVelo = 0;
        this.yVelo = 0;
        this.shootCooldown = 0;
        this.invulnerableTime = 40;
        this.polygonX = new int[6];
        this.polygonY = new int[6];
    }

    public void move(boolean[] keys) {
        veloUpdate(keys);
        x+= xVelo;
        y+= yVelo;

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
        dir.normalize();

        if (keys[KeyEvent.VK_W]) {
            thrust = !thrust;
            xVelo += dir.getxComp()/2;
            yVelo += dir.getyComp()/2;
        }
        else {
            thrust = false;
        }

        xVelo*=FRICTION_CONSTANT;
        yVelo*=FRICTION_CONSTANT;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        if ((invulnerableTime / 4) % 2 == 0) {
            g.drawPolygon(polygonX, polygonY, polygonX.length);
            if (thrust) {
                g.drawLine((int)(x + (30 * -Math.cos(dir.getAngle() - .2)) * 3/4), (int)(y + (30 * -Math.sin(dir.getAngle() - .2)) * 3/4), (int)(x + (32 * -Math.cos(dir.getAngle()))), (int)(y + (32 * -Math.sin(dir.getAngle()))));
                g.drawLine((int)(x + (30 * -Math.cos(dir.getAngle() + .2)) * 3/4), (int)(y + (30 * -Math.sin(dir.getAngle() + .2)) * 3/4), (int)(x + (32 * -Math.cos(dir.getAngle()))), (int)(y + (32 * -Math.sin(dir.getAngle()))));
            }
        }
    }

    public void updatePolygon() {
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
        if (keys[KeyEvent.VK_A]) {
            dir.changeAngle(-Math.toRadians(ROTATIONAL_CONSTANT));
        }
        if (keys[KeyEvent.VK_D]) {
            dir.changeAngle(Math.toRadians(ROTATIONAL_CONSTANT));
        }
    }

    public void update(boolean[] keys, ArrayList<Asteroid> asteroids, ArrayList<Debris> debris) {
        if (deadPauseTime > 0) {
            deadPauseTime--;
            return;
        }
        shootCooldown--;
        rotate(keys);
        move(keys);
        updatePolygon();
        int collideIndex;
        if (invulnerableTime > 0) {
            invulnerableTime--;
            collideIndex = -1;
        }
        collideIndex = checkAsteroids(asteroids);
        if (collideIndex >= 0) {
            for (int j = 0; j < Utilities.randint(6, 8); j++) {
                debris.add(new Debris(asteroids.get(collideIndex).getX(),
                asteroids.get(collideIndex).getY(),new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), Debris.DOT));
            }
            for (int j = 0; j < 4; j++) {
                debris.add(new Debris(asteroids.get(collideIndex).getX(),
                asteroids.get(collideIndex).getY(),new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), Debris.LINE));
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
            deadPauseTime = 5;
            invulnerableTime = 45;
            deadReset();
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Vector2D getdir() {
        return dir;
    }

    public int canShoot(int lastShot, boolean[] keys) {
        if (shootCooldown > 0) {
            return -1;
        }
        if (lastShot != 3 && !keys[KeyEvent.VK_W]) {
            return 3;
        }
        return 1;
    }

    public Rectangle getRect() {
        int[] xVals = Arrays.copyOf(polygonX, polygonX.length);
        Arrays.sort(xVals);
        int[] yVals = Arrays.copyOf(polygonY, polygonY.length);
        Arrays.sort(yVals);

        return new Rectangle(xVals[0], yVals[0], xVals[xVals.length - 1] - xVals[0], yVals[yVals.length - 1] - yVals[0]);
    }

    public int checkAsteroids(ArrayList<Asteroid> asteroids) {
        Rectangle pRect = getRect();
        for (int i = 0; i < asteroids.size(); i++) {
            if (pRect.intersects(asteroids.get(i).getRect())) {
                return i;
            }
        }

        return -1;
    }

    public void deadReset() {
        this.x = GamePanel.WIDTH / 2;
        this.y = GamePanel.HEIGHT / 2;
        this.xVelo = 0;
        this.yVelo = 0;
    }
}
