import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.Arrays;

public class Asteroid {
    private double x, y;
    private int childNum;
    private Vector2D dir;
    private int speed;
    private final int WIDTH_HEIGHT = 9;
    private final int[][] polygonXShapes = new int[][] {{1,3,6,9,10,9,9,8,5,3,2,0,2,0,1}, {1,3,5,7,9,10,9,9,4,2,1,1,0,1}, {1,3,3,6,8,9,10,8,8,9,5,3,2,0,0,1,1}, {1,3,5,8,8,10,8,6,7,4,2,0,1,1}};
    private final int[][] polygonYShapes = new int[][] {{1,1,0,1,5,6,8,9,10,10,8,7,4,4,1}, {2,2,0,2,3,6,7,9,11,9,9,8,5,2}, {1,2,0,1,0,2,4,6,7,8,10,8,9,8,5,3,1}, {1,1,0,1,3,6,9,8,10,10,9,6,4,1}};
    private final int[] polygonX, polygonY;
    private int randomshape;
    private final int score;
 
    public Asteroid(double x, double y, int childNum, Vector2D dir) {
        this.x = x;
        this.y = y;
        this.childNum = childNum;
        this.dir = dir.copy();
        this.speed = Utilities.randint(1, 2);
        this.randomshape = Utilities.randint(0, polygonXShapes.length - 1);
        this.polygonX = Arrays.copyOf(polygonXShapes[randomshape], polygonXShapes[randomshape].length);
        this.polygonY = Arrays.copyOf(polygonYShapes[randomshape], polygonYShapes[randomshape].length);
        this.score = 600/(childNum + 1);
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
        g.setColor(Color.MAGENTA);
        g.drawPolygon(getPolygon());
    }

    public void updatePolygon() {
        for (int i = 0; i < polygonX.length; i++) {
            polygonX[i] = (int) (polygonXShapes[randomshape][i] * (WIDTH_HEIGHT / (childNum + 1)) + x);
            polygonY[i] = (int) (polygonYShapes[randomshape][i] * (WIDTH_HEIGHT / (childNum + 1)) + y);
        }
    }

    public Polygon getPolygon() {
        return new Polygon(polygonX, polygonY, polygonX.length);
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

    public void update() {
        move();
        updatePolygon();
    }

    public int getScore() {
        return score;
    }
}
