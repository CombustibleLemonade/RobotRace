package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import static java.lang.Math.*;
import java.nio.FloatBuffer;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2GL3.*;
import javax.media.opengl.glu.GLU;

/**
 * Implementation of the terrain.
 */
class Terrain {

    /**
     * Can be used to set up a display list.
     */
    public Terrain() {
       
    }

    /**
     * Draws the terrain.
     */
    public void draw(GL2 gl, GLU glu, GLUT glut) {
        float terrainTexColors[] = {
            21f, 43f, 18f, 1.0f,
            21f, 43f, 18f, 1.0f,
            29f, 58f, 24f, 1.0f,
            29f, 58f, 24f, 1.0f,
            39f, 78f, 33f, 1.0f,
            53f, 104f, 45f, 1.0f,
            248f, 240f, 164f, 1.0f,
            218f, 215f, 191f, 1.0f,
            255f, 254f, 254f, 1.0f,
            53f, 98f, 131f, 1.0f,
            53f, 98f, 131f, 1.0f,
            43f, 79f, 105f, 1.0f,
            43f, 79f, 105f, 1.0f,
            43f, 79f, 105f, 1.0f,
            43f, 79f, 105f, 1.0f,
            43f, 79f, 105f, 1.0f
        };
        
        FloatBuffer terrainTex;
        terrainTex = FloatBuffer.wrap(terrainTexColors);
        
        
        
        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1, 16, 0, GL_RGBA, GL_UNSIGNED_BYTE, terrainTex);
        
        gl.glEnable(GL_TEXTURE_2D);
        int mapSize = 41;
        gl.glPushMatrix();
        for(float x = 1; x < mapSize-1; x+=0.25){
            gl.glBegin(GL_TRIANGLE_STRIP);
            for(float y = 1; y < mapSize-1; y+=0.25){
//                gl.glNormal3f((float)Normal(x-21,y-21).x,(float)Normal(x-21,y-21).y,(float)Normal(x-21,y-21).z);
//                gl.glTexCoord1f((heightAt(x-32, y-21)+1)/2);
                gl.glTexCoord2f(0.5f,1f);
                gl.glVertex3f(x-21, y-21, heightAt(x-21, y-21));
//                gl.glNormal3f((float)Normal(x-20,y-21).x,(float)Normal(x-20,y-21).y,(float)Normal(x-20,y-21).z);
//                gl.glTexCoord1f((heightAt(x-20, y-21)+1)/2);
                gl.glTexCoord2f(0.5f,1f);
                gl.glVertex3f(x-20, y-21, heightAt(x-20, y-21));
//                gl.glNormal3f((float)Normal(x-21,y-20).x,(float)Normal(x-21,y-20).y,(float)Normal(x-21,y-20).z);
//                gl.glTexCoord1f((heightAt(x-21, y-20)+1)/2);
                gl.glTexCoord2f(0.5f,1f);
                gl.glVertex3f(x-21, y-20, heightAt(x-21, y-20));
//                gl.glNormal3f((float)Normal(x-20,y-20).x,(float)Normal(x-20,y-20).y,(float)Normal(x-20,y-20).z);
//                gl.glTexCoord1f((heightAt(x-20, y-20)+1)/2);
                gl.glTexCoord2f(0.5f,1f);
                gl.glVertex3f(x-20, y-20, heightAt(x-20, y-20));
            }
            gl.glEnd();
        }
        gl.glPopMatrix();
        gl.glDisable(GL_TEXTURE_2D);
    }

    /**
     * Computes the elevation of the terrain at (x, y).
     */
    public float heightAt(float x, float y) {
        float height =  (float) (0.6 * cos(0.3 * x + 0.2 * y) + 0.4 * cos(x - 0.5 * y));
        return height;
    }
    
/*    private Vector Normal(float x, float y){
        Vector nor1 = new Vector(0,0,0);
        float nor1Length = (float)(sqrt(Math.pow((0.25 * heightAt((float)(x + 0.25), (float)(y)))-(heightAt((float)(x + 0.25), (float)(y + 0.25))*0),2)+Math.pow((heightAt((float)(x+0.25),(float)(y+0.25))*0.25)-(0.25*heightAt((float)(x+0.25),(float)(y))),2)+Math.pow((0.25*0)-(0.25*0.25),2)));
        nor1.x = (0.25 * heightAt((float)(x + 0.25), (float)(y)))-(heightAt((float)(x + 0.25), (float)(y + 0.25))*0)/nor1Length;
        nor1.y = (heightAt((float)(x+0.25),(float)(y+0.25))*0.25)-(0.25*heightAt((float)(x+0.25),(float)(y)))/nor1Length;
        nor1.z = (0.25*0)-(0.25*0.25)/nor1Length;
        Vector nor2 = new Vector(0,0,0);
        nor2.x = (0 * heightAt((float)(x), (float)(y-0.25)))-(heightAt((float)(x + 0.25), (float)(y))*-0.25);
        nor2.y = (heightAt((float)(x+0.25),(float)(y))*0)-(0.25*heightAt((float)(x),(float)(y-0.25)));
        nor2.z = (0.25*-0.25)-(0*0);
        Vector nor3 = new Vector(0,0,0);
        nor3.x = (-0.25 * heightAt((float)(x-0.25), (float)(y-0.25)))-(heightAt((float)(x), (float)(y-0.25))*-0.25);
        nor3.y = (heightAt((float)(x),(float)(y-0.25))*-0.25)-(0*heightAt((float)(x-0.25),(float)(y-0.25)));
        nor3.z = (0*-0.25)-(-0.25*-0.25);
        Vector nor4 = new Vector(0,0,0);
        nor4.x = (-0.25 * heightAt((float)(x-0.25), (float)(y)))-(heightAt((float)(x-0.25), (float)(y-0.25))*0);
        nor4.y = (heightAt((float)(x-0.25),(float)(y-0.25))*-0.25)-(-0.25*heightAt((float)(x-0.25),(float)(y)));
        nor4.z = (-0.25*0)-(-0.25*-0.25);
        Vector nor5 = new Vector(0,0,0);
        nor5.x = (0 * heightAt((float)(x), (float)(y+0.25)))-(heightAt((float)(x-0.25), (float)(y))*0);
        nor5.y = (heightAt((float)(x-0.25),(float)(y))*0)-(-0.25*heightAt((float)(x),(float)(y+0.25)));
        nor5.z = (-0.25*0.25)-(0*0);
        Vector nor6 = new Vector(0,0,0);
        nor6.x = (0.25 * heightAt((float)(x+0.25), (float)(y+0.25)))-(heightAt((float)(x), (float)(y+0.25))*0.25);
        nor6.y = (heightAt((float)(x),(float)(y+0.25))*0.25)-(0*heightAt((float)(x+0.25),(float)(y+0.25)));
        nor6.z = (0*0.25)-(0.25*0.25);
        Normalize(nor1);
        
        Vector normal = new Vector(0,0,0);
        return normal;
    }
*/
}
