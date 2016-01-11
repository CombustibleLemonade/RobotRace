package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import static javax.media.opengl.GL.*;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2GL3.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.*;
import javax.media.opengl.glu.GLU;
import static robotrace.Base.head;
import static robotrace.Base.torso;

/**
* Represents a Robot, to be implemented according to the Assignments.
*/
class Robot {
    /** The position of the robot. */
    public Vector position = new Vector(0, 0, 0);
    
    /** The direction in which the robot is running. */
    public Vector direction = new Vector(1, 0, 0);
    
    /** Are we a stick figure? */
    public boolean isStickFigure = false;
    
    /** How far we've traversed the track so far */
    public double distanceTravelled = 0.0;
    
    /** How much we deviate from the standard distance traveled. */
    public double deviation = 0.0;
    
    /** How much we deviate from the standard distance traveled per second */
    public double ddeviation = 0.0;
    
    /** The material from which this robot is built. */
    private final Material material;
    
    double armXOffset = 0.44;
    double legXOffset = 0.15;
    
    double upperLegLength = 0.65;
    double lowerLegLength = 0.7;
    
    double legThickness = 0.27;
    double stickThickness = 0.05;
    double stickJointSize = 0.1;
    
    /**
     * Converts a position into an angle and a distance
     * @param pos target position
     * @return angle (x) and distance(y)
     */
    public static Vector2 posToAngleDist(Vector2 pos){
        double distance = Math.sqrt(pos.x * pos.x + pos.y * pos.y);
        double angle;
        angle = Math.toDegrees(Math.acos(pos.y / distance));
        
        if (pos.x < 0){
            angle *= -1;
        }

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
        gl.glTranslated(armXOffset, 0.0, 0.0); // Translate them outside of the body
        gl.glRotatef(-10, 0.0f, 1.0f, 0.0f); // Make arms point slightly outwards
        if (isStickFigure){
            glut.glutSolidSphere(stickJointSize, 10, 10);
            gl.glRotated(180.0, 1.0, 0.0, 0.0);
            gl.glScaled(stickThickness, stickThickness, 0.7);
            glut.glutSolidCylinder(1.0, 1.0, 10, 10);
        } else {
            gl.glTranslatef(0.0f, 0.0f, -0.3f); // Go down a bit
            gl.glScalef(0.2f, 0.2f, 0.7f); // Scale the cubes to become long
            glut.glutSolidCube(1.0f);
        }
        gl.glPopMatrix();
    }
    
    /**
     * draws a leg
     * @param tAnim animation position 
     */
    public void drawLeg(GL2 gl, GLU glu, GLUT glut, Vector2 target){
        gl.glPushMatrix();
        
        if (isStickFigure){
            glut.glutSolidSphere(stickJointSize, 20, 20);
        }
        
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
        if (isStickFigure){
            gl.glTranslated(0, 0, -upperLegLength/2.0);
            gl.glScaled(stickThickness, stickThickness, upperLegLength);
            glut.glutSolidCylinder(1, 1, 10, 10);
        } else {
            gl.glScaled(legThickness, legThickness, upperLegLength);
            glut.glutSolidCube(1.0f);
        }
        gl.glPopMatrix();
        gl.glTranslated(0, 0, -upperLegLength/2.0);
        
        if (isStickFigure){
            glut.glutSolidSphere(stickJointSize, 20, 20);
        }
        
        gl.glRotated(IK.rot2, 1.0, 0.0, 0.0);
        
        /**
         * Draw upper leg
         */
        gl.glTranslated(0, 0, -lowerLegLength/2.0);
        gl.glPushMatrix();
        if (isStickFigure){
            gl.glTranslated(0, 0, -lowerLegLength/2.0);
            gl.glScaled(stickThickness, stickThickness, lowerLegLength);
            glut.glutSolidCylinder(1, 1, 10, 10);
        } else {
            gl.glScaled(legThickness, legThickness, lowerLegLength);
            glut.glutSolidCube(1.0f);
        }
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
        double speed = 5.0;
                
        gl.glPushMatrix();
        gl.glTranslated(legXOffset, 0, 1.0);
        drawLeg(gl, glu, glut, new Vector2(0.5 * Math.sin(tAnim * speed), -1));
        
        if (isStickFigure){
            gl.glRotated(-90.0, 0.0, 1.0, 0.0);
            gl.glScaled(stickThickness, stickThickness, legXOffset*2);
            glut.glutSolidCylinder(1.0, 1.0, 10, 10);
        }
        
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        gl.glTranslated(-legXOffset, 0, 1.0);
        drawLeg(gl, glu, glut, new Vector2(0.5 * Math.sin(tAnim * speed + Math.PI), -1));
        gl.glPopMatrix();
    }
    
    
    /**
     * Draws this robot (as a {@code stickfigure} if specified).
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim) {
        isStickFigure = stickFigure;
        
        gl.glMaterialfv(GL_FRONT, GL_DIFFUSE, material.diffuse, 0);
        gl.glMaterialfv(GL_FRONT, GL_SPECULAR, material.specular, 0);
        gl.glMaterialf(GL_FRONT, GL_SHININESS, material.shininess);
        
        gl.glPushMatrix();
        
        // Translate to the correct position
        gl.glTranslated(position.x, position.y, position.z);
        // Rotate to face track tangent
        double rotation = - 180 * Math.atan(direction.x / direction.y) / Math.PI + Math.signum(direction.y) * 90 - 90;
        gl.glRotated(rotation, 0.0, 0.0, 1.0);
        
        // Head
        gl.glPushMatrix();
        
        gl.glTranslatef(0.0f, 0.0f, 1.87f);
        gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);

        glut.glutSolidTeapot(0.2);
        gl.glEnable(GL_TEXTURE_2D);
        head.bind(gl);
        head.setTexParameteri(gl, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        head.setTexParameteri(gl, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Eyes
        gl.glBegin(GL_QUADS);
        gl.glNormal3f(1,0,0);
        gl.glTexCoord2f(0,0);
        gl.glVertex3f(0.18f,0.05f,0.1f);
        gl.glTexCoord2f(0,1);
        gl.glVertex3f(0.18f,0.0f,0.1f);
        gl.glTexCoord2f(1,1);
        gl.glVertex3f(0.18f,0.0f,0.05f);
        gl.glTexCoord2f(1,0);
        gl.glVertex3f(0.18f,0.05f,0.05f);
        gl.glEnd();
        
        gl.glBegin(GL_QUADS);
        gl.glNormal3f(1,0,0);
        gl.glTexCoord2f(0,0);
        gl.glVertex3f(0.18f,0.05f,-0.1f);
        gl.glTexCoord2f(0,1);
        gl.glVertex3f(0.18f,0.0f,-0.1f);
        gl.glTexCoord2f(1,1);
        gl.glVertex3f(0.18f,0.0f,-0.05f);
        gl.glTexCoord2f(1,0);
        gl.glVertex3f(0.18f,0.05f,-0.05f);
        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL_TEXTURE_2D);
        
        //Torso
        gl.glPushMatrix();
        
        if (isStickFigure){
            gl.glTranslated(0.0, 0.0, 1.0);
            gl.glScaled(stickThickness, stickThickness, 1.0);
            glut.glutSolidCylinder(1.0, 1.0, 10, 10);
        } else {
            gl.glTranslatef(0.0f, 0.0f, 1.3f);
            gl.glScalef(0.616f, 0.426f, 0.8f);
            glut.glutSolidCube(1.0f);
            gl.glEnable(GL_TEXTURE_2D);
            torso.bind(gl);
            torso.setTexParameteri(gl, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            torso.setTexParameteri(gl, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            
            // Cape
            gl.glBegin(GL_QUADS);
            gl.glNormal3f(0, 0.27f, 0.57f);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.3f,-0.55f,0.55f);
            gl.glTexCoord2f(0,1);
            gl.glVertex3f(-1f,-1.5f,-1f);
            gl.glTexCoord2f(1,1);
            gl.glVertex3f(1f,-1.5f,-1f);
            gl.glTexCoord2f(1,0);
            gl.glVertex3f(0.3f,-0.55f,0.55f);
            gl.glEnd();
            gl.glDisable(GL_TEXTURE_2D);
        }
        gl.glPopMatrix();
        
        // Arms
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, 1.6f);
        drawArm(gl, glu, glut, tAnim);
        gl.glScalef(-1.0f, 1.0f, 1.0f);
        drawArm(gl, glu, glut, tAnim);
        if (isStickFigure){
            gl.glTranslated(armXOffset, 0.0, 0.0);
            gl.glRotated(-90.0, 0.0, 1.0, 0.0);
            gl.glScaled(stickThickness, stickThickness, armXOffset*2);
            glut.glutSolidCylinder(1.0, 1.0, 10, 10);
        }
        gl.glPopMatrix();
        
        // Legs
        gl.glPushMatrix();
        drawLegs(gl, glu, glut, tAnim);
        gl.glPopMatrix();
                
        gl.glPopMatrix();
        
    }
}

/**
 * Inverse kinematics is used when a system of joints and rods are required
 * to be placed at a certain target. Here we use two joints, the hip joint and
 * the knee joint.
 * @author s145358
 */
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
            d = length1 + length2;
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