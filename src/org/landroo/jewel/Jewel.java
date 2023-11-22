package org.landroo.jewel;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.opengles.GL10;

public class Jewel 
{
	private final static String TAG = "Jewel";
	
	private int size = 40;
		
	public int effectCnt = 0;
	private int[] lastStones;
	
	private int tableWidth;
	private int tableHeight;
	
	public Sprite[][] back;
	public Sprite[][] sprites;
	public Sprite[] cursors;
	
	public GL10 gl;							// 
	public Texture stoneTexture;			// stones texture
	
	public int iLastScore = 0;				// last score
	private int iScoreMulti = 1;			// score multiply
	
	public Timer timer = null;				// help timer
	private int iHelpCnt = 0;
	private int iDemoCnt = 0;
	
	public boolean demo = true;
	public boolean pause = false;
	
	public Jewel(int tableWidth, int tableHeight, int size)
	{
		this.tableWidth = tableWidth;
		this.tableHeight = tableHeight;
		
		this.size = size;  
		
		this.back = new Sprite[tableWidth][tableHeight / 2 + 1];
		this.sprites = new Sprite[tableWidth][tableHeight];
		this.cursors = new Sprite[2];
		
		this.lastStones = new int[4];
		
		this.timer = new Timer();
		this.timer.scheduleAtFixedRate(new timerTask(), 1, 1000);
	}
	
    class timerTask extends TimerTask 
    {
        public void run() 
        {
        	if(effectCnt > 0) return;
        	if(pause) return;
        	
        	iHelpCnt++;
        	if(demo) iDemoCnt++;
        	if(iDemoCnt > 120)
        	{
				String sSteps = possibleSteps();
				if(sSteps.length() > 0)
				{
					String[] aSteps = sSteps.split("\n");
					int iRand = random(0, aSteps.length - 2, 1);
					if(aSteps.length == 1) iRand = 0;
					String sStep = aSteps[iRand];
	
					String[] selStep = sStep.split(",");
					
					lastStones[0] = Integer.parseInt(selStep[0]);
					lastStones[1] = Integer.parseInt(selStep[1]);
					lastStones[2] = Integer.parseInt(selStep[2]);
					lastStones[3] = Integer.parseInt(selStep[3]);
					
					startChange(false);
				}
				iDemoCnt = 120;
				iScoreMulti = 0;
        	}
        	else if(iHelpCnt > 60)
        	{
				String sSteps = possibleSteps();
				if(sSteps.length() > 0)
				{
					String[] aSteps = sSteps.split("\n");
					int iRand = random(0, aSteps.length - 2, 1);
					String sStep = aSteps[iRand];
	
					String[] selStep = sStep.split(",");
					
					cursors[0].x = Integer.parseInt(selStep[0]) * size;
					cursors[0].y = Integer.parseInt(selStep[1]) * size;
					
					cursors[0].visible = true;
					cursors[1].visible = false;
				}
				iHelpCnt = 0;
        	}
        }
    }
	
	public void start()
	{
		this.checkTable(false);
	}

    public static int random(int nMinimum, int nMaximum, int nRoundToInterval) 
	{
		if(nMinimum > nMaximum) 
		{
			int nTemp = nMinimum;
			nMinimum = nMaximum;
			nMaximum = nTemp;
		}
	
		int nDeltaRange = (nMaximum - nMinimum) + (1 * nRoundToInterval);
		double nRandomNumber = Math.random() * nDeltaRange;
	
		nRandomNumber += nMinimum;
		
		int nRet = (int)(Math.floor(nRandomNumber / nRoundToInterval) * nRoundToInterval);
	
		return nRet;
	}  
	
	/**
	 * user selected stone
	 * @param x
	 * @param y
	 * @return
	 */
	public void selectStone(int sx, int sy)
	{
		
		if(sx < 0 || sx >= this.sprites.length || sy < 0 || sy >= this.sprites[0].length) return;
		
		Sprite sprite = this.sprites[sx][sy];
		
		int cx; 
		int cy;
		int cursor = 0;
		
		this.iScoreMulti = 0;
		
		this.iHelpCnt = 0;
		this.iDemoCnt = 0;
		
		if(this.effectCnt == 0)
		{
			if(this.cursors[0].visible == false && this.cursors[1].visible == false)
			{
				this.cursors[0].x = sprite.x;
				this.cursors[0].y = sprite.y;
				this.cursors[0].visible = true;

				return;
			}
			
			if(this.cursors[0].visible == true)
			{
				cursor = 1;
				cx = (int)(this.cursors[0].x / this.size);
				cy = (int)(this.cursors[0].y / this.size);
			}
			else
			{
				cursor = 0;
				cx = (int)(this.cursors[1].x / this.size);
				cy = (int)(this.cursors[1].y / this.size);
			}
			
			// nearby
			if((sx + 1 == cx && sy == cy) 
			|| (sx - 1 == cx && sy == cy)
			|| (sy + 1 == cy && sx == cx) 
			|| (sy - 1 == cy && sx == cx))
			{
				this.cursors[cursor].x = sprite.x;
				this.cursors[cursor].y = sprite.y;
				this.cursors[cursor].visible = true;
				
				this.lastStones[0] = sx;
				this.lastStones[1] = sy;
				this.lastStones[2] = cx;
				this.lastStones[3] = cy;
				
				this.startChange(false);
			}
			else	
			{
				this.cursors[0].x = sprite.x;
				this.cursors[0].y = sprite.y;
				
				this.cursors[cursor].visible = false;
			}
		}	
	}

	private void startChange(boolean bCancel)
	{
		int sx = lastStones[0];
		int sy = lastStones[1];
		int ex = lastStones[2];
		int ey = lastStones[3];
		
		if(bCancel)
		{
			ex = lastStones[0];
			ey = lastStones[1];
			sx = lastStones[2];
			sy = lastStones[3];
		}
		
		this.sprites[sx][sy].setDesX(ex * this.size);
		this.sprites[sx][sy].setDesY(ey * this.size);
		
		this.sprites[ex][ey].setDesX(sx * this.size);
		this.sprites[ex][ey].setDesY(sy * this.size);
		
		this.effectCnt += 2;
	}
	
	public void zoomEnd()
	{
		this.effectCnt--;

		if(iDemoCnt < 120) this.iLastScore += 1 * this.iScoreMulti;
		
		if(this.effectCnt == 0) moveDown();
	}
	
	public void moveEnd()
	{
		this.effectCnt--;
		
		
		if(this.effectCnt == 0)
		{
			this.iScoreMulti++;
			
			syncTable();
			
			String sRes = possibleSteps();
			if(sRes.equals(""))
			{
				zoomAll();
				this.iLastScore = 0;
			}
			else if(!this.checkTable(false) && this.cursors[0].visible && this.cursors[1].visible) startChange(true);
			
			this.cursors[0].visible = false;
			this.cursors[1].visible = false;
		}
	}
	
	private void syncTable()
	{
		Sprite[][] newsprites = new Sprite[this.tableWidth][this.tableHeight];
		for(int x = 0; x < this.tableWidth; x++)
		{
			for(int y = 0; y < this.tableHeight; y++)
			{
				newsprites[(int)(this.sprites[x][y].x / this.size)][(int)(this.sprites[x][y].y / this.size)] = this.sprites[x][y];
				this.sprites[x][y] = null;
			}
		}
		this.sprites = newsprites;
	}
	
	private boolean checkTable(boolean bCheck)
	{
		boolean bOK = false;
		for(int x = 0; x < this.tableWidth; x++)
		{
			for(int y = 0; y < this.tableHeight; y++)
			{
				if(checkRow(x, y, 1, 0, bCheck)) bOK = true;		// right
				if(checkRow(x, y, -1, 0, bCheck)) bOK = true;		// left
				if(checkRow(x, y, 0, -1, bCheck)) bOK = true;		// up
				if(checkRow(x, y, 0, 1, bCheck)) bOK = true;		// down
			}
		}
		
		return bOK;
	}
	
	// sign the removable stones
	private boolean checkRow(int x, int y, int rx, int ry, boolean bCheck)
	{
		boolean bOK = true;
		int i = 0;
		int iCnt = 0;
		int type = this.sprites[x][y].type;
		
		// check similar stones in the line
		while(bOK)
		{
			bOK = false;
			if(x + (i * rx) >= 0 && x + (i * rx) < this.tableWidth
			&& y + (i * ry) >= 0 && y + (i * ry) < this.tableHeight 
			&& this.sprites[x + (i * rx)][y + (i * ry)].type == type)
			{
				bOK = true;
				iCnt++;
			}
			i++;
		}
		
		if(bCheck) return iCnt > 2;
		
		// if found more than three similar stone in the line
		if(iCnt > 2)
		{
			bOK = true;
			i = 0;
			while(bOK)
			{
				bOK = false;
				if(x + (i * rx) >= 0 && x + (i * rx) < this.tableWidth 
				&& y + (i * ry) >= 0 && y + (i * ry) < this.tableHeight 
				&& this.sprites[x + (i * rx)][y + (i * ry)].type == type)
				{
					bOK = true;
					if(this.sprites[x + (i * rx)][y + (i * ry)].scale == 0)
					{
						this.sprites[x + (i * rx)][y + (i * ry)].scale = 1;
						this.effectCnt++;
					}
				}
				i++;
			}
		}
		
		return iCnt > 2;
	}
	
	//
	private void moveDown()
	{
		int pos;
		int max;
		boolean downsound = true;
		boolean newsound = true;
		for(int x = 0; x < this.tableWidth; x++)
		{
			pos = 0;
			max = 0;
			for(int y = 0; y < this.tableHeight; y++)
			{
				// find removable stones
				if(this.sprites[x][y].removable)
				{
					// count removable stones
					if(max == 0) for(int i = 0; i < this.tableHeight; i++) if(this.sprites[x][i].removable) max++;
					// place a new stone to the top
					newStone(x, y, pos, max, newsound);
					newsound = false;
					downsound = true;
					pos++;
				}
				// move down stones above
				else if(pos > 0)
				{
					this.sprites[x][y].setDesY((y - pos) * this.size);
					this.sprites[x][y].sound = downsound;
					downsound = false;
					this.effectCnt++;
				}
			}
		}
	}

	// replace the stone to the to the top
	private void newStone(int x, int y, int pos, int max, boolean sound)
	{
		float px = x * this.size;
		float py = (this.tableHeight + pos) * this.size;
    	int type = random(0, 6, 1);
    	TextureRegion region;
    	if(type < 4) region = new TextureRegion(stoneTexture, type * size, 0, size, size);
    	else region = new TextureRegion(stoneTexture, (type - 4) * size, size, size, size);

		this.sprites[x][y].newSprite(px, py, region, type);
		this.sprites[x][y].removable = false;
		this.sprites[x][y].sound = sound;
		this.sprites[x][y].setDesY((this.tableHeight - max + pos) * this.size);
		
		this.effectCnt++;
	}
	
	//
	private String possibleSteps()
	{
		int p = 0;
		StringBuilder sSteps = new StringBuilder();

		for(int x = 0; x < this.tableWidth; x++)
		{
			for(int y = 0; y < this.tableHeight; y++)
			{
				p = this.sprites[x][y].type;
				
				if(x < this.tableWidth - 3 && this.sprites[x + 2][y].type == p && this.sprites[x + 3][y].type == p) sSteps.append(x + "," + y + "," + (x + 1) + "," + y + ",1\n");
				if(x > 2 && this.sprites[x - 2][y].type == p && this.sprites[x - 3][y].type == p) sSteps.append(x + "," + y + "," + (x - 1) + "," + y + ",2\n");
				if(y < this.tableHeight - 3 && this.sprites[x][y + 2].type == p && this.sprites[x][y + 3].type == p) sSteps.append(x + "," + y + "," + x + "," + (y + 1) + ",3\n");
				if(y > 2 && this.sprites[x][y - 2].type == p && this.sprites[x][y - 3].type == p) sSteps.append(x + "," + y + "," +  x + "," + (y - 1) + ",4\n");

				if(x > 0 && x < this.tableWidth - 1 && y > 0 && this.sprites[x - 1][y - 1].type == p && this.sprites[x + 1][y - 1].type == p) sSteps.append(x + "," + y + "," +  x + "," + (y - 1) + ",5\n");
				if(x > 0 && x < this.tableWidth - 1 && y > this.tableHeight - 1 && this.sprites[x - 1][y + 1].type == p && this.sprites[x + 1][y + 1].type == p) sSteps.append(x + "," + y + "," + x + "," + (y + 1) + ",6\n");
				if(x > 0 && y > 0 && y < this.tableHeight - 1 && this.sprites[x - 1][y - 1].type == p && this.sprites[x - 1][y + 1].type == p) sSteps.append(x + "," + y + "," + (x - 1) + "," + y + ",7\n");
				if(x < this.tableWidth - 1 && y > 0 && y < this.tableHeight - 1 && this.sprites[x + 1][y - 1].type == p && this.sprites[x + 1][y + 1].type == p) sSteps.append(x + "," + y + "," + (x + 1) + "," + y + ",8\n");
				
				if(x < this.tableWidth - 1 && y > 1 && y < this.tableHeight - 2 && this.sprites[x + 1][y - 1].type == p && this.sprites[x + 1][y - 2].type == p) sSteps.append(x + "," + y + "," + (x + 1) + "," + y + ",9\n");
				if(x < this.tableWidth - 2 && y > 0 && this.sprites[x + 1][y - 1].type == p && this.sprites[x + 2][y - 1].type == p) sSteps.append(x + "," + y + "," +  x + "," + (y - 1) + ",10\n");
				if(x < this.tableWidth - 2 && y < this.tableHeight - 1 && this.sprites[x + 1][y + 1].type == p && this.sprites[x + 2][y + 1].type == p) sSteps.append(x + "," + y + "," + x + "," + (y + 1) + ",11\n");
				if(x < this.tableWidth - 1 && y < this.tableHeight - 2 && this.sprites[x + 1][y + 1].type == p && this.sprites[x + 1][y + 2].type == p) sSteps.append(x + "," + y + "," + (x + 1) + "," + y + ",12\n");

				if(x > 0 && y < this.tableHeight - 2 && this.sprites[x - 1][y + 1].type == p && this.sprites[x - 1][y + 2].type == p) sSteps.append(x + "," + y + "," + (x - 1) + "," + y + ",13\n");
				if(x > 1 && y < this.tableHeight - 1 && this.sprites[x - 1][y + 1].type == p && this.sprites[x - 2][y + 1].type == p) sSteps.append(x + "," + y + "," + x + "," + (y + 1) + ",14\n");
				if(x > 1 && y > 0 && this.sprites[x - 1][y - 1].type == p && this.sprites[x - 2][y - 1].type == p) sSteps.append(x + "," + y + "," +  x + "," + (y - 1) + ",15\n");
				if(x > 0 && y > 1 && this.sprites[x - 1][y - 1].type == p && this.sprites[x - 1][y - 2].type == p) sSteps.append(x + "," + y + "," + (x - 1) + "," + y + ",16\n");
			}
		}
		
		return sSteps.toString();			
	}
	
	private void zoomAll()
	{
		for(int x = 0; x < this.tableWidth; x++)
		{
			for(int y = 0; y < this.tableHeight; y++)
			{
				this.sprites[x][y].scale = 1;
				this.effectCnt++;
			}
		}
	}
	
}   
	
	


