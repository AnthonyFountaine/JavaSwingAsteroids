import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;

public class UFO {
    private double x,y;
    private int destx, desty;
    private Vector2D dir;
    private final int SPEED = 5;
    private final int[] polygonX, polygonY;

    public UFO(double x, double y) {
        this.x = x;
        this.y = y;
        this.destx = Utilities.randint(0, GamePanel.WIDTH);
        this.desty = Utilities.randint(0, GamePanel.HEIGHT);
        this.dir = new Vector2D(new double[] {destx - x, desty - y});
        polygonX = new int[] {0, 20, 20, 0};
        polygonY = new int[] {0, 20, 20, 0};
        dir.normalize();
    }

    public void move() {
        x += dir.getxComp() * SPEED;
        y += dir.getyComp() * SPEED;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval((int)x, (int)y, 20, 20);
    }

    public void checkDest() {
        // System.out.println(x + " " + y + " " + destx + " " + desty);
        if (Math.abs(x - destx) < 10 && Math.abs(y - desty) < 10) {
            destx = Utilities.randint(0, GamePanel.WIDTH);
            desty = Utilities.randint(0, GamePanel.HEIGHT);
            dir = new Vector2D(new double[] {destx - x, desty - y});
            dir.normalize();
        }
    }

    public int checkAsteroids(ArrayList<Asteroid> asteroids) {
        Polygon pPoly = getPolygon();
        for (int i = 0; i < asteroids.size(); i++) {
            Area intersectArea = new Area(pPoly);
            intersectArea.intersect(new Area(asteroids.get(i).getPolygon()));
            if (!intersectArea.isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    public Polygon getPolygon() {
        return new Polygon(polygonX, polygonY, polygonX.length);
    }

    public void update() {
        checkDest();
        move();
    }
}
