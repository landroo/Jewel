package org.landroo.jewel;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLUtils; 


public class Texture 
{
	private GL10 glGraphics; 
	private int textureId; 
	private int minFilter;
	private int magFilter;
	private Bitmap bitmap;
	public int width;
    public int height;

    public Texture(GL10 glGame, Bitmap bitmap) 
    {
        this.glGraphics = glGame;
        this.bitmap = bitmap;
        apply();
    }
    
    private void apply() 
    {
        GL10 gl = glGraphics;
        int[] textureIds = new int[1];
        gl.glGenTextures(1, textureIds, 0);
        textureId = textureIds[0];
        
        try 
        {
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            setFilters(GL10.GL_NEAREST, GL10.GL_NEAREST);            
            gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
            
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            
            bitmap.recycle();
        } 
        catch(Exception e) 
        { 
            throw new RuntimeException("Couldn't apply texture ", e);
        } 
    }
     
    public void reload() 
    {
    	apply();
        bind();
        setFilters(minFilter, magFilter);        
        glGraphics.glBindTexture(GL10.GL_TEXTURE_2D, 0);
    }
     
    public void setFilters(int minFilter, int magFilter) 
    {
        this.minFilter = minFilter;
        this.magFilter = magFilter; 
        GL10 gl = glGraphics;
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, minFilter);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, magFilter);
    }    
     
    public void bind() 
    {
        GL10 gl = glGraphics;
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
    }
     
    public void dispose() 
    {
        GL10 gl = glGraphics;
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId); 
        int[] textureIds = { textureId };
        gl.glDeleteTextures(1, textureIds, 0);
        gl.glFlush();
    }
}
