public class Vector2D {
    private double xComp, yComp, magnitude, angle;

    public Vector2D(double[] components) {
       this.xComp = components[0];
       this.yComp = components[1];
       this.magnitude = calcMagnitude();
       this.angle = calcAngle();
    }

    public Vector2D(double magnitude, double angle) {
        this.magnitude = magnitude;
        this.angle = angle;
        this.xComp = calcComponents()[0];
        this.yComp = calcComponents()[1];
    }

    private double calcMagnitude() {
        return Math.sqrt(xComp * xComp + yComp * yComp);
    }

    private double calcAngle() {
        return Math.atan(yComp/xComp);
    }

    private double[] calcComponents() {
        return new double[] {magnitude*Math.cos(angle), magnitude*Math.sin(angle)};
    }

    public void normalize() {
        double originalMagnitude = magnitude;
        xComp = xComp/originalMagnitude;
        yComp = yComp/originalMagnitude;
        magnitude = 1;
    }

    public void setxComp(double xComp) {
        this.xComp = xComp;
        magnitude = calcMagnitude();
    }

    public void setyComp(double yComp) {
        this.yComp = yComp;
        magnitude = calcMagnitude();
    }

    public double getxComp() {
        return xComp;
    }

    public double getyComp() {
        return yComp;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public double getAngle() {
        return angle;
    }

    public void changeAngle(double amount) {
        angle += amount;
        xComp = calcComponents()[0];
        yComp = calcComponents()[1];
        normalize();
    }

    public Vector2D copy() {
        return new Vector2D(getMagnitude(), getAngle());
    }
}
