package robotrace;

import java.util.Random;

/**
 * Implementation of a camera with a position and orientation. 
 */
class Camera {

    /** The position of the camera. */
    public Vector eye = new Vector(3f, 6f, 5f);

    /** The point to which the camera is looking. */
    public Vector center = Vector.O;

    /** The up vector. */
    public Vector up = Vector.Z;
    
    /** The racetrack we're on */
    public RaceTrack raceTrack;

    /**
     * Updates the camera viewpoint and direction based on the
     * selected camera mode.
     */
    public void update(GlobalState gs, Robot focus) {
        Robot robots[] = raceTrack.robots;
        switch (gs.camMode) { 
            // Helicopter mode
            case 1:
                // Get the leading robot
                double maxDTravel = 0.0;
                
                for (Robot i : robots){
                    if (i == null){break;}
                    if (i.distanceTravelled > maxDTravel){
                        focus = i;
                        maxDTravel = i.distanceTravelled;
                    }
                }
                // Set the camera
                setHelicopterMode(gs, focus);
                break;
                
            // Motor cycle mode    
            case 2:
                // Get the leading robot
                maxDTravel = 0.0;
                for (Robot i : robots){
                    if (i.distanceTravelled > maxDTravel){
                        focus = i;
                        maxDTravel = i.distanceTravelled;
                    }
                }
                // Set the camera
                setMotorCycleMode(gs, focus);
                break;
                
            // First person mode    
            case 3:
                // Get the losing robot
                maxDTravel = robots[0].distanceTravelled;
                for (Robot i : robots){
                    if (i.distanceTravelled <= maxDTravel){
                        focus = i;
                        maxDTravel = i.distanceTravelled;
                    }
                }
                
                setFirstPersonMode(gs, focus);
                break;
                
            // Auto mode
            case 4:
                setAutoMode(gs, focus);
                break;
                
            // Default mode    
            default:
                setDefaultMode(gs);
        }
    }

    /**
     * Computes eye, center, and up, based on the camera's default mode.
     */
    private void setDefaultMode(GlobalState gs) {
        center = gs.cnt;
        eye.x = Math.cos(gs.theta)*Math.cos(gs.phi) * gs.vDist + center.x;
        eye.y = Math.sin(gs.theta)*Math.cos(gs.phi) * gs.vDist + center.y;
        eye.z = Math.sin(gs.phi) * gs.vDist + center.z;
    }

    /**
     * Computes eye, center, and up, based on the helicopter mode.
     * The camera should focus on the robot.
     */
    private void setHelicopterMode(GlobalState gs, Robot focus) {
        // eye = focus.position;
        eye = focus.position.add(new Vector(0.1, 0.1, 6));
        center = focus.position;
    }

    /**
     * Computes eye, center, and up, based on the motorcycle mode.
     * The camera should focus on the robot.
     */
    private void setMotorCycleMode(GlobalState gs, Robot focus) {
        // We position the camera behind the focus
        eye = raceTrack.getLanePoint(1, focus.distanceTravelled - 2);
        eye.z += 1.5;
        
        // And look at the leading robot
        center = focus.position;
        center.z += 1.2;
    }

    /**
     * Computes eye, center, and up, based on the first person mode.
     * The camera should view from the perspective of the robot.
     */
    private void setFirstPersonMode(GlobalState gs, Robot focus) {
        // The camera is positioned in the head of the robot
        eye = focus.position;
        eye.z += 1.7;
        
        // And the camera looks in the same direction as the robot
        center = focus.position.add(focus.direction);
        center.z += 1.7;
    }
    
    /**
     * Computes eye, center, and up, based on the auto mode.
     * The above modes are alternated.
     */
    int autoCamMode = 1;
    private void setAutoMode(GlobalState gs, Robot focus) {
        Random r = new Random();
        if(r.nextDouble() < 0.01){
            autoCamMode = r.nextInt(4);
        }
        
        switch (autoCamMode) {    
            // Helicopter mode
            case 1:
                setHelicopterMode(gs, focus);
                break;
                
            // Motor cycle mode    
            case 2:
                setMotorCycleMode(gs, focus);
                break;
                
            // First person mode    
            case 3:
                setFirstPersonMode(gs, focus);
                break;
            
            default:
                setDefaultMode(gs);
        }
    }
}
