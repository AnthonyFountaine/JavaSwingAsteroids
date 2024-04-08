import java.awt.*;
import java.awt.event.*;

public class Player {
    private double x, y;
    private double xVelo, yVelo;
    private int width, height;
    private Vector2D dir;
    private final double MAX_VELO = 7;
    private final int SPEED = 4;
    private final double FRICTION_CONSTANT = 0.95, ROTATIONAL_CONSTANT = 9;
    private boolean thrust = false;

    public Player() {
        this.x = GamePanel.WIDTH/2;
        this.y = GamePanel.HEIGHT/2;
        this.width = 30;
        this.height = 50;
        this.dir = new Vector2D(1, Math.toRadians(90));
        this.xVelo = 0;
        this.yVelo = 0;
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

        if (keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP]) {
            thrust = !thrust;
            if (-MAX_VELO < xVelo && xVelo < MAX_VELO) {
                xVelo += dir.getxComp()/2;
            }
            if (-MAX_VELO < yVelo && yVelo < MAX_VELO) {
                yVelo += dir.getyComp()/2;
            }
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
    }

    public void rotate(boolean[] keys) {
        if (keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT]) {
            dir.changeAngle(Math.toRadians(ROTATIONAL_CONSTANT));
        }
        if (keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT]) {
            dir.changeAngle(-Math.toRadians(ROTATIONAL_CONSTANT));
        }
        System.out.println(dir.getAngle());
    }

    public void update(boolean[] keys) {
        rotate(keys);
        move(keys);
    }
}
