import java.awt.Color;
import java.awt.Graphics;

public class Bullet {
    private double x, y;
    private Vector2D dir;
    private int timeOnScreen;
    private final int SPEED = 20;

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

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval((int)x, (int)y, 4, 4);
    }

    public void update() {
        move();
    }

    public boolean checkDead() {
        if (timeOnScreen >= 25) {
            return true;
        }
        return false;
    }

}
