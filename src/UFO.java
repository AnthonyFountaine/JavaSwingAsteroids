import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.ArrayList;

public class UFO {
    private double x,y;
    private int destx, desty;
    private Vector2D dir;
    private final int SPEED = 3;
    private final int[] polygonXShape =  new int[] {-40, -20, -20, -15, 0, 15, 20, 20, 40, 30, 0, -30, -40}, polygonYShape = new int[] {21, 8, 0, -15, -20, -15, 0, 8, 21, 28, 36, 28, 21};
    private final int[] polygonX, polygonY;

    public UFO(double x, double y) {
        this.x = x;
        this.y = y;
        this.destx = Utilities.randint(0, GamePanel.WIDTH);
        this.desty = Utilities.randint(0, GamePanel.HEIGHT);
        this.dir = new Vector2D(new double[] {destx - x, desty - y});
        this.polygonX = new int[polygonXShape.length];
        this.polygonY = new int[polygonYShape.length];
        dir.normalize();
    }

    public void move() {
        x += dir.getxComp() * SPEED;
        y += dir.getyComp() * SPEED;
    }
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.drawArc((int)(x-20), (int)(y-20), 40, 40, -20, 220);
        g.drawOval((int)(x-40), (int)(y+6), 80, 30);
        g.setColor(Color.RED);
        g.drawPolygon(getPolygon());
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

    public void updatePolygon() {
        for (int i = 0; i < polygonXShape.length; i++) {
            polygonX[i] = (int) x + polygonXShape[i];
            polygonY[i] = (int) y + polygonYShape[i];
        }
    }

    public Polygon getPolygon() {
        return new Polygon(polygonX, polygonY, polygonX.length);
    }

    public void update(ArrayList<Asteroid> asteroids, ArrayList<Debris> debris) {
        checkDest();
        move();
        updatePolygon();
        //int collideIndex = checkAsteroids(asteroids);
        //if (collideIndex >= 0) {
            //newObjects(collideIndex, asteroids, debris);
        //}
    }

    private void newObjects(int collideIndex, ArrayList<Asteroid> asteroids, ArrayList<Debris> debris) {
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
    }
}
