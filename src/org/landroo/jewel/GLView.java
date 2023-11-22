package org.landroo.jewel;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.FloatMath;
import android.util.Log;

/**
 * OpneGL Surface and renderer
 * @author rkovacs
 *
 */
class GLView extends GLSurfaceView implements GLSurfaceView.Renderer
{
	private final static String TAG = "Jewel";
	private static final int MAX_NUMS = 6;
	
	private float deltaTime = 1;
	private long lastFrameStart = System.nanoTime();
	
    private long startTime = System.nanoTime();
    private int frames = 0;
    
    private GL10 gl;
    
    public Jewel jewel;
    private Bitmap stonesBitmap;
    private Texture stoneTexture = null;
    
    public int tableWidth;
    public int tableHeight;

    public int displayWidth;
    private int displayHeight;

    private float pictureWidth;
    private float pictureHeight;
    private float origWidth;
    private float origHeight;    
    
    private int xOff = 0;
    private int yOff = 0;
    
    public int size = 64;
    public float tablesizeX = 1;
    public float tablesizeY = 1;
    
    private Sprite head;
    private Sprite splash;
    
    private Sprite[][] numbers = new Sprite[MAX_NUMS][10];
    private int numSize;
    
    public boolean score = true;
    private int oldScore = -1;
    private String sScore = "";
	public int iMaxScore;
	private int iScore = 0;
	
    public MusicService mServ;
    public SoundManager mSoundManager;
    public boolean sound = true;
    
    public int[][] table = null;
    
    // swipe
    private Timer timer = null;			// scroll timer
    private float swipeVelocity = 0;	//
	private float swipeSpeed = 0;		//
	private float swipeDistX = 0;		//
	private float swipeDistY = 0;		// 
	private float backSpeedX = 0;
	private float backSpeedY = 0;
	private float offMarginX = 0;
	private float offMarginY = 0;
	
	private float zoomSize = 0;	
    
	// move
	private int mX = 0;					// touch x
	private int mY = 0;					// touch y
	private int sX = 0;					// touch start x
	private int sY = 0;					// touch start y
	private float xPos = 0;
	private float yPos = 0;
	
	// zoom
	private float zoomDist = 0;			//
	private float sizeDiff;				// zoom multiplier
	private float zoom = 1f;			// zoom factor
	
	private boolean starting = false; 
	
	public boolean demo = true;
	public boolean zoomable = false;
    
	public GLView(Context context) 
	{
		super(context);
		setRenderer(this);
		
		sScore = "";
		for(int i = 0; i < MAX_NUMS; i++) sScore += "0";
		
        timer = new Timer();
		timer.scheduleAtFixedRate(new SwipeTask(), 0, 10);
	}
	
	/**
	 * Initialize the OpenGL surface
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		this.gl = gl;
		
        gl.glEnable(GL10.GL_BLEND);											// enable blend mode
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);		// set blend type
        
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		start(this.getWidth(), this.getHeight(), true);
	}
	
	public void start(int width, int height, boolean showSplash)
	{
		starting = true;
		
		displayWidth = width;
		displayHeight = height;
		
		pictureWidth = (int)((float)displayWidth * tablesizeX);
		pictureHeight = (int)((float)displayHeight * tablesizeY);
		origWidth = pictureWidth;
		origHeight = pictureHeight;
		
		tableWidth = (int)(pictureWidth / size);
		tableHeight = (int)(pictureHeight / size);
		
		xOff = (displayWidth - (tableWidth * size)) / 2;
		yOff = (displayHeight - (tableHeight * size)) / 2;
		
		jewel = new Jewel(tableWidth, tableHeight, size);
		jewel.demo = demo;
		jewel.iLastScore = iScore;
		
		TextureAtlas ta = new TextureAtlas();

		stonesBitmap = ta.stones(size);
		//Log.i(TAG, "Atlas: " + stonesBitmap.getWidth());
		numSize = ta.numWidth;

		if(stoneTexture != null) stoneTexture.dispose();
    	stoneTexture = new Texture(gl, stonesBitmap);
    	stoneTexture.bind();
    	
    	jewel.gl = gl;
    	jewel.stoneTexture = stoneTexture;

    	initBack(gl);
        initCursors(gl);
        initStones(gl);
        initNumbers(gl);
        
        //TextureRegion region = new TextureRegion(stoneTexture, size, size * 2, size, size);
		TextureRegion region = new TextureRegion(stoneTexture, size, size * 2, size, size);
		head = new Sprite(gl, xOff, yOff + tableHeight * size , pictureWidth, displayHeight, region, 9);
		
		region = new TextureRegion(stoneTexture, size * 2, size * 2, size * 2, size * 2);
		splash = new Sprite(gl, (displayWidth - size * 4) / 2, (displayHeight - size * 4) / 2, size * 2, size * 2, region, 0);
		splash.scale = 2;
		splash.visible = showSplash;
        
    	jewel.start();
    	
		if(this.table == null)
		{
			this.table = new int[tableWidth][tableHeight];
			for(int x = 0; x < tableWidth; x++)
				for(int y = 0; y < tableHeight; y++)
					this.table[x][y] = jewel.sprites[x][y].type;
		}
		
		starting = false; 
	}
	
	/**
	 * onSurfaceChanged
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
	}

	/**
	 * create cursor sprites
	 * @param gl
	 */
	private void initCursors(GL10 gl)
	{
		TextureRegion region = new TextureRegion(stoneTexture, 3 * size, size, size, size);
		jewel.cursors[0] = new Sprite(gl, 0, 0, size, size, region, 8);
		jewel.cursors[0].visible = false;
		jewel.cursors[0].setOffset(xOff, yOff);
		
		jewel.cursors[1] = new Sprite(gl, 0, 0, size, size, region, 8);
		jewel.cursors[1].visible = false;
		jewel.cursors[1].setOffset(xOff, yOff);
	}
    
	/**
	 * create background sprites
	 * @param gl
	 */
	public void initBack(GL10 gl)
	{
		int x1 = 0;
		int y1 = 0;
		int ymax = 0;
		// chess table
		for(int x = 0; x < tableWidth; x++)
		{
			y1 = 0;
			ymax = tableHeight / 2;
			if(x % 2 == 1) y1++;
			else ymax++;
			for(int y = 0; y < ymax; y++)
			{
	        	TextureRegion region = new TextureRegion(stoneTexture, 0, size * 2, size, size);
	        	jewel.back[x][y] = new Sprite(gl, x1 * size, y1 * size, size, size, region, 8);
	        	jewel.back[x][y].setOffset(xOff, yOff);
				y1 += 2;
			}
			x1++;
		}
	}
	
	/**
	 * create stones sprites
	 * @param gl
	 */
    private void initStones(GL10 gl) 
    {
    	int r;
    	TextureRegion region;
        for(int y = 0; y < tableHeight; y++) 
        {
        	for(int x = 0; x < tableWidth; x++)
        	{
	        	r = stoneType(x, y);
	        	if(r < 4) region = new TextureRegion(stoneTexture, r * size, 0, size, size);
	        	else region = new TextureRegion(stoneTexture, (r - 4) * size, size, size, size);
	        	jewel.sprites[x][y] = new Sprite(gl, x * size, y * size, size, size, region, r);
	        	jewel.sprites[x][y].setOffset(xOff, yOff);
        	}
        }
    }
    
    /**
     * set stone types
     * @param x
     * @param y
     * @return
     */
    private int stoneType(int x, int y)
    {
    	int type = Jewel.random(0, 6, 1);
    	if(table != null && table.length > y && table[0].length > x) type = this.table[y][x];

    	return type;
    }

    /**
     * create number sprites
     * @param gl
     */
    private void initNumbers(GL10 gl) 
    {
    	TextureRegion region;
        for(int x = 0; x < MAX_NUMS; x++) 
        {
        	for(int y = 0; y < 10; y++)
        	{
	        	if(y < 5) region = new TextureRegion(stoneTexture, y * numSize, size * 3, numSize, size / 2);
	        	else region = new TextureRegion(stoneTexture, (y - 5) * numSize, size * 3 + size / 2, numSize, size / 2);
	        	numbers[x][y] = new Sprite(gl, 0, 0, numSize, size / 2, region, y);
	        	numbers[x][y].visible = false;
	        	numbers[x][y].setOffset(-xPos, -yPos);
        	}
        }
    }
    
	public void onDrawFrame(GL10 gl)
	{
		long currentFrameStart = System.nanoTime();
		deltaTime = (currentFrameStart - lastFrameStart) / 1000000000.0f;
		lastFrameStart = currentFrameStart;
		
    	setViewportAndMatrices();
		
    	if(starting == false)
    	{
    		updateSprite();
		
    		drawSprite(gl);
    	}
		
		logFrame();
	}
	
    private void updateSprite()
	{
    	int ret;
        for(int y = 0; y < tableHeight; y++)
        {
        	for(int x = 0; x < tableWidth; x++)
        	{
        		ret = jewel.sprites[x][y].update(deltaTime);
        		if(ret == 1 || ret == 2)
        		{
        			jewel.moveEnd();
            		if(jewel.sprites[x][y].sound)
            		{
            			if(this.sound) mSoundManager.playSound(1);
            			jewel.sprites[x][y].sound = false;
            		}
        		}
        		if(ret == 3) jewel.zoomEnd();
        	}
        }
        if(oldScore != jewel.iLastScore)
        {
        	oldScore = jewel.iLastScore;
        	iScore = oldScore; 
        	
        	if(iMaxScore < oldScore) iMaxScore = oldScore;

        	String sNum;
        	if(splash.visible) sNum = "" + iMaxScore;
        	else sNum = "" + oldScore;
        	StringBuffer sZer = new StringBuffer();
        	for(int i = 0; i < MAX_NUMS; i++) sZer.append("0");
        	String sScor = sZer.toString().substring(sNum.length()) + sNum;
        	sScore = sScor; 

            for(int x = 0; x < MAX_NUMS; x++) 
            	for(int y = 0; y < 10; y++)
            		numbers[x][y].visible = false;
        }
	}
	
    private void drawSprite(GL10 gl)
    {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        drawBack();
        
        drawStones();
        
        if(!starting) 
        {
        	jewel.cursors[0].draw();
        	jewel.cursors[1].draw();
        	
        	head.draw();
        	
        	splash.draw();
        }
        
        if(splash.visible) drawScore(true);
        else if(score) drawScore(false);	
    }
    
    /**
     * draw chess table
     */
    private void drawBack()
    {
        for(int y = 0; y < tableHeight / 2 + 1; y++) 
        	for(int x = 0; x < tableWidth; x++)
        		if(!starting && jewel.back[x][y] != null) jewel.back[x][y].draw();
    }

    /**
     * draw the stones
     */
    private void drawStones()
    {
        for(int y = 0; y < tableHeight; y++) 
        	for(int x = 0; x < tableWidth; x++)
       			if(!starting) jewel.sprites[x][y].draw();
    }
    
    private void drawScore(boolean splash)
    {
    	int no;
    	for(int i = 0; i < MAX_NUMS; i++)
    	{
    		no = Integer.parseInt(sScore.substring(i, i + 1));
    		numbers[i][no].x = i * numSize;
    		numbers[i][no].visible = true;
    		numbers[i][no].draw();
    		if(splash) numbers[i][no].setOffset((displayWidth - numSize * MAX_NUMS) / 2, displayHeight / 2 - size * 2 + 2);
    		else numbers[i][no].setOffset(-xPos, -yPos);
    	}
    }
    
    public void setViewportAndMatrices() 
    {
        gl.glViewport(0, 0, displayWidth, displayHeight);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(xPos * -1, xPos * -1 + displayWidth * zoom, yPos * -1, yPos * -1 + displayHeight * zoom, 1, -1);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    public void logFrame() 
    {
        frames++;
        if(System.nanoTime() - startTime >= 1000000000) 
        {
            //Log.d(TAG, "fps: " + frames + " " + jewel.effectCnt); 
            frames = 0;
            startTime = System.nanoTime();
        }
    }
    
    /**
     * Select a stone
     * @param fx screen x coordinate
     * @param fy screen y coordinate
     */
    public void onTap(float fx, float fy)
    {
    	splash.visible = false;
    	oldScore = 0;
    	
//		float bx = (fx - xPos) * (origWidth / pictureWidth);
//		float by = (displayHeight - fy - yPos) * (origHeight / pictureHeight);
    	
    	float tx = fx - xPos - xOff;
    	float ty = displayHeight - fy - yPos - yOff;
    	
		float bx = (int)(tx / (this.size * (1 / zoom)));
		float by = (int)(ty / (this.size * (1 / zoom)));

		//Log.i("jewel", "tx: " + sx + " " + fx + " " + (1 / zoom) + " " + xPos + " " + xOff);
    	
    	jewel.selectStone((int)bx, (int)by);
    }
    
    /**
     * touching the screen
     * @param fx
     * @param fy
     */
    public void onDown(float fx, float fy)
    {
    	sX = (int)fx;
    	sY = displayHeight - (int)fy;
    	
   		swipeVelocity = 0;
    }
    
    /**
     * move table
     * @param fx
     * @param fy
     */
    public void onMove(float fx, float fy)
    {
    	splash.visible = false;
    	oldScore = 0;
    	
    	if(tablesizeX <= 1 && tablesizeY <= 1) return;
/*    	
    	mX = (int)fx;
    	mY = displayHeight - (int)fy;
    	
		float dx = mX - sX;
		float dy = mY - sY;

		if(xPos + dx < (float)(displayWidth - xOff - screenWidth) || xPos + dx > (float)Math.abs(xOff)) dx = 0;
		if(yPos + dy < (float)(displayHeight - yOff - screenHeight) || yPos + dy > (float)Math.abs(yOff)) dy = 0;
		
		xPos += dx;
		yPos += dy;
		
		sX = (int)mX;
		sY = (int)mY;
*/		
		mX = (int) fx;
//		mY = (int) fy;
		mY = displayHeight - (int)fy;

		float dx = mX - sX;
		float dy = mY - sY;

		// picture bigger than the display
		if (pictureWidth >= displayWidth)
		{
			if(xPos + dx < displayWidth - (pictureWidth + offMarginX) || xPos + dx > offMarginX) dx = 0;
			if(yPos + dy < displayHeight - (pictureHeight + offMarginY) || yPos + dy > offMarginY) dy = 0;
		}
		else
		{
			if(xPos + dx > displayWidth - pictureWidth || xPos + dx < 0) dx = 0;
			if(yPos + dy > displayHeight - pictureHeight || yPos + dy < 0) dy = 0;
		}

		xPos += dx;
		yPos += dy;

		sX = (int) mX;
		sY = (int) mY;
    }

    public void reset()
    {
    	table = null;
    	
    	mX = 0;
    	mY = 0;
    	
    	sX = 0;
    	sY = 0;
    	
    	xPos = 0;
    	yPos = 0;
    	
    	zoom = 1;
    }
    
    public void onSwipe(int direction, float velocity, float x1, float y1, float x2, float y2)
	{
    	if(tablesizeX <= 1 && tablesizeY <= 1) return;
    	
    	swipeDistX = x2 - x1;
		swipeDistY = y2 - y1;    			
		swipeSpeed = 1;
		swipeVelocity = velocity;
		if(swipeVelocity > 200)	swipeVelocity = 200;
    }
    
    class SwipeTask extends TimerTask 
    {
        public void run() 
        {
			if (swipeVelocity > 0)
			{
				float dist = FloatMath.sqrt(swipeDistY * swipeDistY + swipeDistX * swipeDistX);
				float x = xPos - (float) ((swipeDistX / dist) * (swipeVelocity / 10));
				float y = yPos - (float) ((swipeDistY / dist) * (swipeVelocity / 10));

				if ((pictureWidth >= displayWidth) && (x < displayWidth - (pictureWidth + offMarginX) || x > offMarginX)
						|| ((pictureWidth < displayWidth) && (x > displayWidth - pictureWidth || x < 0)))
				{
					swipeDistX *= -1;
					swipeSpeed = swipeVelocity;
					//swipeSpeed += .5;
				}

				if ((pictureHeight >= displayHeight) && (y < displayHeight - (pictureHeight + offMarginY) || y > offMarginY)
						|| ((pictureHeight < displayHeight) && (y > displayHeight - pictureHeight || y < 0)))
				{
					swipeDistY *= -1;
					swipeSpeed = swipeVelocity;
					//swipeSpeed += .5;
				}

				xPos -= (float) ((swipeDistX / dist) * (swipeVelocity / 10));
				yPos -= (float) ((swipeDistY / dist) * (swipeVelocity / 10));

				swipeVelocity -= swipeSpeed;
				swipeSpeed += .0001;

				if(swipeVelocity <= 0) checkOff();
			}        	
        	/*if(swipeVelocity > 0)
        	{
        		float dist = FloatMath.sqrt(swipeDistY * swipeDistY + swipeDistX * swipeDistX);
    			float x = xPos - (float)((swipeDistX / dist) * (swipeVelocity / 10));
    			float y = yPos - (float)((swipeDistY / dist) * (swipeVelocity / 10));
    			
    			if(x < (displayWidth - xOff - screenWidth) || x > Math.abs(xOff))
    			{
    				swipeDistX *= -1;
    				swipeSpeed += .01;
    			}

    			if(y < (displayHeight - yOff - screenHeight) || y > Math.abs(yOff))
    			{
    				swipeDistY *= -1;
    				swipeSpeed += .01;
    			}
    			
    			xPos -= (float)((swipeDistX / dist) * (swipeVelocity / 10));
    			yPos -= (float)((swipeDistY / dist) * (swipeVelocity / 10));
    			
    			swipeVelocity -= swipeSpeed;
    			swipeSpeed += .0001;
        	}*/
        	
			if(backSpeedX != 0)
			{
				if((backSpeedX < 0 && xPos <= 0.1f) || (backSpeedX > 0 && xPos + 0.1f >= displayWidth - pictureWidth)) backSpeedX = 0;
				else if(backSpeedX < 0) xPos -= xPos / 20;
				else xPos += (displayWidth - (pictureWidth + xPos)) / 20;
			}
			
			if(backSpeedY != 0)
			{
				if((backSpeedY < 0 && yPos <= 0.1f) || (backSpeedY > 0 && yPos + 0.1f >= displayHeight - pictureHeight)) backSpeedY = 0;
				else if(backSpeedY < 0) yPos -= yPos / 20;
				else yPos += (displayHeight - (pictureHeight + yPos)) / 20;
			}
        }
    }
    
	public void onZoom(int mode, float x, float y, float distance, float xdiff,	float ydiff) 
	{
//		if(zoomable == false) return;
		int dist = (int) distance * 5;
		switch (mode)
		{
		case 1:
			zoomSize = dist;
			break;
		case 2:
			int diff = (int) (dist - zoomSize);
			float sizeNew = FloatMath.sqrt(pictureWidth * pictureWidth + pictureHeight * pictureHeight);
			float sizeDiff = 100 / (sizeNew / (sizeNew + diff));
			float newSizeX = pictureWidth * sizeDiff / 100;
			float newSizeY = pictureHeight * sizeDiff / 100;

			// zoom between min and max value
			if (newSizeX > origWidth / 4 && newSizeX < origWidth * 10)
			{
				//bitmapDrawable.setBounds(0, 0, (int)(newSizeX / displayWidth * tileSize), (int)(newSizeY / displayHeight * tileSize));
				
				zoomSize = dist;

				float diffX = newSizeX - pictureWidth;
				float diffY = newSizeY - pictureHeight;
				float xPer = 100 / (pictureWidth / (Math.abs(xPos) + mX)) / 100;
				float yPer = 100 / (pictureHeight / (Math.abs(yPos) + mY)) / 100;

				xPos -= diffX * xPer;
				yPos -= diffY * yPer;

				pictureWidth = newSizeX;
				pictureHeight = newSizeY;

				if (pictureWidth > displayWidth || pictureHeight > displayHeight)
				{
					if (xPos > 0) xPos = 0;
					if (yPos > 0) yPos = 0;

					if (xPos + pictureWidth < displayWidth) xPos = displayWidth - pictureWidth;
					if (yPos + pictureHeight < displayHeight) yPos = displayHeight - pictureHeight;
				}
				else
				{
					if (xPos <= 0) xPos = 0;
					if (yPos <= 0) yPos = 0;

					if (xPos + pictureWidth > displayWidth) xPos = displayWidth - pictureWidth;
					if (yPos + pictureHeight > displayHeight) yPos = displayHeight - pictureHeight;
				}

				// Log.i(TAG, "" + xPos + " " + yPos);
			}
			break;
		case 3:
			zoomSize = 0;
			break;
		}
/*		
		int newDist = (int)distance * 2;
		switch(mode)
		{
			case 1:
				zoomDist = newDist;
				break;
			case 2:	
	    		int diff = (int)(newDist - zoomDist);
	    		float sizeOrig =  FloatMath.sqrt(displayWidth * displayWidth + displayHeight * displayHeight);	// triangle longest length
	    		sizeDiff = (sizeOrig + diff) / sizeOrig;								// change ratio percent
	    		float newSizeX = (float)(screenWidth * sizeDiff);
	    		float newSizeY = (float)(screenHeight * sizeDiff);
	    		
	    		zoomDist = newDist;
	    		
	    		float diffX = newSizeX - screenWidth;
	    		float diffY = newSizeY - screenHeight;
	    		float dXPer = 100 / ((float)screenWidth / (Math.abs(xPos) + mX)) / 100;
	    		float dYPer = 100 / ((float)screenHeight / (Math.abs(yPos) + mY)) / 100;
	    		
	    		xPos -= diffX * dXPer;
	    		yPos -= diffY * dYPer;
	    		

	    		
	    		if(sizeDiff < 1 && zoom < 2) zoom  += 1 - sizeDiff;
	    		else if(zoom > 0.5f) zoom  -= sizeDiff - 1;
    	    		
	    		//Log.d(TAG, "zoom: "+ zoom);

				break;
			case 3:
				zoomDist = 0;
				break;
		}*/
		
	}
	
	public void pause()
	{
		if(jewel != null) jewel.pause = true;
	}
	
	public void resume()
	{
		if(jewel != null) jewel.pause = false;
	}

	// check the offset of the play table
	private void checkOff()
	{
		if(pictureWidth >= displayWidth)
		{
			if(xPos > 0 && xPos <= offMarginX) backSpeedX = -1;
			else if(xPos < pictureWidth - offMarginX && xPos <= pictureWidth) backSpeedX = 1;
		}
		if(pictureHeight >= displayHeight)
		{
			if(yPos > 0 && yPos <= offMarginY) backSpeedY = -1;
			else if(yPos < pictureHeight - offMarginY && yPos <= pictureHeight) backSpeedY = 1;
		}
	}
	
	public void onUp(float fx, float fy)
	{
		checkOff();
	}
}
