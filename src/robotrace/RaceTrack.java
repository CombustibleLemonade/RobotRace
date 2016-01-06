package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import static java.lang.Math.*;

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
        if (null == controlPoints) {
            // draw the test track
            gl.glColor3f(0, 0, 1);
            gl.glBegin(gl.GL_QUAD_STRIP);
            
            for(double i = 0; i<1.01; i+=0.01){
                float x1 = (float) (getPoint(i).x - (2 * laneWidth * getTangent(i).y));
                float y1 = (float) (getPoint(i).y + (2 * laneWidth * getTangent(i).x));
                float x2 = (float) (getPoint(i).x + (2 * laneWidth * getTangent(i).y));
                float y2 = (float) (getPoint(i).y - (2 * laneWidth * getTangent(i).x));
                
                gl.glVertex3f(x1,y1,1);
                gl.glVertex3f(x2,y2,1);
            }
            gl.glColor3f(0, 0, 0);
            gl.glEnd();
            gl.glBegin(gl.GL_QUAD_STRIP);
            
            for(double i = 0; i<1.01; i+=0.01){
                float x1 = (float) (getPoint(i).x - (2 * laneWidth * getTangent(i).y));
                float y1 = (float) (getPoint(i).y + (2 * laneWidth * getTangent(i).x));
                
                gl.glVertex3f(x1,y1,1);
                gl.glVertex3f(x1,y1,-1);
            }
            gl.glEnd();
            gl.glBegin(gl.GL_QUAD_STRIP);
            
            for(double i = 0; i<1.01; i+=0.01){
                float x2 = (float) (getPoint(i).x + (2 * laneWidth * getTangent(i).y));
                float y2 = (float) (getPoint(i).y - (2 * laneWidth * getTangent(i).x));
                
                gl.glVertex3f(x2,y2,1);
                gl.glVertex3f(x2,y2,-1);
            }
            gl.glColor3f(0, 0, 0);
            gl.glEnd();
            
            
        } else {
            // draw the spline track
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
        return Vector.O; // <- code goes here
    }
    
    /**
     * Returns a tangent on a bezier segment with control points
     * P0, P1, P2, P3 at 0 <= t < 1.
     */
    private Vector getCubicBezierTangent(double t, Vector P0, Vector P1,
                                                   Vector P2, Vector P3) {
        return Vector.O; // <- code goes here
    }
}
