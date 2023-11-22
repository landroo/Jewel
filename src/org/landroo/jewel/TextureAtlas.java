package org.landroo.jewel;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.SweepGradient;

public class TextureAtlas 
{
	public int numWidth = 0;
	
	public Bitmap stones(int size)
	{
		int textSize = 512;
		if(size * 4 <= 128) textSize = 128;
		else if(size * 4 <= 256) textSize = 256;
		else if(size * 4 <= 512) textSize = 512;
		
		Bitmap bitmap = Bitmap.createBitmap(textSize, textSize, Bitmap.Config.ARGB_4444);
		bitmap.eraseColor(Color.TRANSPARENT);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		Bitmap stone;
		int s = size - 3;
		
		// first line
		stone = drawPoly1(s, s, Color.RED);
		canvas.drawBitmap(stone, size * 0 + 1, 1, paint);
		
		stone = drawPoly2(s, s, Color.GREEN);
		canvas.drawBitmap(stone, size * 1 + 1, 1, paint);
		
		stone = drawPoly3(s, s, Color.BLUE);
		canvas.drawBitmap(stone, size * 2 + 1, 1, paint);

		stone = drawPoly4(s, s, Color.MAGENTA);
		canvas.drawBitmap(stone, size * 3 + 1, 1, paint);
		
		
		// second line
		stone = drawPoly5(s, s, Color.YELLOW);
		canvas.drawBitmap(stone, size * 0 + 1, size + 1, paint);
		
		stone = drawPoly6(s, s, Color.CYAN);
		canvas.drawBitmap(stone, size * 1 + 1, size + 1, paint);
		
		stone = drawCircle(s / 2, Color.WHITE);
		canvas.drawBitmap(stone, size * 2 + 1, size + 1, paint);
		
		stone = cursor(size, size, Color.WHITE);
		canvas.drawBitmap(stone, size * 3, size, paint);
		
		
		// third line
		stone = backRect(size, size, 0xFF333333);
		canvas.drawBitmap(stone, size * 0, size * 2, paint);
		
		stone = backRect(size, size, Color.BLACK);
		canvas.drawBitmap(stone, size * 1, size * 2, paint);
		
		// fourth line
		Bitmap nums = drawNumbers(size / 2);
		canvas.drawBitmap(nums, 0, size * 3, paint);
		
		//splash
		Bitmap splash = drawSplash(size);
		canvas.drawBitmap(splash, size * 2, size * 2, paint);
		
		return bitmap;
	}
	
	private Bitmap backRect(int h, int w, int color)
	{
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		bitmap.eraseColor(Color.TRANSPARENT);		
		Canvas canvas = new Canvas(bitmap);
		
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		
		RectF rect = new RectF(0, 0, w, h);
		canvas.drawRect(rect, paint);
		
		return bitmap;
	}
	
	/**
	 * draw an empty frame
	 * @param h		height
	 * @param w		width
	 * @param color	color
	 * @return		bitmap
	 */
	private Bitmap cursor(int h, int w, int color)
	{
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		bitmap.eraseColor(Color.TRANSPARENT);		
		Canvas canvas = new Canvas(bitmap);
		
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2);
		
		RectF rect = new RectF(0, 0, w, h);
		canvas.drawRoundRect(rect, 5, 5, paint);
		
		return bitmap;
	}

	/**
	 * draw a circle
	 * @param r		ray
	 * @param color	color
	 * @return		bitmap
	 */
	public Bitmap drawCircle(int r, int color)
	{
		Bitmap bitmap = Bitmap.createBitmap(r * 2, r * 2, Bitmap.Config.ARGB_4444);
		bitmap.eraseColor(Color.TRANSPARENT);		
		Canvas canvas = new Canvas(bitmap);
		
		RadialGradient gradient = new RadialGradient(r, r, r, color, 0xFF777777, android.graphics.Shader.TileMode.CLAMP);
		
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setShader(gradient);
		
		canvas.drawCircle(r, r, r, paint);
		
		return bitmap;
	}
	
	/**
	 * draw a rounded corner rectangle
	 * @param h		height
	 * @param w		width
	 * @param color	color
	 * @return		bitmap
	 */
	private Bitmap drawPoly1(int h, int w, int color)
	{
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		bitmap.eraseColor(Color.TRANSPARENT);		
		Canvas canvas = new Canvas(bitmap);
		
		int[] colors = new int[3];
		colors[0] = 0xFFFFFFFF;
		colors[1] = color;
		colors[2] = 0xFFFFFFFF;
		
		SweepGradient gradient = new SweepGradient(w / 2, h / 2, colors, null);
		
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setShader(gradient);
		
		RectF rect = new RectF(0, 0, w, h);
		canvas.drawRoundRect(rect, w / 5, h / 5, paint);
		
		return bitmap;
	}

	/**
	 * draw an octogon 
	 * @param h		height
	 * @param w		width
	 * @param color	color
	 * @return		bitmap
	 */
	private Bitmap drawPoly2(int h, int w, int color)
	{
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		bitmap.eraseColor(Color.TRANSPARENT);		
		Canvas canvas = new Canvas(bitmap);
		
		int[] colors = new int[3];
		colors[0] = 0xFFFFFFFF;
		colors[1] = color;
		colors[2] = 0xFFFFFFFF;
		
		SweepGradient gradient = new SweepGradient(w / 2, h / 2, colors, null);

		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setShader(gradient);
		
		Path path = new Path();
		path.moveTo(w / 3, 0);
		path.lineTo(w / 3 * 2, 0);
		path.lineTo(w, h / 3);
		path.lineTo(w, h / 3 * 2);
		path.lineTo(w / 3 * 2, h);
		path.lineTo(w / 3, h);
		path.lineTo(0, h / 3 * 2);
		path.lineTo(0, h / 3);
		path.lineTo(w / 3, 0);
		
		canvas.drawPath(path, paint);
		
		return bitmap;
	}
	
	/**
	 * draw a six angle
	 * @param h		height
	 * @param w		width
	 * @param color	color
	 * @return		bitmap
	 */
	private Bitmap drawPoly3(int h, int w, int color)
	{
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		bitmap.eraseColor(Color.TRANSPARENT);		
		Canvas canvas = new Canvas(bitmap);
		
		int[] colors = new int[3];
		colors[0] = 0xFFFFFFFF;
		colors[1] = color;
		colors[2] = 0xFFFFFFFF;
		
		SweepGradient gradient = new SweepGradient(w / 2, h / 2, colors, null);		

		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setShader(gradient);
		
		Path path = new Path();
		path.moveTo(w / 3, 0);
		path.lineTo(w / 3 * 2, 0);
		path.lineTo(w, h / 2);
		path.lineTo(w / 3 * 2, h);
		path.lineTo(w / 3, h);
		path.lineTo(0, h / 2);
		path.lineTo(w / 3, 0);
		
		canvas.drawPath(path, paint);
		
		return bitmap;
	}
	
	/**
	 * draw a triangle
	 * @param h		height
	 * @param w		width
	 * @param color	color
	 * @return		bitmap
	 */
	private Bitmap drawPoly4(int h, int w, int color)
	{
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		bitmap.eraseColor(Color.TRANSPARENT);		
		Canvas canvas = new Canvas(bitmap);
		
		int[] colors = new int[3];
		colors[0] = 0xFFFFFFFF;
		colors[1] = color;
		colors[2] = 0xFFFFFFFF;
		
		SweepGradient gradient = new SweepGradient(w / 2, 0, colors, null);
		
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setShader(gradient);
		
		Path path = new Path();
		path.moveTo(w / 2, 0);
		path.lineTo(w, h);
		path.lineTo(0, h);
		path.lineTo(w / 2, 0);	
		
		canvas.drawPath(path, paint);
		
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(1);

		canvas.drawLine(w / 4, h / 2, w * 3 / 4, h / 2, paint);
		canvas.drawLine(w / 4, h / 2, w / 2, h, paint);
		canvas.drawLine(w * 3 / 4, h / 2, w / 2, h, paint);
		
		return bitmap;
	}
	
	/**
	 * draw a trapéz
	 * @param h		height
	 * @param w		width
	 * @param color	color
	 * @return		bitmap
	 */
	private Bitmap drawPoly5(int h, int w, int color)
	{
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		bitmap.eraseColor(Color.TRANSPARENT);		
		Canvas canvas = new Canvas(bitmap);
		
		int[] colors = new int[3];
		colors[0] = 0xFFFFFFFF;
		colors[1] = color;
		colors[2] = 0xFFFFFFFF;
		
		SweepGradient gradient = new SweepGradient(w / 2, h / 2, colors, null);
		
		Paint paint = new Paint();		
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setShader(gradient);
		
		Path path = new Path();
		path.moveTo(w / 2, 0);
		path.lineTo(w, h / 2);
		path.lineTo(w / 2, h);
		path.lineTo(0, h / 2);
		path.lineTo(w / 2, 0);
		
		canvas.drawPath(path, paint);
		
		return bitmap;
	}

	/**
	 * draw a diamond
	 * @param h		height
	 * @param w		width
	 * @param color	color
	 * @return		bitmap
	 */
	private Bitmap drawPoly6(int h, int w, int color)
	{
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		bitmap.eraseColor(Color.TRANSPARENT);		
		Canvas canvas = new Canvas(bitmap);
		
		SweepGradient gradient = new SweepGradient(w / 2, h / 3, 0xFFFFFFFF, color);
		
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAntiAlias(true);
		paint.setShader(gradient);
		
		Path path = new Path();
		path.moveTo(w / 3, 0);
		path.lineTo(w / 3 * 2, 0);
		path.lineTo(w, h / 3);
		path.lineTo(w / 2, h);
		path.lineTo(0, h / 3);
		path.lineTo(w / 3, 0);
		
		canvas.drawPath(path, paint);
		
		return bitmap;
	}

	private Bitmap drawNumbers(int height)
	{
		int w = 0;
		int h = height;
		Paint paint = new Paint();
		paint.setTextSize(h);
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
		paint.setFakeBoldText(true);
		paint.setShadowLayer(3, 0, 0, Color.BLACK);

		String c;
		float f = 0;
		for(int i = 0; i < 10; i++)
		{
			c = "" + i;
			if(f < paint.measureText(c)) f = paint.measureText(c); 
		}
		w = (int)f * 10;
		numWidth = (int)f;
		
		Bitmap bitmap = Bitmap.createBitmap(w, h * 2, Bitmap.Config.ARGB_4444);
		bitmap.eraseColor(Color.TRANSPARENT);		
		Canvas canvas = new Canvas(bitmap);
		for(int i = 0; i < 10; i++)
		{
			if(i < 5) canvas.drawText("" + i, i * numWidth, h - 2, paint);
			else canvas.drawText("" + i, (i - 5) * numWidth, h * 2 - 2, paint);
		}
		
		return bitmap;
	}
	
	private Bitmap drawSplash(int s)
	{
		Bitmap bitmap = null;
		int r = Jewel.random(0, 6, 1);
		switch(r)
		{
		case 0:
			bitmap = drawPoly1(s * 2, s * 2, Color.RED);
			break;
		case 1:
			bitmap = drawPoly2(s * 2, s * 2, Color.GREEN);
			break;
		case 2:
			bitmap = drawPoly3(s * 2, s * 2, Color.BLUE);
			break;
		case 3:
			bitmap = drawPoly4(s * 2, s * 2, Color.MAGENTA);
			break;
		case 4:
			bitmap = drawPoly5(s * 2, s * 2, Color.YELLOW);
			break;
		case 5:
			bitmap = drawPoly6(s * 2, s * 2, Color.CYAN);
			break;
		case 6:
			bitmap = drawCircle(s, Color.WHITE);
			break;
		}
		
		Canvas canvas = new Canvas(bitmap);
		
		Paint paint = new Paint();
		paint.setTextSize(s / 2 - 2);
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
		paint.setFakeBoldText(true);
		paint.setShadowLayer(3, 0, 0, Color.BLACK);

		String txt = "JEWELS";
		float f = paint.measureText(txt);
		canvas.drawText(txt, (s * 2 - f) / 2, s, paint);
		txt = "unlimited";
		f = paint.measureText(txt);
		canvas.drawText(txt, (s * 2 - f) / 2, s + s / 2, paint);
		
		return bitmap;
	}
}
