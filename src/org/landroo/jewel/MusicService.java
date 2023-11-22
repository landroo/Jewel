package org.landroo.jewel;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * 
 * MusicService class
 *
 */
public class MusicService extends Service implements 
MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener 
{
	private final IBinder mBinder = new ServiceBinder(); 
    private MediaPlayer mMediaPlayer;
    private int length = 0;
    private boolean pause = false;
   
    /**
     * MusicService
     */
    public MusicService()
    {
    } 
    
    /**
     * 
     * ServiceBinder class
     *
     */
	public class ServiceBinder extends Binder 
	{
		public MusicService getService()
		{
			return MusicService.this;
		}
	}
  
	/**
	 * onBind
	 */
    @Override
    public IBinder onBind(Intent arg0)
    {
    	return mBinder;
    }

    /**
     * onCreate
     */
    @Override
    public void onCreate()
	{
    	super.onCreate(); 
	}

    /**
     * onStartCommand
     */
    @Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
    	mMediaPlayer.start();
    	return START_STICKY;
	}
	
    /**
     * pauseMusic
     */
	public void pauseMusic()
	{
		if(mMediaPlayer.isPlaying())
		{
			mMediaPlayer.pause();
			length = mMediaPlayer.getCurrentPosition(); 
			pause = true;
		}
	}
	
	public boolean isPlaing()
	{
		if(mMediaPlayer != null)
		{
			return mMediaPlayer.isPlaying();
		}
		
		return false;
	}

	public boolean isPaused()
	{
		return pause;
	}

	
	/**
	 * resumeMusic
	 */
	public void resumeMusic()
	{
		if(mMediaPlayer != null && mMediaPlayer.isPlaying() == false)
		{
			mMediaPlayer.seekTo(length);
			mMediaPlayer.start();
			pause = false;
		}
	}
	
	/**
	 * stopMusic
	 */
	public void stopMusic()
	{
		if(mMediaPlayer != null)
		{
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			pause = false;
		}
	}

	/**
	 * onDestroy
	 */
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if(mMediaPlayer != null)
		{
			try
			{
				mMediaPlayer.stop();
				mMediaPlayer.release();
			}
			finally
			{
				mMediaPlayer = null;
			}
		}
	}
	
	public void playRes(AssetFileDescriptor fileDescriptor) 
	{
		try 
		{
			if(mMediaPlayer == null) 
			{
				this.mMediaPlayer = new MediaPlayer();
			} 
			else 
			{
				mMediaPlayer.stop();
				mMediaPlayer.reset();
			}
			mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getDeclaredLength());
			mMediaPlayer.prepare();			
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setOnBufferingUpdateListener(this);
			mMediaPlayer.setOnErrorListener(this);
			pause = false;
		} 
		catch(Throwable t) 
		{
			Log.d("MusicService", t.toString());
		}
	}

	/**
	 * playMuisc
	 * @param sUrl
	 */
	public void playMuisc(String sUrl) 
	{
    	//Uri myUri = Uri.parse("android.resource://com.example.myapp/" + R.raw.my_movie");
		Uri myUri = Uri.parse(sUrl);
		try 
		{
			if(mMediaPlayer == null) 
			{
				this.mMediaPlayer = new MediaPlayer();
			} 
			else 
			{
				mMediaPlayer.stop();
				mMediaPlayer.reset();
			}
			mMediaPlayer.setDataSource(this, myUri); // Go to Initialized state
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setOnBufferingUpdateListener(this);
			mMediaPlayer.setOnErrorListener(this);
			mMediaPlayer.prepareAsync();
			pause = false;
		} 
		catch(Throwable t) 
		{
			Log.d("MusicService", t.toString() + " " + sUrl);
		}
	}

	public void onBufferingUpdate(MediaPlayer arg0, int arg1) 
	{
		Log.d("MusicService", "PlayerService onBufferingUpdate : " + arg1 + "%");
	}

	public boolean onError(MediaPlayer arg0, int arg1, int arg2) 
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Media Player Error: ");
		switch(arg1) 
		{
			case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
				sb.append("Not Valid for Progressive Playback");
				break;
			case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
				sb.append("Server Died");
				break;
			case MediaPlayer.MEDIA_ERROR_UNKNOWN:
				sb.append("Unknown");
				break;
			default:
				sb.append(" Non standard (");
				sb.append(arg1);
				sb.append(")");
		}
		sb.append(" (" + arg1 + ") ");
		sb.append(arg2);
		Log.e("MusicService", sb.toString());
		
		return true;
	}

	public void onPrepared(MediaPlayer arg0) 
	{
		mMediaPlayer.start();
	}

	public void onCompletion(MediaPlayer arg0) 
	{
		mMediaPlayer.stop();
	}
}
