package org.landroo.jewel;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import android.view.Window;
import android.view.WindowManager;

public class JewelMainActivity extends Activity implements UIInterface 
{
	private UI ui = null;
	private GLView glView;
	
    private MusicService mServ;
    private boolean mIsBound = false;
    
    private SensorManager sensorManager;
    private Sensor sensor;
    private float currOri = 0f;
    private float lastOri = -1f;
    
    private SoundManager mSoundManager;
    
    private boolean restart = false;
    
    private ServiceConnection sConn = new ServiceConnection()
    {
    	@Override
    	public void onServiceConnected(ComponentName name, IBinder binder) 
    	{
    		mServ = ((MusicService.ServiceBinder)binder).getService();
    		glView.mServ = mServ;
    		mIsBound = true;
    	}
    	
		@Override
		public void onServiceDisconnected(ComponentName name) 
		{
			mServ = null;
		}
    };
    
	void doBindService()
	{
		bindService(new Intent(this, MusicService.class), sConn, Context.BIND_AUTO_CREATE);
	}
	
	void doUnbindService()
	{
		if(mIsBound)
		{
			unbindService(sConn);
      		mIsBound = false;
		}
	}	
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        glView = new GLView(this);
        setContentView(glView);
        
		SharedPreferences settings = getSharedPreferences("org.landroo.jewel_preferences", MODE_PRIVATE);
		int ss = settings.getInt("stoneSize", 28);
        if(ss == 28)
        {
	        Display display = getWindowManager().getDefaultDisplay();
	        ss = display.getHeight(); 
	        if(ss > 240 && ss <= 320) glView.size = 32;
	        else if(ss > 320 && ss <= 480) glView.size = 40;
	        else if(ss > 480 && ss <= 854) glView.size = 64;
	        else glView.size = 96;
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("stoneSize", glView.size);
			editor.commit();
        }
        
        ui = new UI(this);
        
		doBindService();
		
		sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        
        mSoundManager = new SoundManager(getBaseContext());
        mSoundManager.addSound(1, R.raw.tick);
        glView.mSoundManager = this.mSoundManager;
    }

	private SensorEventListener listener = new SensorEventListener()
    {
        @Override
        public void onSensorChanged(SensorEvent event)
        {
    	    float[] values = event.values;
//    	    float x = values[0];
    	    float y = values[1];
    	    float z = values[2];
                	   
//			int angxy = Math.round(getViewAngDist(0, 0, x, y, true));
//			int angxz = Math.round(getViewAngDist(0, 0, x, z, true));
			int angyz = Math.round(getViewAngDist(0, 0, y, z, true));
			
			currOri = angDir(angyz);
			if(lastOri == -1) lastOri = currOri;
//			Log.i("jewel", "" + currOri);
        }
        
        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) 
        {
        }
    };
    
    private int angDir(int ang)
    {
    	int iRet = 0;
		if(ang > 0 && ang < 45) iRet = 0;
		else if(ang > 45 && ang < 135) iRet = 90;
		else if(ang > 135 && ang < 225) iRet = 180;
		else if(ang > 225 && ang < 305) iRet = 270;
		else iRet = 0;
		
		return iRet;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_jewel_main, menu);
        return true;
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle item selection
		switch (item.getItemId()) 
		{
		case R.id.menu_settings:
			Intent SettingsIntent = new Intent(this, SettingsScreen.class);
			startActivity(SettingsIntent);
			restart = true;
			return true;
			
		case R.id.menu_exit:
			System.runFinalizersOnExit(true);
			this.setResult(1);
	    	int pid = android.os.Process.myPid(); 
	    	android.os.Process.killProcess(pid);
			//this.finish();
            System.exit(0);
			return true;			
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
    
    @Override
    public void onStart() 
    {
        super.onStart();
    }
    
    @Override
    public synchronized void onResume() 
    {
        super.onResume();
        
        if(restart)
        {
        	glView = new GLView(this);
        	setContentView(glView);
        	glView.mSoundManager = this.mSoundManager;
        	restart = false;
        }
        
		SharedPreferences settings = getSharedPreferences("org.landroo.jewel_preferences", MODE_PRIVATE);
		glView.size = settings.getInt("stoneSize", 32);
		
		float f = settings.getInt("tableSizeX", 100);
		glView.tablesizeX = f / 100;
		f = settings.getInt("tableSizeY", 100);
		glView.tablesizeY = f / 100;

		glView.iMaxScore = settings.getInt("maxscore", 0);
		glView.score = settings.getBoolean("showscore", true);
		glView.sound = settings.getBoolean("sound", true);
		glView.demo = settings.getBoolean("demo", true);
		glView.zoomable = settings.getBoolean("zoom", false);
		
		if(mServ != null && mServ.isPaused()) mServ.resumeMusic();
		
		glView.resume();
    }
    
    @Override
    public synchronized void onPause() 
    {
        super.onPause();

        if(mServ.isPlaing()) mServ.pauseMusic();

		glView.pause();
		
		SharedPreferences settings = getSharedPreferences("org.landroo.jewel_preferences", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("maxscore", glView.iMaxScore);
		editor.commit();
    }
    
    @Override
    public void onStop() 
    {
        super.onStop();
        sensorManager.unregisterListener(listener);
    }

    @Override
    public void onDestroy() 
    {
        super.onDestroy();
		if(mIsBound)
		{
			mServ.stopMusic();
		}
        doUnbindService();        
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) 
    {
    	return ui.tapEvent(event);
    }

	public void onDown(float x, float y) 
	{
		glView.onDown(x, y);
	}

	public void onUp(float x, float y) 
	{
		glView.onUp(x, y);
	}

	public void onTap(float x, float y) 
	{
		glView.onTap(x, y);
	}

	public void onHold(float x, float y) 
	{
		// TODO Auto-generated method stub
		
	}

	public void onMove(float x, float y) 
	{
		glView.onMove(x, y);
	}

	public void onSwipe(int direction, float velocity, float x1, float y1, float x2, float y2) 
	{
		glView.onSwipe(direction, velocity, x1, y1, x2, y2);
	}

	public void onDoubleTap(float x, float y) 
	{
		glView.reset();
	}

	public void onZoom(int mode, float x, float y, float distance, float xdiff,	float ydiff) 
	{
		glView.onZoom(mode, x, y, distance, xdiff, ydiff);
	}

	public void onRotate(int mode, float x, float y, float angle) 
	{
		// TODO Auto-generated method stub

	}
	
	public void onConfigurationChanged(Configuration newConfig) 
	{
	    super.onConfigurationChanged(newConfig);
	    
		StringBuilder sb = new StringBuilder();
		for(int y = 0; y < glView.tableHeight; y++)
		{
			for(int x = 0; x < glView.tableWidth; x++)
			{
				if(x > 0) sb.append(",");
				sb.append("" + glView.jewel.sprites[x][y].type);
			}
			sb.append("\n");
		}
		
		String sTable = sb.toString();
		String[] sx = sTable.split("\n");
		String[] sy = sx[0].split(",");
		glView.table = new int[sy.length][sx.length];
		
		if(currOri > lastOri || (currOri == 0 && lastOri == 270))	// right
		{
			int nx = 0;
			int ny = 0;
			for(int x = 0; x < sx.length; x++)
			{
				sy = sx[x].split(",");
				for(int y = sy.length - 1; y >= 0; y--)
				{
					// Invalid int: ""
					glView.table[ny][nx] = Integer.parseInt(sy[y]);
					ny++;
				}
				ny = 0;
				nx++;
			}
		}
		else
		{
			int nx = sx.length - 1;
			int ny = 0;
			for(int x = 0; x < sx.length; x++)
			{
				sy = sx[x].split(",");
				for(int y = 0; y < sy.length; y++)
				{
					glView.table[ny][nx] = Integer.parseInt(sy[y]);
					ny++;
				}
				ny = 0;
				nx--;
			}
		}
		
		//Log.i("jewel", "" + lastOri + " " + currOri);
		
		lastOri = currOri;
		
		Display display = getWindowManager().getDefaultDisplay();
		glView.start(display.getWidth(), display.getHeight(), false);
	}
	
	private float getViewAngDist(float x1, float y1, float x2, float y2, boolean bMode)
    {
		float nDelX = x2 - x1;
		float nDelY = y2 - y1;
		float nDe = 0;

		if(bMode)
		{
			if(nDelX != 0)
			{
				nDe = 2 * (float)Math.PI;
				nDe = nDe + (float)Math.atan(nDelY / nDelX);
				if(nDelX <= 0)
				{
					nDe = (float)Math.PI;
					nDe = nDe + (float)Math.atan(nDelY / nDelX);
				}
				else if(nDelY >= 0)
				{
					nDe = 0;
					nDe = nDe + (float)Math.atan(nDelY / nDelX);
				}
			}
			else
			{
				if(nDelY == 0) nDe = 0;
				else
				{
					if(nDelY < 0) nDe = (float)Math.PI;
					nDe = nDe + (float)Math.PI / 2;
				}
			}
		
        	return nDe / (float)Math.PI * 180;
		}
        else
		{
        	return  (float)Math.sqrt(nDelY * nDelY + nDelX * nDelX);
		}
    }		

}
