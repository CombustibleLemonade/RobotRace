package robotrace;
import static java.lang.Math.*;
import javax.media.opengl.GL;
import static javax.media.opengl.GL2.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.*;

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
            /* add control points like:
            new Vector(10, 0, 1), new Vector(10, 5, 1), new Vector(5, 10, 1),
            new Vector(..., ..., ...), ...
            */
        });
        
        // L-track
        raceTracks[2] = new RaceTrack(new Vector[] { 
            /* add control points */
        });
        
        // C-track
        raceTracks[3] = new RaceTrack(new Vector[] { 
            /* add control points */
        });
        
        // Custom track
        raceTracks[4] = new RaceTrack(new Vector[] { 
           /* add control points */
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
        camera.update(gs, robots[0]);
        glu.gluLookAt(camera.eye.x(),    camera.eye.y(),    camera.eye.z(),
                      camera.center.x(), camera.center.y(), camera.center.z(),
                      camera.up.x(),     camera.up.y(),     camera.up.z());
    }
    
    /**
     * Draws the entire scene.
     */
    @Override
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
        
        float[] ambient= { 0.2f, 0.2f, 0.2f, 1.0f};
        float[] diffuse = {1.0f, 1.0f, 1.0f, 1.0f};
        
        
        //Lighting
        gl.glLightfv(GL_LIGHT0, GL_AMBIENT, ambient ,0);                        // Set ambient for light 0
        gl.glLightfv(GL_LIGHT0, GL_POSITION, lightPos ,0);                      // Set position for light 0
        gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuse, 0);                        // Set diffuse for light 0
        gl.glEnable(GL_LIGHTING);                                               // Enable Lighting
        gl.glEnable(GL_LIGHT0);                                                 // Enable Light 0
        gl.glDisable(GL_COLOR_MATERIAL);                                        // Disable Color Material

        
        // Get the position and direction of the first robot.
        robots[0].position = raceTracks[gs.trackNr].getLanePoint(0, 0);
        robots[0].direction = raceTracks[gs.trackNr].getLaneTangent(0, 0);
        
        double t = gs.tAnim * 0.03;
        
        for (int i=0; i < robots.length; i++){
            robots[i].position = raceTracks[gs.trackNr].getLanePoint(i, t);
            robots[i].direction = raceTracks[gs.trackNr].getLaneTangent(i, t);
        }
        
        // Draw the robots.        
        robots[0].draw(gl, glu, glut, gs.showStick, gs.tAnim);
        robots[1].draw(gl, glu, glut, gs.showStick, gs.tAnim);
        robots[2].draw(gl, glu, glut, gs.showStick, gs.tAnim);
        robots[3].draw(gl, glu, glut, gs.showStick, gs.tAnim);
        
        gl.glEnable(GL_COLOR_MATERIAL);
        // Draw the race track.
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
