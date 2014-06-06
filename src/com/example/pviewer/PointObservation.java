package com.example.pviewer;

import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class PointObservation extends Drawable
{
	public Paint paint;
	public float x, y;
	public int orientation;
	
	PointObservation() {;}
	
	public void draw(Canvas canvas)
	{
		float width = 5;
		
		if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			float temp;
			
			temp = x;
			x = y;
			y = temp;
		}
		
		paint.setColor(Color.MAGENTA);
		paint.setStrokeWidth(1.5f);
		
		canvas.drawLine(x - width,y - width,x + width,y + width,paint);
		canvas.drawLine(x - width,y + width,x + width,y - width,paint);
	}

	public int getOpacity()
	{
		return 0;
	}
	
	public void setAlpha(int arg0) {;}
	
	public void setColorFilter(ColorFilter arg0) {;}
	
	public String toString()
	{
		return "[PointObservation]: (" + x + "," + y + ")";
	}
}
