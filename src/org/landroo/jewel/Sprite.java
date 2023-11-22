package org.landroo.jewel;

import javax.microedition.khronos.opengles.GL10;

class Sprite
{
	public float x;
	public float y;
	public float width;
	public float height;
    public Vertices spriteModel;
    public boolean visible = true;
    public boolean removable = false;
    public int type;
    public int speed = 200;
	public float scale = 0;
	public float scaleSpeed = 1;
	public boolean sound = false;
	
	private float scaleX = 0;// position offset while the stone is shrink
	private float scaleY = 0;
	private float dirX = 0;// directions 
    private float dirY = 0;
    private float desX = 0;// destination position
    private float desY = 0;
	private float xOff = 0;// offset from the display edge
	private float yOff = 0;
    
    private GL10 gl;

    public Sprite(GL10 gl, float x, float y, float width, float height, TextureRegion region, int type) 
    {
    	this.gl = gl;
    	
    	float[] buff = initSprite(width, height, region);
    	
    	this.spriteModel = new Vertices(gl, 4, 12, false, true); 
        spriteModel.setVertices(buff, 0, 16); 
        spriteModel.setIndices(new short[] {0, 1, 2, 2, 3, 0}, 0, 6);
    	
    	this.x = x;
    	this.y = y;
    	this.width = width;
    	this.height = height;
    	this.type = type;
    }
    
    public int update(float deltaTime) 
    { 
    	x = x + dirX * deltaTime;
        y = y + dirY * deltaTime;
        
        if((dirX < 0 && x + dirX * deltaTime < desX) || (dirX > 0 && x + dirX * deltaTime > desX))
        {
        	dirX = 0;
        	x = desX;
        	return 1;
        }
        if((dirY < 0 && y + dirY * deltaTime < desY) || (dirY > 0 && y + dirY * deltaTime > desY))
        {
        	dirY = 0;
        	y = desY;        	
        	return 2;
        }
        
        if(scale > 0)
        {
        	scale = scale - scaleSpeed * deltaTime;
        	scaleX = (width - (width * scale)) / 2;
        	scaleY = (height - (height * scale)) / 2;
        	if(scale <= 0)
        	{
        		scale = 0;
        		scaleX = 0;
        		scaleY = 0;
        		visible = false;
        		removable = true;
        		return 3;
        	}
        }
        
        return 0;
    }
    
    private float[] initSprite(float width, float height, TextureRegion region) 
    {
    	float[] vertices = new float[16];
    	int idx = 0;
        
    	vertices[idx++] = 0;
    	vertices[idx++] = 0;
    	vertices[idx++] = region.u1;
    	vertices[idx++] = region.v2;
        
    	vertices[idx++] = width;
    	vertices[idx++] = 0;
    	vertices[idx++] = region.u2;
    	vertices[idx++] = region.v2;
        
    	vertices[idx++] = width;
    	vertices[idx++] = height;
    	vertices[idx++] = region.u2;
    	vertices[idx++] = region.v1;
        
    	vertices[idx++] = 0;
    	vertices[idx++] = height;
    	vertices[idx++] = region.u1;
    	vertices[idx++] = region.v1;
        
        return vertices;
    }
    
    public void newSprite(float x, float y, TextureRegion region, int type)
    {
    	float[] buff = initSprite(this.width, this.height, region);

    	this.spriteModel = new Vertices(this.gl, 4, 12, false, true); 
        spriteModel.setVertices(buff, 0, 16); 
        spriteModel.setIndices(new short[] {0, 1, 2, 2, 3, 0}, 0, 6);
    	
    	this.x = x;
    	this.y = y;
    	this.type = type;
    	
    	this.dirX = 0;
    	this.dirY = 0;
    	this.desX = 0;
    	this.desY = 0;
    	
    	this.visible = true;
    	this.removable = false;
    }
    
    public void draw()
    {
    	if(this.visible)
    	{
			this.spriteModel.bind();
		    this.gl.glLoadIdentity();
		    this.gl.glTranslatef(x + xOff + scaleX, y + yOff + scaleY, 0);
		    if(scale > 0) this.gl.glScalef(scale, scale, 0);
		    this.spriteModel.draw(GL10.GL_TRIANGLES, 0, 6);
		    this.spriteModel.unbind();
    	}
    }
    
    public void setDesX(float desx)
    {
    	this.desX = desx;
    	this.dirX = setDir(this.x, this.desX);
    }
    
    public void setDesY(float desy)
    {
    	this.desY = desy;
    	this.dirY = setDir(this.y, this.desY);
    }
    
	private int setDir(float s, float e)
	{
		int ret = 0;
		if(s > e) ret = -speed;
		if(s < e) ret = speed;
		
		return ret;
	}
	
	public void setOffset(float x, float y)
	{
		xOff = x;
		yOff = y;
	}   
}
