import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Asteroid {
    private double x, y;
    private int childNum;
    private Vector2D dir;
    private int speed;
    private final int WIDTH_HEIGHT = 60;

    public Asteroid(double x, double y, int childNum, Vector2D dir) {
        this.x = x;
        this.y = y;
        this.childNum = childNum;
        this.dir = dir.copy();
        this.speed = Utilities.randint(3, 6);
    }

    public int getchildNum() {
        return childNum;
    }

    public void move() {
        x += dir.getxComp() * speed;
        y += dir.getyComp() * speed;

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

    public void draw(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect((int)x, (int)y, WIDTH_HEIGHT/(childNum + 1), WIDTH_HEIGHT/(childNum + 1));
    }

    public Rectangle getRect() {
        return new Rectangle((int)x, (int)y, WIDTH_HEIGHT/(childNum + 1), WIDTH_HEIGHT/(childNum + 1));
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
}
