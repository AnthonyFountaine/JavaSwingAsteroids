/*
 * Vector2D.java
 * Anthony Fountaine
 * This class handles the vector objects with components, magnitude, and angle
 */

public class Vector2D {
    //components of a vector
    private double xComp, yComp, magnitude, angle;

    public Vector2D(double[] components) {
        /*
        * This constructor initializes the vector object with components, and calculates magnitude and angle
        */
       this.xComp = components[0];
       this.yComp = components[1];
       this.magnitude = calcMagnitude();
       this.angle = calcAngle();
    }

    public Vector2D(double magnitude, double angle) {
        /*
         * This constructor initializes the vector object with magnitude and angle, and calculates components
         */
        this.magnitude = magnitude;
        this.angle = angle;
        this.xComp = calcComponents()[0];
        this.yComp = calcComponents()[1];
    }

    private double calcMagnitude() {
        /*
         * This method calculates the magnitude of the vector, using the Pythagorean theorem
         */
        return Math.sqrt(xComp * xComp + yComp * yComp);
    }

    private double calcAngle() {
        /*
         * This method calculates the angle of the vector, using the inverse tangent function
         */
        return Math.atan(yComp/xComp);
    }

    private double[] calcComponents() {
        /*
         * This method calculates the components of the vector, using the unit circle
         */
        return new double[] {magnitude*Math.cos(angle), magnitude*Math.sin(angle)};
    }

    public void normalize() {
        /*
         * This method normalizes the vector
         * To normalize a vector means to make it have a magnitude of 1
         * and the components are adjusted accordingly
         * the angle remains the same
         */
        double originalMagnitude = magnitude;
        xComp = xComp/originalMagnitude;
        yComp = yComp/originalMagnitude;
        magnitude = 1;
    }

    public void setxComp(double xComp) {
        /*
         * This method sets the x component of the vector, and recalculates the magnitude
         */
        this.xComp = xComp;
        magnitude = calcMagnitude();
    }

    public void setyComp(double yComp) {
        /*
         * This method sets the y component of the vector, and recalculates the magnitude
         */ 
        this.yComp = yComp;
        magnitude = calcMagnitude();
    }

    public double getxComp() {
        //return x component
        return xComp;
    }

    public double getyComp() {
        //return y component
        return yComp;
    }

    public double getMagnitude() {
        //return magnitude
        return magnitude;
    }

    public double getAngle() {
        //return angle
        return angle;
    }

    public void changeAngle(double amount) {
        /*
         * This method changes the angle of the vector by a certain amount, and recalculates the components
         */
        angle += amount;
        xComp = calcComponents()[0];
        yComp = calcComponents()[1];
        normalize();
    }

    public Vector2D copy() {
        /*
         * This method returns a copy of the vector
         */
        return new Vector2D(new double[] {xComp, yComp});
    }
}
