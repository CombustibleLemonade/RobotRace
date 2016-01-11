package robotrace;
import static java.lang.Math.*;
import java.util.Random;
import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL.GL_LESS;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL2.*;
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.GL2GL3.GL_FILL;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_COLOR_MATERIAL;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_NORMALIZE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_POSITION;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

/**
 * Handles all of the RobotRace graphics functionality,
 * which should be extended per the assignment.
 * 
 * OpenGL functionality:
 * - Basic commands are called via the gl object;
 * - Utility commands are called via the glu and
 *   glut objects;
 * 
 * GlobalState:
 * The gs object contains the GlobalState as described
 * in the assignment:
 * - The camera viewpoint angles, phi and theta, are
 *   changed interactively by holding the left mouse
 *   button and dragging;
 * - The camera view width, vWidth, is changed
 *   interactively by holding the right mouse button
 *   and dragging upwards or downwards;
 * - The center point can be moved up and down by
 *   pressing the 'q' and 'z' keys, forwards and
 *   backwards with the 'w' and 's' keys, and
 *   left and right with the 'a' and 'd' keys;
 * - Other settings are changed via the menus
 *   at the top of the screen.
 * 
 * Textures:
 * Place your "track.jpg", "brick.jpg", "head.jpg",
 * and "torso.jpg" files in the same folder as this
 * file. These will then be loaded as the texture
 * objects track, bricks, head, and torso respectively.
 * Be aware, these objects are already defined and
 * cannot be used for other purposes. The texture
 * objects can be used as follows:
 * 
 * gl.glColor3f(1f, 1f, 1f);
 * track.bind(gl);
 * gl.glBegin(GL_QUADS);
 * gl.glTexCoord2d(0, 0);
 * gl.glVertex3d(0, 0, 0);
 * gl.glTexCoord2d(1, 0);
 * gl.glVertex3d(1, 0, 0);
 * gl.glTexCoord2d(1, 1);
 * gl.glVertex3d(1, 1, 0);
 * gl.glTexCoord2d(0, 1);
 * gl.glVertex3d(0, 1, 0);
 * gl.glEnd(); 
 * 
 * Note that it is hard or impossible to texture
 * objects drawn with GLUT. Either define the
 * primitives of the object yourself (as seen
 * above) or add additional textured primitives
 * to the GLUT object.
 */
public class RobotRace extends Base {
    
    /** Array of the four robots. */
    private final Robot[] robots;
    
    /** Instance of the camera. */
    private final Camera camera;
    
    /** Instance of the race track. */
    private final RaceTrack[] raceTracks;
    
    /** Instance of the terrain. */
    private final Terrain terrain;
    
    /**
     * Constructs this robot race by initializing robots,
     * camera, track, and terrain.
     */
    public RobotRace() {
        // Create a new array of four robots
        robots = new Robot[4];
        
        // Initialize robot 0
        robots[0] = new Robot(Material.GOLD);
        
        // Initialize robot 1
        robots[1] = new Robot(Material.SILVER);
        
        // Initialize robot 2
        robots[2] = new Robot(Material.WOOD);

        // Initialize robot 3
        robots[3] = new Robot(Material.ORANGE);
        
        // Initialize the camera
        camera = new Camera();
        
        // Initialize the race tracks
        raceTracks = new RaceTrack[5];
        
        // Test track
        raceTracks[0] = new RaceTrack();
        
        // O-track
        raceTracks[1] = new RaceTrack(new Vector[] {
            new Vector(-10, 0, 0),
            new Vector(-10, -10, 0),
            new Vector(10, -10, 0),
            new Vector(10, 0, 0),
            new Vector(10, 10, 0),
            new Vector(-10, 10, 0),
            new Vector(-10, 0, 0)
        });
        
        // L-track
        raceTracks[2] = new RaceTrack(new Vector[] { 
            new Vector(10, -6, 0), // New segment: vertical_left
            new Vector(10, -2, 0),
            new Vector(10, 4, 0),
            new Vector(10, 10, 0), // New segment: bottom_right_outer_corner
            new Vector(10, 12, 0),
            new Vector(9, 13, 0), 
            new Vector(7, 13, 0), // New segment: horizontal_bottom
            new Vector(3, 13.01, 0),
            new Vector(-1, 13, 0), 
            new Vector(-4, 12.98, 0), // New segment: right_end
            new Vector(-8, 12.99, 0), 
            new Vector(-8, 8, 0),
            new Vector(-4, 8, 0), // New segment: horizontal_top
            new Vector(-2, 8.01, 0),
            new Vector(0, 7.99, 0),
            new Vector(2, 8, 0), // New segment: bottom_right_inner_corner
            new Vector(4, 8, 0),
            new Vector(5, 7, 0),
            new Vector(5, 5, 0), // New segment: vertical_right
            new Vector(5, 2, 0), 
            new Vector(5, -3, 0),
            new Vector(5, -6, 0), // New segment: top_end
            new Vector(5, -10, 0),
            new Vector(10, -10, 0),
            new Vector(10, -6, 0)
        });
        
        // C-track
        raceTracks[3] = new RaceTrack(new Vector[] { 
            new Vector(-13, 4, 0),// New segment: outer_c
            new Vector(-13, -16, 0),
            new Vector(13, -16, 0),
            new Vector(13, 4, 0),// New segment: cap
            new Vector(13, 8, 0),
            new Vector(8, 8, 0),
            new Vector(8, 4, 0), // New segment: inner_c
            new Vector(8, -9, 0),
            new Vector(-8, -9, 0),
            new Vector(-8, 4, 0), // New segment: cap
            new Vector(-8, 8, 0),
            new Vector(-13, 8, 0),
            new Vector(-13, 4, 0)
        });
        
        // Custom track
        raceTracks[4] = new RaceTrack(new Vector[] { 
            new Vector(-2.5, -10, 0), // New segment: straight
            new Vector(-2.5, -5, 0),
            new Vector(-2.5, 5, 0),
            new Vector(-2.5, 10, 0), // New segment: cap
            new Vector(-2.5, 14, 0),
            new Vector(2.5, 14, 0),
            new Vector(2.5, 10, 0), // New segment: straight
            new Vector(2.5, 5, 0),
            new Vector(2.5, -5, 0),
            new Vector(2.5, -10, 0), // New segment: cap
            new Vector(2.5, -14, 0),
            new Vector(-2.5, -14, 0),
            new Vector(-2.5, -10, 0),        
        });
        
        // Initialize the terrain
        terrain = new Terrain();
    }
    
    /**
     * Called upon the start of the application.
     * Primarily used to configure OpenGL.
     */
    @Override
    public void initialize() {
        // Enable blending.
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                
        // Enable depth testing.
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);
		
	// Normalize normals.
        gl.glEnable(GL_NORMALIZE);
        
        // Enable textures. 
        gl.glEnable(GL_TEXTURE_2D);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glBindTexture(GL_TEXTURE_2D, 0);
		
	// Try to load four textures, add more if you like.
        track = loadTexture("robotrace/track.jpg");       
        brick = loadTexture("robotrace/brick.jpg");
        head = loadTexture("robotrace/head.jpg");
        torso = loadTexture("robotrace/torso.jpg");
    }
    
    /**
     * Configures the viewing transform.
     */
    @Override
    public void setView() {
        // Select part of window.
        gl.glViewport(0, 0, gs.w, gs.h);
        
        // Set projection matrix.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        // Set the perspective.
        // Modify this to meet the requirements in the assignment.
        double vHeight = gs.vWidth * ((float)gs.h / (float)gs.w ); // Get the viewing height at vDist
        vHeight *= 1.1; // Correction for the top bar
        double fovy = 2.0 * Math.toDegrees(Math.atan(vHeight/gs.vDist)); // Compute the fov angle from that
        
        glu.gluPerspective(fovy, (float)gs.w / (float)gs.h, gs.vDist*0.1, gs.vDist*10.0);

        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();
        
        // Set light position
        
        // Update the view according to the camera mode and robot of interest.
        // For camera modes 1 to 4, determine which robot to focus on.
        camera.raceTrack = raceTracks[gs.trackNr];
        camera.update(gs, robots[0]);
        glu.gluLookAt(camera.eye.x(),    camera.eye.y(),    camera.eye.z(),
                      camera.center.x(), camera.center.y(), camera.center.z(),
                      camera.up.x(),     camera.up.y(),     camera.up.z());
    }
    
    /**
     * Draws the entire scene.
     */
    @Override
    @SuppressWarnings("empty-statement")
    public void drawScene() {
        // Background color.
        gl.glClearColor(1f, 1f, 1f, 0f);
        
        // Clear background.
        gl.glClear(GL_COLOR_BUFFER_BIT);
        
        // Clear depth buffer.
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        
        // Set color to black.
        gl.glColor3f(0f, 0f, 0f);
        
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        
        // Enable Color Material for the AxisFrame
        gl.glEnable(GL_COLOR_MATERIAL);
        
        // Draw the axis frame.
        if (gs.showAxes) {
            drawAxisFrame();
        }
        
        // Get camera position for lightPos
        float x = (float) (cos(gs.theta - ((1/18)* PI))*cos(gs.phi + ((1/18)* PI)));
        float y = (float) (sin(gs.theta - ((1/18)* PI))*cos(gs.phi + ((1/18)* PI)));
        float z = (float) sin(gs.phi + ((1/18)* PI));
        float lightPos[] = { x, y, z, 0.0f };
        
        if(gs.camMode != 0){
            lightPos[0] = 0.1f;
            lightPos[1] = 0.1f;
            lightPos[2] = 1.0f;
            lightPos[3] = 0.0f;
        }
        
        float[] ambient= { 0.2f, 0.2f, 0.2f, 1.0f};
        float[] diffuse = {1.0f, 1.0f, 1.0f, 1.0f};
        
        
        //Lighting
        gl.glLightfv(GL_LIGHT0, GL_AMBIENT, ambient ,0);                        // Set ambient for light 0
        gl.glLightfv(GL_LIGHT0, GL_POSITION, lightPos ,0);                      // Set position for light 0
        gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuse, 0);                        // Set diffuse for light 0
        gl.glEnable(GL_LIGHTING);                                               // Enable Lighting
        gl.glEnable(GL_LIGHT0);                                                 // Enable Light 0
        gl.glDisable(GL_COLOR_MATERIAL);                                        // Disable Color Material

        
        // Get the position and direction of the robots.
        double t = gs.tAnim;
        Random r = new Random();
        for (int i=0; i < robots.length; i++){
            robots[i].distanceTravelled = t + robots[i].deviation;
            // Set the position and direction of the robot
            robots[i].position = raceTracks[gs.trackNr].getLanePoint(i, robots[i].distanceTravelled);
            robots[i].direction = raceTracks[gs.trackNr].getLaneTangent(i, robots[i].distanceTravelled);
            // Draw the robot
            robots[i].draw(gl, glu, glut, gs.showStick, (float) robots[i].distanceTravelled);
            // Add some randomness in the robots movement
            robots[i].ddeviation += (r.nextDouble() - 0.49) * 0.01;
            robots[i].deviation += robots[i].ddeviation;
            
            // Add some damping effect on deviation
            if(robots[i].ddeviation > 0.1){
                robots[i].ddeviation = 0.01;
            } else if (robots[i].ddeviation < 0){
                robots[i].ddeviation = 0.05;
            }
            
            raceTracks[gs.trackNr].robots[i] = robots[i];
        }
        
        Robot slowest = robots[0]; // Slowest robot
        Robot fastest = robots[0]; // Fastest robot
        for (Robot i : robots){
            if (i.deviation < slowest.deviation){
                slowest = i;
            }
            if (i.deviation > fastest.deviation){
                fastest = i;
            }
        }
        
        if (slowest.ddeviation < 0.09){
            slowest.ddeviation += 0.005; // Speed up the slowest
        }
        if (fastest.ddeviation > 0.01){
            fastest.ddeviation -= 0.001;
        }
        
        // Draw the race track.
        gl.glEnable(GL_COLOR_MATERIAL);
        raceTracks[gs.trackNr].draw(gl, glu, glut);
        gl.glDisable(GL_COLOR_MATERIAL);
        
        // Draw the terrain.
        terrain.draw(gl, glu, glut);
    }
    
    /**
     * Draws a single axis
     */
    public void drawAxis(){
        int circleSegments = 40;
        
        gl.glTranslatef(0.0f, 0.0f, 0.8f);
        glut.glutSolidCone(0.1f, 0.3f, circleSegments, 2);
        glut.glutSolidCylinder(0.1f, 0.0f, circleSegments, 1); // Fill the end
        gl.glTranslatef(0.0f, 0.0f, -0.4f);
        gl.glScalef(0.08f, 0.08f, 1.0f);
        glut.glutSolidCube(0.8f);
    }
    
    /**
     * Draws the x-axis (red), y-axis (green), z-axis (blue),
     * and origin (yellow).
     */
    public void drawAxisFrame() {
        if(gs.showAxes){
            gl.glColor3f(0.9f, 0.9f, 0.0f);
            glut.glutSolidSphere(0.15f, 40, 20);
            
            // X-axis
            gl.glColor3f(0.0f, 1.0f, 0.0f);
            gl.glPushMatrix();
            gl.glRotatef(90.0f, -1.0f, 0.0f, 0.0f);
            drawAxis();
            gl.glPopMatrix();
            
            // Y-axis
            gl.glColor3f(1.0f, 0.0f, 0.0f);
            gl.glPushMatrix();
            gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
            drawAxis();
            gl.glPopMatrix();
            
            // Z-axis
            gl.glColor3f(0.0f, 0.0f, 1.0f);
            gl.glPushMatrix();
            drawAxis();
            gl.glPopMatrix();
            
            gl.glColor3f(0.2f, 0.2f, 0.2f);
        }
    }
 
    /**
     * Main program execution body, delegates to an instance of
     * the RobotRace implementation.
     */
    public static void main(String args[]) {
        RobotRace robotRace = new RobotRace();
        robotRace.run();
    }
}
