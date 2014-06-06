package com.example.pviewer;

import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class ObservationMapping extends Drawable
{
	public Paint paint;
	public float x1, x2, y1, y2;
	public int orientation;
	
	ObservationMapping() {;}
	
	public void draw(Canvas canvas)
	{
		if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			float temp;
			
			temp = x1;
			x1 = y1;
			y1 = temp;
			
			temp = x2;
			x2 = y2;
			y2 = temp;
		}
		
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(2.5f);
		
		canvas.drawLine(x1,y1,x2,y2,paint);
	}

	public int getOpacity()
	{
		return 0;
	}
	
	public void setAlpha(int arg0) {;}
	
	public void setColorFilter(ColorFilter arg0) {;}
	
	public String toString()
	{
		return "[ObservationMapping]: (" + x1 + "," + y1 + ";" + x2 + "," + y2 + ")";
	}
}
