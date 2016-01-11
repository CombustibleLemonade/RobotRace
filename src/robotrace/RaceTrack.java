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
                Vector t = getTestTangent(i).normalized();
                float x1 = (float) (getTestPoint(i).x - (2 * laneWidth * t.y));
                float y1 = (float) (getTestPoint(i).y + (2 * laneWidth * t.x));
                float x2 = (float) (getTestPoint(i).x + (2 * laneWidth * t.y));
                float y2 = (float) (getTestPoint(i).y - (2 * laneWidth * t.x));
                
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
                Vector t = getTestTangent(i).normalized();
                float x1 = (float) (getTestPoint(i).x - (2 * laneWidth * t.y));
                float y1 = (float) (getTestPoint(i).y + (2 * laneWidth * t.x));
                
                gl.glNormal3d(-getTestTangent(i).y,getTestTangent(i).x,0);
                gl.glTexCoord2f((float)(200*i),0.0f);
                gl.glVertex3f(x1,y1,1);
                gl.glTexCoord2f((float)(200*i),8f);
                gl.glVertex3f(x1,y1,-1);
            }
            gl.glEnd();
            gl.glBegin(GL2.GL_QUAD_STRIP);
            
            for(double i = 0; i<1.01; i+=0.01){
                Vector t = getTestTangent(i).normalized();
                float x2 = (float) (getTestPoint(i).x + (2 * laneWidth * t.y));
                float y2 = (float) (getTestPoint(i).y - (2 * laneWidth * t.x));
                
                gl.glNormal3d(getTestTangent(i).y,-getTestTangent(i).x,0);
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
            
            int n = 0;
            while(n + 3 <= controlPoints.length){
                Vector[] c = controlPoints;
                
                for(double i = 0; i<1.01; i+=0.01){
                    Vector v = getCubicBezierPoint(i, c[n], c[n + 1], c[n + 2], c[n + 3]);
                    Vector t = getCubicBezierTangent(i, c[n], c[n + 1], c[n + 2], c[n + 3]).normalized();

                    float x1 = (float) (v.x - (2 * laneWidth * t.y));
                    float y1 = (float) (v.y + (2 * laneWidth * t.x));
                    float x2 = (float) (v.x + (2 * laneWidth * t.y));
                    float y2 = (float) (v.y - (2 * laneWidth * t.x));

                    gl.glNormal3f(0, 0, 1);
                    gl.glTexCoord2f(0,0);
                    gl.glVertex3f(x1,y1,1);
                    gl.glTexCoord2f(1,0);
                    gl.glVertex3f(x2,y2,1);
                }
                n += 3;
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
            t *= 0.03;
            Vector tangent = getTestTangent(t).normalized();
            float x = (float) (getTestPoint(t).x + ((-1.5 + lane) * laneWidth * tangent.y));
            float y = (float) (getTestPoint(t).y - ((-1.5 + lane) * laneWidth * tangent.x));
            Vector lanePoint = new Vector(x,y,1);
            return lanePoint;
        } else {
            t *= 3;
            Vector[] c = controlPoints;

            int n = 0;
            while(n + 3 <= controlPoints.length){
                t -= getSegmentLength(c[n], c[n+1], c[n+2], c[n+3]);
                if( t < 0 ){
                    break;
                }
                
                n += 3;
                if (n > controlPoints.length - 3){
                    n = 0;
                }
            }
            
            double index = 1 + t/getSegmentLength(c[n], c[n+1], c[n+2], c[n+3]);
            Vector v = getCubicBezierPoint(index, c[n], c[n+1], c[n+2], c[n+3]);
            Vector tangent = getCubicBezierTangent(index, c[n], c[n+1], c[n+2], c[n+3]);
            
            tangent = tangent.normalized();
            
            v.x += tangent.y * laneWidth * (-1.5 + lane);
            v.y -= tangent.x * laneWidth * (-1.5 + lane);
            
            v = v.add(new Vector(0, 0, 1));
            
            return v;
        }
    }
    
    /**
     * Returns the tangent of a lane at 0 <= t < 1.
     * Use this method to find the orientation of a robot on the track.
     */
    public Vector getLaneTangent(int lane, double t) {
        if (null == controlPoints) {
            return getTestTangent(t*0.03);
        } else {
            t *= 3;
            Vector[] c = controlPoints;

            int n = 0;
            while(n + 3 <= controlPoints.length){
                t -= getSegmentLength(c[n], c[n+1], c[n+2], c[n+3]);
                if( t < 0 ){
                    break;
                }
                
                n += 3;
                if (n > controlPoints.length - 3){
                    n = 0;
                }
            }
            
            double index = 1+t/getSegmentLength(c[n], c[n+1], c[n+2], c[n+3]);
            Vector v = getCubicBezierTangent(index, c[n], c[n+1], c[n+2], c[n+3]);
            return v;
        }
    }

    /**
     * Returns a point on the test track at 0 <= t < 1.
     */
    private Vector getTestPoint(double t) {
        double pointX = 10*cos(PI*2*t);
        double pointY = 14*sin(PI*2*t);
        Vector point = new Vector(pointX,pointY,1);
        return point;
    }

    /**
     * Returns a tangent on the test track at 0 <= t < 1.
     */
    private Vector getTestTangent(double t) {
        double tangentX = -20*PI*sin(PI*2*t);
        double tangentY = 28*PI*cos(PI*2*t);
        Vector tangent = new Vector(tangentX,tangentY,0);
        return tangent;
    }
    
    /**
     * Returns distance of the curve.
     */
    private double getSegmentLength(Vector P0, Vector P1,
                           Vector P2, Vector P3){
        double out = 0;
        for (double i = 0.0; i <= 1.0; i += 0.05){
            Vector t = getCubicBezierTangent(i, P0, P1,
                                                P2, P3);
            out += t.length() * 0.05;
        }
        
        return out;
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
        Vector sum = new Vector(0, 0, 0);
        sum = sum.add(c0);
        sum = sum.add(c1);
        sum = sum.add(c2);
        sum = sum.add(c3);
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
        Vector c0 = P0.scale(-3 * Math.pow(1-t, 2)); //
        Vector c1 = P1.scale(3 * Math.pow(1-t, 2) - 6 * t * (1-t));
        Vector c2 = P2.scale(6 * t * (1-t) - 3 * t * t);
        Vector c3 = P3.scale(3 * t * t);

        // sum is the sum of all coefficients
        Vector sum = Vector.O;
        sum = sum.add(c0);
        sum = sum.add(c1);
        sum = sum.add(c2);
        sum = sum.add(c3);
        return sum;
    }
}
