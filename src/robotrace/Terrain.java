package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Color;
import static java.lang.Math.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_NEAREST;
import static javax.media.opengl.GL.GL_REPEAT;
import static javax.media.opengl.GL.GL_RGBA;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_S;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_T;
import static javax.media.opengl.GL.GL_TRIANGLE_STRIP;
import static javax.media.opengl.GL.GL_UNSIGNED_BYTE;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2GL3.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;
import javax.media.opengl.glu.GLU;

/**
 * Implementation of the terrain.
 */
class Terrain {
    ByteBuffer bb;
    
    /**
     * Can be used to set up a display list.
     */
    public Terrain() {
        Color colors[] = new Color[1024];
        Random r = new Random();
        
        for(int i = 0; i < 1024; i++){
            colors[i] = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat());
        }
        
        bb = ByteBuffer.allocateDirect(colors.length * 4).order(ByteOrder.nativeOrder());

        for (Color color : colors){
            int pixel = color.getRGB();
            bb.put((byte) ((pixel >> 16) & 0xFF)); // Red component
            bb.put((byte) ((pixel >> 8) & 0xFF));  // Green component
            bb.put((byte) (pixel & 0xFF));         // Blue component
            bb.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component
        }
        
        bb.flip();
    }

    /**
     * Draws the terrain.
     */
    public void draw(GL2 gl, GLU glu, GLUT glut) {
        float terrainTexColors[] = {
            210f, 43f, 18f, 1.0f,
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
                
        gl.glEnable(GL_TEXTURE_2D);
        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 32, 32, 0, GL_RGBA, GL_UNSIGNED_BYTE, bb);


        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        int mapSize = 41;
        gl.glPushMatrix();
        for(float x = 1; x < mapSize-1; x+=0.25){
            gl.glBegin(GL_TRIANGLE_STRIP);
            for(float y = 1; y < mapSize-1; y+=0.25){
                gl.glNormal3f(0.0f, 0.0f, 1.0f);
//                gl.glNormal3f((float)Normal(x-21,y-21).x,(float)Normal(x-21,y-21).y,(float)Normal(x-21,y-21).z);
//                gl.glTexCoord1f((heightAt(x-32, y-21)+1)/2);
                gl.glTexCoord2f(0.0f, 0.0f);
                gl.glVertex3f(x-21, y-21, heightAt(x-21, y-21));
//                gl.glNormal3f((float)Normal(x-20,y-21).x,(float)Normal(x-20,y-21).y,(float)Normal(x-20,y-21).z);
//                gl.glTexCoord1f((heightAt(x-20, y-21)+1)/2);
                gl.glTexCoord2f(1.0f, 0.0f);
                gl.glVertex3f(x-20, y-21, heightAt(x-20, y-21));
//                gl.glNormal3f((float)Normal(x-21,y-20).x,(float)Normal(x-21,y-20).y,(float)Normal(x-21,y-20).z);
//                gl.glTexCoord1f((heightAt(x-21, y-20)+1)/2);
                gl.glTexCoord2f(0.0f, 1.0f);
                gl.glVertex3f(x-21, y-20, heightAt(x-21, y-20));
//                gl.glNormal3f((float)Normal(x-20,y-20).x,(float)Normal(x-20,y-20).y,(float)Normal(x-20,y-20).z);
//                gl.glTexCoord1f((heightAt(x-20, y-20)+1)/2);
                gl.glTexCoord2f(1.0f, 1.0f);
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
