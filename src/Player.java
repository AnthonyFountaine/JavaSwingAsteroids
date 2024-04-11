import java.awt.*;
import java.awt.event.*;

public class Player {
    private double x, y;
    private double xVelo, yVelo;
    private Vector2D dir;
    private final double FRICTION_CONSTANT = 0.95, ROTATIONAL_CONSTANT = 9;
    private boolean thrust = false;
    public int shootCooldown;

    public Player() {
        this.x = GamePanel.WIDTH/2;
        this.y = GamePanel.HEIGHT/2;
        this.dir = new Vector2D(1, Math.toRadians(90));
        this.xVelo = 0;
        this.yVelo = 0;
        this.shootCooldown = 0;
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
        g.drawLine((int)x, (int)y, (int)(x + 30 * -Math.cos(dir.getAngle() - .3)), (int)(y + 30 * -Math.sin(dir.getAngle() - .3)));
        g.drawLine((int)x, (int)y, (int)(x + 30 * -Math.cos(dir.getAngle() + .3)), (int)(y + 30 * -Math.sin(dir.getAngle() + .3)));
        g.drawLine((int)(x + (30 * -Math.cos(dir.getAngle() - .3)) * 3/4), (int)(y + (30 * -Math.sin(dir.getAngle() - .3)) * 3/4), (int)(x + (30 * -Math.cos(dir.getAngle() + .3)) * 3/4), (int)(y + (30 * -Math.sin(dir.getAngle() + .3)) * 3/4));
        if (thrust) {
            g.drawLine((int)(x + (30 * -Math.cos(dir.getAngle() - .2)) * 3/4), (int)(y + (30 * -Math.sin(dir.getAngle() - .2)) * 3/4), (int)(x + (32 * -Math.cos(dir.getAngle()))), (int)(y + (32 * -Math.sin(dir.getAngle()))));
            g.drawLine((int)(x + (30 * -Math.cos(dir.getAngle() + .2)) * 3/4), (int)(y + (30 * -Math.sin(dir.getAngle() + .2)) * 3/4), (int)(x + (32 * -Math.cos(dir.getAngle()))), (int)(y + (32 * -Math.sin(dir.getAngle()))));
        }
    }

    public void rotate(boolean[] keys) {
        if (keys[KeyEvent.VK_A]) {
            dir.changeAngle(-Math.toRadians(ROTATIONAL_CONSTANT));
        }
        if (keys[KeyEvent.VK_D]) {
            dir.changeAngle(Math.toRadians(ROTATIONAL_CONSTANT));
        }
    }

    public void update(boolean[] keys) {
        shootCooldown--;
        rotate(keys);
        move(keys);
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
}
