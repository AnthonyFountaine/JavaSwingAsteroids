import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
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
    private int level;
 
    public Asteroid(double x, double y, int childNum, Vector2D dir, int level) {
        this.x = x;
        this.y = y;
        this.childNum = childNum;
        this.dir = dir.copy();
        this.level = level;
        this.speed = Utilities.randint(1, 2) * this.level/10 + 1;
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

        if (x < -40 + WIDTH_HEIGHT * (childNum + 1)) {
            x = GamePanel.WIDTH + 40 - WIDTH_HEIGHT * (childNum + 1);
        }
        else if (x > GamePanel.WIDTH + 40 - WIDTH_HEIGHT * (childNum + 1)) {
            x = -40  + WIDTH_HEIGHT * (childNum + 1);
        }
        if (y < -40 + WIDTH_HEIGHT * (childNum + 1)) {
            y = GamePanel.HEIGHT + 40 - WIDTH_HEIGHT * (childNum + 1);
        }
        else if (y > GamePanel.HEIGHT + 40 - WIDTH_HEIGHT * (childNum + 1)) {
            y = -40 + WIDTH_HEIGHT * (childNum + 1);
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

    public int getLevel() {
        return level;
    }

    public static int randomSpawnLocation(String type) {
        int randomLocation;
        if (type == "x") {
            randomLocation = GamePanel.WIDTH/2;
            while (true) {
                if (new Rectangle(GamePanel.WIDTH/2 - 50, GamePanel.HEIGHT/2 - 50, 100, 100).contains(new Point(randomLocation, GamePanel.HEIGHT/2))) {
                    randomLocation = Utilities.randint(-40, GamePanel.WIDTH + 40);
                }
                else{
                    break;
                }
            }
        }
        else{
            randomLocation = GamePanel.HEIGHT/2;
            while (true) {
                if (new Rectangle(GamePanel.WIDTH/2 - 50, GamePanel.HEIGHT/2 - 50, 100, 100).contains(new Point(GamePanel.WIDTH/2, randomLocation))) {
                    randomLocation = Utilities.randint(-40, GamePanel.HEIGHT + 40);
                }
                else{
                    break;
                }
            }
        }
        return randomLocation;
    }
}
