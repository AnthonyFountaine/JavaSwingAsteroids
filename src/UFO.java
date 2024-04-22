import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;

public class UFO {
    private double x,y;
    private int destx, desty;
    private Vector2D dir;
    private final int SPEED = 3;
    private int shootCooldown;
    private final int[] polygonXShape =  new int[] {-40, -20, -20, -15, 0, 15, 20, 20, 40, 30, 0, -30, -40}, polygonYShape = new int[] {21, 8, 0, -15, -20, -15, 0, 8, 21, 28, 36, 28, 21};
    private final int[] polygonX, polygonY;
    public static final int DEAD = -2;

    public UFO(double x, double y) {
        this.x = x;
        this.y = y;
        this.destx = Utilities.randint(0, GamePanel.WIDTH);
        this.desty = Utilities.randint(0, GamePanel.HEIGHT);
        this.dir = new Vector2D(new double[] {destx - x, desty - y});
        this.polygonX = new int[polygonXShape.length];
        this.polygonY = new int[polygonYShape.length];
        this.shootCooldown = 240;
        dir.normalize();
    }

    public void move() {
        x += dir.getxComp() * SPEED;
        y += dir.getyComp() * SPEED;
    }
    public void draw(Graphics g) {
        g.setColor(Color.PINK);
        g.drawArc((int)(x-10), (int)(y-10), 20, 20, -20, 220);
        g.drawOval((int)(x-20), (int)(y+3), 40, 15);
    }

    public void checkDest() {
        if (Math.abs(x - destx) < 10 && Math.abs(y - desty) < 10) {
            destx = Utilities.randint(0, GamePanel.WIDTH);
            desty = Utilities.randint(0, GamePanel.HEIGHT);
            dir = new Vector2D(new double[] {destx - x, desty - y});
            dir.normalize();
        }
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

    public void shoot(ArrayList<Bullet> bullets, Player player) {
        shootCooldown--;
        if (shootCooldown > 0) {
            return;
        }

        // 1 in 60 chance of shooting after 4 seconds, decrease as time goes
        if (Utilities.randint(1, 60 + shootCooldown/60) == 1) {
            double dx = player.getX() - this.x;
            double dy = player.getY() - this.y;
            Vector2D direction = new Vector2D(new double[] {dx, dy});
            bullets.add(new Bullet(x, y, direction, "UFO"));
            shootCooldown = 240;
        }
    }

    private boolean checkPlayer(Player player, ArrayList<Debris> debris) {
        Area intersectArea = new Area(getPolygon());
        intersectArea.intersect(new Area(player.getPolygon()));
        if (intersectArea.isEmpty() == false){
            for (int j = 0; j < Utilities.randint(6, 8); j++) {
                debris.add(new Debris(x,
                y,
                new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), 
                Debris.DOT));
            }
            for (int j = 0; j < 4; j++) {
                debris.add(new Debris(x,
                y,
                new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), 
                Debris.LINE));
            }
            for (int i = 0; i < 5; i++) {
                debris.add(new Debris(x,
                y, 
                new Vector2D(1, Math.toRadians(Utilities.randint(0, 360))), 
                Debris.ARC));
            }
            player.newLife();
            return true;
        }
        return false;
    }

    public int update(ArrayList<Asteroid> asteroids, ArrayList<Debris> debris, ArrayList<Bullet> bullets, Player player) {
        checkDest();
        move();
        if (checkPlayer(player, debris)) {
            return DEAD;
        }
        updatePolygon();
        shoot(bullets, player);

        return -1;
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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
