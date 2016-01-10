package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.*;
import static java.lang.Math.*;
import java.nio.*;
import java.util.*;
import static javax.media.opengl.GL.*;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2GL3.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_COLOR_MATERIAL;
import javax.media.opengl.glu.GLU;

/**
 * Implementation of the terrain.
 */
class Terrain {
    
    Random r = new Random();
    ByteBuffer bb;
    int gensize = (int)(pow(2,8) + 1);
    private final int width = 163;
    private final int height = 163;
    private final float variance = 2;
    float[][] map = new float[gensize][gensize];
    float[][] terrainHeight = generate();
    
    /**
     * Can be used to set up a display list.
     */
    public Terrain() {
        Color colors[] = new Color[16];
        int terrainTexColors[] = {
            43, 79, 105,
            43, 79, 105,
            43, 79, 105,
            43, 79, 105,
            43, 79, 105,
            53, 98, 131,
            53, 98, 131,
            255, 254, 254,
            248, 240, 164,
            218, 215, 191,
            53, 104, 45,
            39, 78, 33,
            29, 58, 24,
            29, 58, 24,
            21, 43, 18,
            21, 43, 18,
        };
        
        for(int i = 0; i < 16; i++){
            colors[i] = new Color(terrainTexColors[(3*i)], terrainTexColors[(3*i)+1], terrainTexColors[(3*i)+2]);
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
        gl.glDisable(GL_COLOR_MATERIAL);
        gl.glDisable(GL_TEXTURE_2D);
        gl.glEnable(GL_TEXTURE_1D);
        gl.glTexImage1D(GL_TEXTURE_1D, 0, GL_RGBA, 16, 0, GL_RGBA, GL_UNSIGNED_BYTE, bb);


        gl.glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        gl.glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        int mapSize = 41;
        gl.glPushMatrix();
        gl.glBegin(GL_TRIANGLES);
        for(float x = 0; x < mapSize-1.25; x+=0.25){
            for(float y = 0; y < mapSize-1; y+=0.25){
                gl.glNormal3f((float) (-(heightAt(x+0.25f,y)-heightAt(x,y))*0.25), (float)(-(heightAt(x,y+0.25f)-heightAt(x,y))*0.25), 0.25f*0.25f);
                
                gl.glTexCoord1f((heightAt(x, y)+1)/2);
                gl.glVertex3f(x-21, y-21, heightAt(x, y));
                
                gl.glTexCoord1f((heightAt(x+0.25f, y)+1)/2);
                gl.glVertex3f(x-20.75f, y-21, heightAt(x+0.25f, y));
                
                gl.glTexCoord1f((heightAt(x, y+0.25f)+1)/2);
                gl.glVertex3f(x-21, y-20.75f, heightAt(x, y+0.25f));
                
                gl.glNormal3f((float) (0.25f*(heightAt(x,y+0.25f)-heightAt(x+0.25f,y+0.25f))),(float)(-(heightAt(x+0.25f,y)-heightAt(x+0.25f,y+0.25f))*-0.25f),-0.25f*-0.25f);
                
                gl.glTexCoord1f((heightAt(x+0.25f, y+0.25f)+1)/2);
                gl.glVertex3f(x-20.75f, y-20.75f, heightAt(x+0.25f, y+0.25f));

                gl.glTexCoord1f((heightAt(x+0.25f, y)+1)/2);
                gl.glVertex3f(x-20.75f, y-21, heightAt(x+0.25f, y));

                gl.glTexCoord1f((heightAt(x, y+0.25f)+1)/2);
                gl.glVertex3f(x-21, y-20.75f, heightAt(x, y+0.25f));
            }
        }
        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL_TEXTURE_1D);
        gl.glEnable(GL_COLOR_MATERIAL);
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4f(166,166,166,0.2f);
        gl.glPushMatrix();
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-21,-21,0);
        gl.glVertex3f(-21,21,0);
        gl.glVertex3f(21,21,0);
        gl.glVertex3f(21,-21,0);
        gl.glEnd();
        gl.glPopMatrix();
        gl.glDisable(GL_BLEND);
        
        gl.glDisable(GL_COLOR_MATERIAL);
    }

    /**
     * Computes the elevation of the terrain at (x, y).
     */
    public float heightAt(float x, float y) {
        
        float terHeight =  (float)(((terrainHeight[(int)(x*4)][(int)(y*4)]))-1);
        return terHeight;
    }   

    public float[][] generate() {
        // Place initial seeds for corners
        map[0][0] = r.nextFloat()*2;
        map[0][map.length - 1] = r.nextFloat()*2;
        map[map.length - 1][0] = r.nextFloat()*2;
        map[map.length - 1][map.length - 1] = r.nextFloat()*2;
        map = generate(map);
        if(width < gensize || height < gensize){
            float[][] temp = new float[width][height];
            for(int i = 0; i < temp.length; i++){
                temp[i] = Arrays.copyOf(map[i], temp[i].length);
            }
            map = temp;
        }
        return map;
    }
        
    public float[][] generate(float[][] map){
        int step = map.length - 1;
        float v = variance;
        float var = 0;
        while(step > 1){
            // SQUARE STEP
            for(int i = 0; i < map.length - 1; i += step){
                for(int j = 0; j < map[i].length - 1; j += step){
                    float average = (map[i][j] + map[i + step][j] + map[i][j + step] + map[i+step][j+step])/4;
                    if(map[i + step/2][j + step/2] == 0) // check if not pre-seeded
                        var = randVariance(v);
                        if(average + var >= 2){
                            map[i+step/2][j+step/2] = 1.99f;
                        }
                        else if(average + var <= 0){
                            map[i + step/2][j + step/2] = 0.01f;
                        }
                        else{
                            map[i + step/2][j + step/2] = average + var;
                        }
                }
            }
            // DIAMOND STEP
            for(int i = 0; i < map.length - 1; i += step){
                for(int j = 0; j < map[i].length - 1; j += step){
                    if(map[i + step/2][j] == 0){ // check if not pre-seeded
                        var = randVariance(v);
                        if(averageDiamond(map, i + step/2, j, step) + var >=2){
                            map[i + step/2][j]=1.99f;
                        }
                        else if(averageDiamond(map, i + step/2, j, step) + var <0){
                            map[i + step/2][j]=0.01f;
                        }
                        else{
                            map[i + step/2][j] = averageDiamond(map, i + step/2, j, step) + var;
                        }
                    }
                    if(map[i][j + step/2] == 0){
                        var = randVariance(v);
                        if(averageDiamond(map, i, j + step/2, step) + var >=2){
                            map[i][j + step/2]=1.99f;
                        }
                        else if(averageDiamond(map, i, j + step/2, step) + var <0){
                            map[i][j + step/2]=0.01f;
                        }
                        else{
                            map[i][j + step/2] = averageDiamond(map, i, j + step/2, step) + var;
                        }
                    }
                    if(map[i + step][j + step/2] == 0){
                        var = randVariance(v);
                        if(averageDiamond(map, i, j + step/2, step) + var >=2){
                            map[i][j + step/2]=1.99f;
                        }
                        else if(averageDiamond(map, i, j + step/2, step) + var <0){
                            map[i][j + step/2]=0.01f;
                        }
                        else{
                            map[i][j + step/2] = averageDiamond(map, i + step, j + step/2, step) + var;
                        }
                    }
                    if(map[i + step/2][j + step] == 0){
                        var = randVariance(v);
                        if(averageDiamond(map, i + step/2, j + step, step) + var >=2){
                            map[i + step/2][j + step]=1.99f;
                        }
                        else if(averageDiamond(map, i + step/2, j + step, step) + var <0){
                            map[i + step/2][j + step]=0.01f;
                        }
                        else{
                            map[i + step/2][j + step] = averageDiamond(map, i + step/2, j + step, step) + var;
                        }
                    }
                }       
            }
            v /=2;
            step /= 2;
        }
        return map;
    }

    private float averageDiamond(float[][] map, int x, int y, int step){
        int count = 0;
        float average = 0;
        if(x - step/2 >= 0){
            count++;
            average += map[x - step/2][y];
        }
        if(x + step/2 < map.length){
            count++;
            average += map[x + step/2][y];
        }
        if(y - step/2 >= 0){
            count++;
            average += map[x][y - step/2];
        }
        if(y + step/2 < map.length){
            count++;
            average += map[x][y + step/2];
        }
        return average/count;
    }

    private float randVariance(float v){
        return (float) (r.nextFloat()*2*v - v);
    }
}
