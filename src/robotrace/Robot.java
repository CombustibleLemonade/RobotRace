package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
* Represents a Robot, to be implemented according to the Assignments.
*/
class Robot {
    
    /** The position of the robot. */
    public Vector position = new Vector(0, 0, 0);
    
    /** The direction in which the robot is running. */
    public Vector direction = new Vector(1, 0, 0);

    /** The material from which this robot is built. */
    private final Material material;
    
    double upperLegLength = 0.8;
    double lowerLegLength = 0.8;
    
    /**
     * Converts a position into an angle and a distance
     * @param pos target position
     * @return angle (x) and distance(y)
     */
    public static Vector2 posToAngleDist(Vector2 pos){
        double distance = Math.sqrt(pos.x * pos.x + pos.y * pos.y);
        double angle;
        angle = Math.toDegrees(Math.acos(pos.y / distance));

        return new Vector2(angle, distance);
    }
    
    /**
     * Constructs the robot with initial parameters.
     */
    public Robot(Material material
        /* add other parameters that characterize this robot */) {
        this.material = material;

        // code goes here ...
    }
        
    /**
     * draws an arm
     * @param tAnim animation position 
     */
    public void drawArm(GL2 gl, GLU glu, GLUT glut, float tAnim){
        gl.glPushMatrix();
        gl.glTranslatef(0.44f, 0.0f, 0.0f); // Translate them outside of the body
        gl.glRotatef(-10, 0.0f, 1.0f, 0.0f); // Make arms point slightly outwards
        gl.glTranslatef(0.0f, 0.0f, -0.3f); // Go down a bit
        gl.glScalef(0.2f, 0.2f, 0.7f); // Scale the cubes to become long
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();
    }
    
    /**
     * draws a leg
     * @param tAnim animation position 
     */
    public void drawLeg(GL2 gl, GLU glu, GLUT glut, Vector2 target){
        gl.glPushMatrix();
        Vector2 targetAD = posToAngleDist(target);
        IKhelper IK;
        IK = new IKhelper(upperLegLength, lowerLegLength, targetAD.y);
        IK.compute();
        
        gl.glRotated(180-targetAD.x, 1.0, 0.0, 0.0);
        gl.glRotated(IK.rot1, 1.0, 0.0, 0.0);
        
        /**
         * Draw upper leg
         */
        gl.glTranslated(0, 0, -upperLegLength/2.0);
        gl.glPushMatrix();
        gl.glScaled(0.27, 0.27, upperLegLength);
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();
        gl.glTranslated(0, 0, -upperLegLength/2.0);
        
        gl.glRotated(IK.rot2, 1.0, 0.0, 0.0);
        System.out.println(IK.rot2);
        
        /**
         * Draw upper leg
         */
        gl.glTranslated(0, 0, -lowerLegLength/2.0);
        gl.glPushMatrix();
        gl.glScaled(0.27, 0.27, lowerLegLength);
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();
        gl.glTranslated(0, 0, -lowerLegLength/2.0);
        
        gl.glPopMatrix();
    }
    
    /**
     * Draws both legs
     * @param gl
     * @param glu
     * @param glut
     * @param tAnim 
     */
    public void drawLegs(GL2 gl, GLU glu, GLUT glut, float tAnim){
        gl.glPushMatrix();
        
        gl.glTranslated(0.15, 0, 0.9);
        drawLeg(gl, glu, glut, new Vector2(1, -1));
        
        gl.glPopMatrix();
    }
    
    
    /**
     * Draws this robot (as a {@code stickfigure} if specified).
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim) {
        posToAngleDist(new Vector2(3, 3));
        
        gl.glPushMatrix();
        
        // Head
        gl.glPushMatrix();
        gl.glColor3f(0.6f, 0.6f, 0.6f);
        
        gl.glTranslatef(0.0f, 0.0f, 1.87f);
        gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);

        glut.glutSolidTeapot(0.2);
        gl.glPopMatrix();
        
        //Torso
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, 1.3f);
        gl.glScalef(0.616f, 0.426f, 0.8f);
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();
        
        // Arms
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, 1.6f);
        drawArm(gl, glu, glut, tAnim);
        gl.glScalef(-1.0f, 1.0f, 1.0f);
        drawArm(gl, glu, glut, tAnim);
        gl.glPopMatrix();
        
        // Legs
        gl.glPushMatrix();
        drawLegs(gl, glu, glut, tAnim);
        gl.glPopMatrix();
                
        gl.glPopMatrix();
        
    }
}


class IKhelper{
    public double length1;
    public double length2;
    public double distance;
    
    public double rot1; // Rotation of the "hip" joint
    public double rot2; // Rotation of the "knee" joint
    
    public IKhelper(float length1_arg, float length2_arg, float distance_arg){
        length1 = length1_arg;
        length2 = length2_arg;
        distance = distance_arg;
    }
    
    public IKhelper(double length1_arg, double length2_arg, double distance_arg){
        length1 = length1_arg;
        length2 = length2_arg;
        distance = distance_arg;
    }
    
    public void compute(){
        /**
         * See: http://mathworld.wolfram.com/Circle-CircleIntersection.html
         * J1 is the intersection of two circles a and b
         * A has radius length1, and is centered on the origin
         * B has radius length2, and is centered on target
         * Applying trigonometry on these constraints gives:
         */
        double d = distance;
        double x = (d*d-length2*length2+length1*length1)/(2.0*d);
        
        /**
         * Circle obeys y=sqrt(length1^2-(x)^2)
         * Therefore we can compute a by:
         */
        double y = 0;
        double ySquared = length1*length1-x*x;
        if (ySquared >= 0){
            y = Math.sqrt(length1*length1-x*x);
        } else {
            x = length1;
            d = length2;
        }
        
        /**
         * Here we set the rotations
         */
        rot1 = Math.toDegrees(Math.acos(x/length1));
        rot2 = - rot1 - Math.toDegrees(Math.acos((d-x)/length2));
    }
}

class Vector2{
    public double x;
    public double y;
    
    public Vector2(float x_arg, float y_arg){
        x = x_arg;
        y = y_arg;
    }
    
    public Vector2(double x_arg, double y_arg){
        x = x_arg;
        y = y_arg;
    }
    
    public Vector2(){
        x = 0;
        y = 0;
    }
    
    public double distance(){
        return Math.sqrt(x*x + y*y);
    }
}