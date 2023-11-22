package org.landroo.jewel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Vertices 
{ 
    final GL10 glGraphics;
    final boolean hasColor;
    final boolean hasTexCoords;
    final int vertexSize;
    final FloatBuffer vertices;
    final ShortBuffer indices; 
    
    public Vertices(GL10 glGraphics, int maxVertices, int maxIndices, boolean hasColor, boolean hasTexCoords) 
    {
        this.glGraphics = glGraphics;
        this.hasColor = hasColor;
        this.hasTexCoords = hasTexCoords;
        this.vertexSize = (2 + (hasColor ? 4: 0) + (hasTexCoords ? 2: 0)) * 4; 
        
        ByteBuffer buffer = ByteBuffer.allocateDirect(maxVertices * vertexSize);
        buffer.order(ByteOrder.nativeOrder());
        vertices = buffer.asFloatBuffer();
         
        if(maxIndices > 0) 
        {
            buffer = ByteBuffer.allocateDirect(maxIndices * Short.SIZE / 8);
            buffer.order(ByteOrder.nativeOrder());
            indices = buffer.asShortBuffer();
        } 
        else 
        { 
            indices = null;
        }            
    }
    
    public void setVertices(float[] vertices, int offset, int length) 
    {
        this.vertices.clear();
        this.vertices.put(vertices, offset, length);
        this.vertices.flip(); 
    }
    
    public void setIndices(short[] indices, int offset, int length) 
    {
        this.indices.clear();
        this.indices.put(indices, offset, length); 
        this.indices.flip();
    } 

    public void bind() 
    {
    	glGraphics.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        vertices.position(0);
        glGraphics.glVertexPointer(2, GL10.GL_FLOAT, vertexSize, vertices);
        
        if(hasColor) 
        {
        	glGraphics.glEnableClientState(GL10.GL_COLOR_ARRAY);
            vertices.position(2);
            glGraphics.glColorPointer(4, GL10.GL_FLOAT, vertexSize, vertices);
        }
        
        if(hasTexCoords) 
        {
        	glGraphics.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            vertices.position(hasColor?6:2);
            glGraphics.glTexCoordPointer(2, GL10.GL_FLOAT, vertexSize, vertices);
        }
    }
     
    public void draw(int primitiveType, int offset, int numVertices) 
    {        
        if(indices != null) 
        {
            indices.position(offset);
            glGraphics.glDrawElements(primitiveType, numVertices, GL10.GL_UNSIGNED_SHORT, indices);
        } 
        else 
        {
        	glGraphics.glDrawArrays(primitiveType, offset, numVertices);
        }        
    }

    public void unbind() 
    {
        if(hasTexCoords)
        {
        	glGraphics.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }
        if(hasColor)
        {
        	glGraphics.glDisableClientState(GL10.GL_COLOR_ARRAY);
        }
    } 
}
