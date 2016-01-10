package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import static java.lang.Math.*;
import static javax.media.opengl.GL.*;
import static robotrace.Base.brick;
import static robotrace.Base.track;

/**
 * Implementation of a race track that is made from Bezier segments.
 */
class RaceTrack {
    
    /** The width of one lane. The total width of the track is 4 * laneWidth. */
    private final static float laneWidth = 1.22f;

    /** Array with 3N control points, where N is the number of segments. */
    private Vector[] controlPoints = null;
    
    /**
     * Constructor for the default track.
     */
    public RaceTrack() {
    }
    
    /**
     * Constructor for a spline track.
     */
    public RaceTrack(Vector[] controlPoints) {
        this.controlPoints = controlPoints;
    }

    /**
     * Draws this track, based on the control points.
     */
    public void draw(GL2 gl, GLU glu, GLUT glut) {
        gl.glEnable(GL_TEXTURE_2D);
        gl.glColor3d(1.0, 1.0, 1.0);
        track.bind(gl);
        
        track.setTexParameteri(gl, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        track.setTexParameteri(gl, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        track.setTexParameteri(gl, GL_TEXTURE_WRAP_S, GL_REPEAT);
        track.setTexParameteri(gl, GL_TEXTURE_WRAP_T, GL_REPEAT);
            
        if (null == controlPoints) {
            // draw the test track            
            gl.glBegin(GL2.GL_QUAD_STRIP);
            
            for(double i = 0; i<1.01; i+=0.01){
                float x1 = (float) (getPoint(i).x - (2 * laneWidth * getTangent(i).y));
                float y1 = (float) (getPoint(i).y + (2 * laneWidth * getTangent(i).x));
                float x2 = (float) (getPoint(i).x + (2 * laneWidth * getTangent(i).y));
                float y2 = (float) (getPoint(i).y - (2 * laneWidth * getTangent(i).x));
                
                gl.glNormal3f(0, 0, 1);
                gl.glTexCoord2f(0,0);
                gl.glVertex3f(x1,y1,1);
                gl.glTexCoord2f(1,0);
                gl.glVertex3f(x2,y2,1);
            }
            gl.glColor3f(0, 0, 0);
            gl.glEnd();
            brick.bind(gl);
            brick.setTexParameteri(gl, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            brick.setTexParameteri(gl, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            brick.setTexParameteri(gl, GL_TEXTURE_WRAP_S, GL_REPEAT);
            brick.setTexParameteri(gl, GL_TEXTURE_WRAP_T, GL_REPEAT);
        
            gl.glBegin(GL2.GL_QUAD_STRIP);
            
            for(double i = 0; i<1.01; i+=0.01){
                float x1 = (float) (getPoint(i).x - (2 * laneWidth * getTangent(i).y));
                float y1 = (float) (getPoint(i).y + (2 * laneWidth * getTangent(i).x));
                
                gl.glNormal3d(-getTangent(i).y,getTangent(i).x,0);
                gl.glTexCoord2f((float)(200*i),0.0f);
                gl.glVertex3f(x1,y1,1);
                gl.glTexCoord2f((float)(200*i),8f);
                gl.glVertex3f(x1,y1,-1);
            }
            gl.glEnd();
            gl.glBegin(GL2.GL_QUAD_STRIP);
            
            for(double i = 0; i<1.01; i+=0.01){
                float x2 = (float) (getPoint(i).x + (2 * laneWidth * getTangent(i).y));
                float y2 = (float) (getPoint(i).y - (2 * laneWidth * getTangent(i).x));
                
                gl.glNormal3d(getTangent(i).y,-getTangent(i).x,0);
                gl.glTexCoord2f((float)(200*i),0.0f);
                gl.glVertex3f(x2,y2,1);
                gl.glTexCoord2f((float)(200*i),8f);
                gl.glVertex3f(x2,y2,-1);
            }
            gl.glColor3f(0, 0, 0);
            gl.glEnd();            
        } else {
            // draw the spline track
            gl.glBegin(GL2.GL_QUAD_STRIP);
            
            for(double i = 0; i<1.01; i+=0.05){
                float x1 = (float) (getPoint(i).x - (2 * laneWidth * getTangent(i).y));
                float y1 = (float) (getPoint(i).y + (2 * laneWidth * getTangent(i).x));
                float x2 = (float) (getPoint(i).x + (2 * laneWidth * getTangent(i).y));
                float y2 = (float) (getPoint(i).y - (2 * laneWidth * getTangent(i).x));
                
                gl.glNormal3f(0, 0, 1);
                gl.glTexCoord2f(0,0);
                gl.glVertex3f(x1,y1,1);
                gl.glTexCoord2f(1,0);
                gl.glVertex3f(x2,y2,1);
            }
            gl.glColor3f(0, 0, 0);
            gl.glEnd();
        }
    }
    
    /**
     * Returns the center of a lane at 0 <= t < 1.
     * Use this method to find the position of a robot on the track.
     */
    public Vector getLanePoint(int lane, double t) {
        if (null == controlPoints) {
            float x = (float) (getPoint(t).x + ((-1.5 + lane) * laneWidth * getTangent(t).y));
            float y = (float) (getPoint(t).y - ((-1.5 + lane) * laneWidth * getTangent(t).x));
            Vector lanePoint = new Vector(x,y,1);
            return lanePoint;
        } else {
            return Vector.O; // <- code goes here
        }
    }
    
    /**
     * Returns the tangent of a lane at 0 <= t < 1.
     * Use this method to find the orientation of a robot on the track.
     */
    public Vector getLaneTangent(int lane, double t) {
        if (null == controlPoints) {
            return getTangent(t);
        } else {
            return Vector.O; // <- code goes here
        }
    }

    /**
     * Returns a point on the test track at 0 <= t < 1.
     */
    private Vector getPoint(double t) {
        double pointX = 10*cos(PI*2*t);
        double pointY = 14*sin(PI*2*t);
        Vector point = new Vector(pointX,pointY,1);
        return point;
    }

    /**
     * Returns a tangent on the test track at 0 <= t < 1.
     */
    private Vector getTangent(double t) {
        double tangentX = -20*PI*sin(PI*2*t)/sqrt(pow(-20*PI*sin(PI*2*t),2)+pow(28*PI*cos(PI*2*t),2));
        double tangentY = 28*PI*cos(PI*2*t)/sqrt(pow(-20*PI*sin(PI*2*t),2)+pow(28*PI*cos(PI*2*t),2));
        Vector tangent = new Vector(tangentX,tangentY,0);
        return tangent;
    }
    
    /**
     * Returns a point on a bezier segment with control points
     * P0, P1, P2, P3 at 0 <= t < 1.
     */
    private Vector getCubicBezierPoint(double t, Vector P0, Vector P1,
                                                 Vector P2, Vector P3) {
        // C0-3 are the coefficients
        Vector c0 = P0.scale(Math.pow(1-t, 3));
        Vector c1 = P1.scale(3 * t * Math.pow(1-t, 2));
        Vector c2 = P2.scale(3 * t * t * (1-t));
        Vector c3 = P3.scale(t * t * t);

        // sum is the sum of all coefficients
        Vector sum = Vector.O;
        sum.add(c0);
        sum.add(c1);
        sum.add(c2);
        sum.add(c3);
        return sum;
    }
    
    /**
     * Returns a tangent on a bezier segment with control points
     * P0, P1, P2, P3 at 0 <= t < 1.
     */
    private Vector getCubicBezierTangent(double t, Vector P0, Vector P1,
                                                   Vector P2, Vector P3) {
        // This basically is the gradient of the getCubicBezierPoint method
        // C0-3 are the coefficients, and derivatives of getCubicBezierPoint coefficients
        Vector c0 = P0.scale(-Math.pow(1-t, 2)); //
        Vector c1 = P1.scale(3 * Math.pow(1-t, 2) - 3 * t * (1-t));
        Vector c2 = P2.scale(6 * t * (1-t) - 3 * t * t);
        Vector c3 = P3.scale(3 * t * t);

        // sum is the sum of all coefficients
        Vector sum = Vector.O;
        sum.add(c0);
        sum.add(c1);
        sum.add(c2);
        sum.add(c3);
        return sum;    
    }
}
