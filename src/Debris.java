import java.awt.Color;
import java.awt.Graphics;

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
        this.x = x;
        this.y = y;
        this.dir = dir.copy();
        this.type = type;
        this.length = 20;
        this.angle = Math.toRadians((double)Utilities.randint(0, 360));
        this.speed = Utilities.randint(8, 11);
        this.timeOnScreen = 0;
    }

    public void move() {
        timeOnScreen++;
        x += dir.getxComp() * speed;
        y += dir.getyComp() * speed;
    }

    public void draw(Graphics g) {
        if (type == LINE) {
            g.setColor(Color.WHITE);
            g.drawLine((int)x, (int)y, (int)(x + length * Math.cos(angle)), (int)(y + length * Math.sin(angle)));
        }
        else if(type == ARC) {
            g.setColor(Color.PINK);
            g.drawArc((int)x, (int)y, 20, 20, (int)(Math.toDegrees(angle)), 75);
        }
        else{
            g.setColor(Color.ORANGE);
            g.fillOval((int) x, (int) y, 3, 3);
        }
    }

    public boolean checkDead() {
        if (timeOnScreen >= 15) {
            return true;
        }
        return false;
    }

    public int update() {
        if (checkDead()) {
            return DEAD; 
        }
        move();
        return NULL;
    }
}
