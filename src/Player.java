import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Player {
    private double x, y;
    private double xVelo, yVelo;
    private int angle;
    private int width, height;
    private final double MAX_VELO = 10;
    private final int SPEED = 30;
    private final double FRICTION_CONSTANT = 0.95;

    public Player() {
        this.x = GamePanel.WIDTH/2;
        this.y = GamePanel.HEIGHT/2;
        this.width = 30;
        this.height = 50;
        this.angle = 90;
        this.xVelo = 0;
        this.yVelo = 0;
    }

    public void move(boolean[] keys) {
        veloUpdate(keys);
        x+= xVelo;
        y+= yVelo;
    }

    private void veloUpdate(boolean[] keys) {
        
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect((int)x - width/2, (int)y - height/2, width, height);
    }

    public void rotate() {
        return;
    }

    public void update(boolean[] keys) {
        move(keys);
    }
}
