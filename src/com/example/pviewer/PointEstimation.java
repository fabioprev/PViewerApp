package com.example.pviewer;

import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class PointEstimation extends Drawable
{
	public Paint paint;
	public String color;
	public float x, y, radius;
	public int orientation;
	
	public PointEstimation() {;}
	
	public void draw(Canvas canvas)
	{
		if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			float temp;
			
			temp = x;
			x = y;
			y = temp;
		}
		
		int r = Color.red(Color.parseColor(color));
		int g = Color.green(Color.parseColor(color));
		int b = Color.blue(Color.parseColor(color));
		
		paint.setColor(Color.rgb(255 - r,255 - g,255 - b));
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		
		canvas.drawCircle(x,y,radius * 4,paint);
	}
	
	public int getOpacity()
	{
		return 0;
	}
	
	public void setAlpha(int arg0) {;}
	
	public void setColorFilter(ColorFilter arg0) {;}
	
	public String toString()
	{
		return "[PointEstimation]: (" + x + "," + y + ";" + radius + ";" + color + ")";
	}
}
