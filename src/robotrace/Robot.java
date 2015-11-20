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
    
    float upperLegLength;
    float lowerLegLength;
    
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
    public void drawLeg(GL2 gl, GLU glu, GLUT glut, float tAnim){
        Vector hipPos;
        hipPos = new Vector(0.0, 0.0, 1.0);
        
        gl.glPushMatrix();
        
        gl.glTranslatef((float)hipPos.x, (float)hipPos.y, (float)hipPos.z);
        glut.glutSolidCube(1.0f);
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
        
        drawLeg(gl, glu, glut, tAnim);
        
        gl.glPopMatrix();
    }
    
    
    /**
     * Draws this robot (as a {@code stickfigure} if specified).
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim) {
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
        
        IKhelper test = new IKhelper(1.0f, 2.0f, new Vector2(0.1f, 0.0f));
        test.compute();
        
        gl.glPopMatrix();
    }
}


class IKhelper{
    public double length1;
    public double length2;
    public Vector2 target;
    
    public Vector2 J1 = new Vector2(); // Joint 1
    
    public double rot1;
    public double rot2;
    
    public IKhelper(float length1_arg, float length2_arg, Vector2 target_arg){
        length1 = length1_arg;
        length2 = length2_arg;
        target = target_arg;
    }
    
    public void compute(){
        /**
         * See: http://mathworld.wolfram.com/Circle-CircleIntersection.html
         * J1 is the intersection of two circles a and b
         * A has radius length1, and is centered on the origin
         * B has radius length2, and is centered on target
         * Applying trigonometry on these constraints gives:
         */
        double d = target.distance();
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
    }
}

class Vector2{
    public double x;
    public double y;
    
    public Vector2(float x_arg, float y_arg){
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