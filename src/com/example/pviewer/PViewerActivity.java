package com.example.pviewer;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.locks.*;

public class PViewerActivity extends Activity
{
	private class ServerThread implements Runnable
	{
		synchronized public void run()
		{
			byte[] receivedData = new byte[8192];
			
			receivedPacket = new DatagramPacket(receivedData,receivedData.length);
			
			isConnected = true;
			
			while (isConnected)
			{
				try
				{
					serverSocket.receive(receivedPacket);
					
					String message = new String(receivedData,0,receivedPacket.getLength());
					
					int agentId = Integer.parseInt(message.split(" ")[0]);
					
					mutex.lock();
					
					if (dataAgents.containsKey(agentId))
					{
						dataAgents.remove(agentId);
						dataAgentsTimestamp.remove(agentId);
					}
					
					dataAgents.put(agentId,message);
					dataAgentsTimestamp.put(agentId,System.currentTimeMillis());
				}
				catch (Exception e)
				{
					isConnected = false;
				}
				finally
				{
					try
					{
						mutex.unlock();
					}
					catch (Exception e) {;}
				}
			}
			
			serverSocket.close();
		}
	}
	
	public static DatagramSocket serverSocket;
	
	@SuppressLint("UseSparseArrays")
	private static Map<Integer,String> dataAgents = new HashMap<Integer,String>();
	@SuppressLint("UseSparseArrays")
	private static Map<Integer,Long> dataAgentsTimestamp = new HashMap<Integer,Long>();
	private static Lock mutex = new ReentrantLock();
	private static boolean isConnected = false;
	
	Vector<Object> objectsToBeDrawn;
	private PViewerView pViewerView;
	private DatagramPacket receivedPacket;
	private float topInterfaceLine, worldXMin, worldXMax, worldYMin, worldYMax;
	private int visualizationFrequency;
	
	private class PViewerView extends SurfaceView implements SurfaceHolder.Callback
	{
		private MainThread thread;
		private Paint paint;
		private boolean hasFocus = false;
		
		public class MainThread extends Thread
		{
			private SurfaceHolder surfaceHolder;
			private PViewerView pViewerView;
			
			public MainThread(SurfaceHolder surfaceHolder, PViewerView pViewerView)
			{
				this.surfaceHolder = surfaceHolder;
				this.pViewerView = pViewerView;
			}
			
			public void run()
			{
				Canvas canvas = null;
				
				while (hasFocus)
				{
					if (surfaceHolder.getSurface().isValid())
					{
						canvas = surfaceHolder.lockCanvas();
						
						pViewerView.draw(canvas);
						
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
					
					try
					{
						Thread.sleep(30,0);
					}
					catch (Exception e) {;}
				}
			}
		}
		
		public PViewerView(Context context)
		{
			super(context);
			
			getHolder().addCallback(this);
			
			paint = new Paint();
		}
		
		@SuppressLint("UseSparseArrays")
		synchronized public void draw(Canvas canvas)
		{
			if (canvas == null) return;
			
			canvas.drawColor(Color.BLACK);
			
			drawInterface(canvas);
			
			mutex.lock();
			
			parseDataReceived();
			
			if (objectsToBeDrawn == null) return;
			
			for (int i = objectsToBeDrawn.size() - 1; i >= 0; i--)
			{
				Object o = objectsToBeDrawn.elementAt(i);
				
				if (o instanceof PointEstimation)
				{
					PointEstimation estimation = (PointEstimation) o;
					
					estimation.draw(canvas);
				}
				else if (o instanceof PointObservation)
				{
					PointObservation observation = (PointObservation) o;
					
					observation.draw(canvas);
				}
				else if (o instanceof ObservationMapping)
				{
					ObservationMapping mapping = (ObservationMapping) o;
					
					mapping.draw(canvas);
				}
			}
			
			HashMap<Integer,Long> temp = new HashMap<Integer,Long>(dataAgentsTimestamp);
			
			Iterator<Entry<Integer,Long> > it = temp.entrySet().iterator();
			
			while (it.hasNext())
			{
				Map.Entry<Integer,Long> data = it.next();
				
				if ((System.currentTimeMillis() - data.getValue()) > ((1000 / visualizationFrequency) * 5))
				{
					dataAgents.remove(data.getKey());
					dataAgentsTimestamp.remove(data.getKey());
				}
			}
			
			try
			{
				mutex.unlock();
			}
			catch (Exception e) {;}
			
			try
			{
				Thread.sleep(1000 / visualizationFrequency,0);
			}
			catch (Exception e) {;}
		}
		
		private void drawInterface(Canvas canvas)
		{
			float factor;
			
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			
			if (display.getOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) factor = 0.07f;
			else factor = 0.045f;
			
			topInterfaceLine = factor * getHeight();
			
			/// Horizontal line.
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(3);
			
			canvas.drawLine(0,topInterfaceLine,getWidth(),topInterfaceLine,paint);
			
			/// Robot connected text.
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL);
			paint.setTextSize(29);
			paint.setTypeface(Typeface.SERIF);
			
			canvas.drawText("Robot:",5,28,paint);
			
			paint.setColor(Color.RED);
			
			canvas.drawText("" + dataAgents.size(),98,28,paint);
			
			/// Pipe
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(3);
			
			canvas.drawLine(140,getHeight() * (factor / 7),140,getHeight() * (factor - (factor / 7)),paint);
			
			/// Visualization frequency text.
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL);
			paint.setTextSize(29);
			paint.setTypeface(Typeface.SERIF);
			
			canvas.drawText("Refresh:",151,28,paint);
			
			paint.setColor(Color.RED);
			
			canvas.drawText("" + visualizationFrequency + " Hz",270,28,paint);
		}
		
		private void parseDataReceived()
		{
			if (dataAgents == null) return;
			
			float resolutionX, resolutionY;
			int indexData, length, numberOfObjects;
			
			objectsToBeDrawn = new Vector<Object>();
			
			Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
			
			Iterator<Entry<Integer,String> > it = dataAgents.entrySet().iterator();
			
			while (it.hasNext())
			{
				String[] words = it.next().getValue().split(" ");
				
				numberOfObjects = Integer.parseInt(words[1]);
				indexData = numberOfObjects * 5 + 2;
				
				for (int i = 2; i < (numberOfObjects * 5); i += 5)
				{
					Object o = null;
					
					if (words[i].compareTo("EstimatedTargetModelsWithIdentityMultiAgent") == 0)
					{
						length = Integer.parseInt(words[indexData]);
						
						for (int j = indexData + 1; j < (indexData + length * 3); j += 3)
						{
							o = new PointEstimation();
							
							PointEstimation estimation = (PointEstimation) o;
							
							estimation.color = words[i + 2];
							estimation.orientation = display.getOrientation();
							estimation.paint = paint;
							estimation.radius = Float.parseFloat(words[i + 4]);
							
							if (display.getOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
							{
								resolutionX = getWidth() / (worldXMax - worldXMin);
								resolutionY = (getHeight() - topInterfaceLine) / (worldYMax - worldYMin);
							}
							else
							{
								resolutionX = getHeight() / (worldXMax - worldXMin);
								resolutionY = (getWidth() - topInterfaceLine) / (worldYMax - worldYMin);
							}
							
							estimation.x = ((Float.parseFloat(words[j + 1]) - worldXMin) * resolutionX);
							estimation.y = (((Float.parseFloat(words[j + 2]) - worldYMin) * resolutionY) + topInterfaceLine);
							
							objectsToBeDrawn.add(o);
						}
						
						indexData += (length * 3) + 1;
					}
					else if (words[i].compareTo("TargetPerceptions") == 0)
					{
						length = Integer.parseInt(words[indexData]);
						
						for (int j = indexData + 1; j < (indexData + length * 3); j += 3)
						{
							o = new PointObservation();
							
							PointObservation observation = (PointObservation) o;
							
							observation.orientation = display.getOrientation();
							observation.paint = paint;
							
							if (display.getOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
							{
								resolutionX = getWidth() / (worldXMax - worldXMin);
								resolutionY = (getHeight() - topInterfaceLine) / (worldYMax - worldYMin);
							}
							else
							{
								resolutionX = getHeight() / (worldXMax - worldXMin);
								resolutionY = (getWidth() - topInterfaceLine) / (worldYMax - worldYMin);
							}
							
							observation.x = ((Float.parseFloat(words[j + 1]) - worldXMin) * resolutionX);
							observation.y = (((Float.parseFloat(words[j + 2]) - worldYMin) * resolutionY) + topInterfaceLine);
							
							objectsToBeDrawn.add(o);
						}
						
						indexData += (length * 3) + 1;
					}
					else if (words[i].compareTo("ObservationsMapping") == 0)
					{
						length = Integer.parseInt(words[indexData]);
						
						for (int j = indexData + 1; j < (indexData + length * 5); j += 5)
						{
							o = new ObservationMapping();
							
							ObservationMapping mapping = (ObservationMapping) o;
							
							mapping.orientation = display.getOrientation();
							mapping.paint = paint;
							
							if (display.getOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
							{
								resolutionX = getWidth() / (worldXMax - worldXMin);
								resolutionY = (getHeight() - topInterfaceLine) / (worldYMax - worldYMin);
							}
							else
							{
								resolutionX = getHeight() / (worldXMax - worldXMin);
								resolutionY = (getWidth() - topInterfaceLine) / (worldYMax - worldYMin);
							}
							
							mapping.x1 = ((Float.parseFloat(words[j + 1]) - worldXMin) * resolutionX);
							mapping.y1 = (((Float.parseFloat(words[j + 2]) - worldYMin) * resolutionY) + topInterfaceLine);
							mapping.x2 = ((Float.parseFloat(words[j + 3]) - worldXMin) * resolutionX);
							mapping.y2 = (((Float.parseFloat(words[j + 4]) - worldYMin) * resolutionY) + topInterfaceLine);
							
							objectsToBeDrawn.add(o);
						}
						
						indexData += (length * 5) + 1;
					}
				}
			}
		}
		
		public void setFocus(boolean hasFocus)
		{
			this.hasFocus = hasFocus;
		}
		
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
			if (thread == null)
			{
				thread = new MainThread(getHolder(),this);
				
				thread.start();
			}
		}
		
		public void surfaceCreated(SurfaceHolder s) {;}
		
		public void surfaceDestroyed(SurfaceHolder arg0) {;}
	}
	
	public void onBackPressed()
	{
		Intent intent = new Intent(this,MainActivity.class);
		
		intent.putExtra(Utils.STRING_VISUALIZATION_FREQUENCY,"" + visualizationFrequency);
		intent.putExtra(Utils.STRING_WORLD_X_MIN,"" + worldXMin);
		intent.putExtra(Utils.STRING_WORLD_X_MAX,"" + worldXMax);
		intent.putExtra(Utils.STRING_WORLD_Y_MIN,"" + worldYMin);
		intent.putExtra(Utils.STRING_WORLD_Y_MAX,"" + worldYMax);
		
		startActivity(intent);
		
		isConnected = false;
		
		try
		{
			serverSocket.close();
			
			Toast.makeText(this,"UDP server unbound on port " + MainActivity.SERVER_PORT,Toast.LENGTH_LONG).show();
		}
		catch (Exception e) {;}
	}
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		
		try
		{
			visualizationFrequency = Integer.parseInt(intent.getStringExtra(Utils.STRING_VISUALIZATION_FREQUENCY));
			worldXMin = Float.parseFloat(intent.getStringExtra(Utils.STRING_WORLD_X_MIN));
			worldXMax = Float.parseFloat(intent.getStringExtra(Utils.STRING_WORLD_X_MAX));
			worldYMin = Float.parseFloat(intent.getStringExtra(Utils.STRING_WORLD_Y_MIN));
			worldYMax = Float.parseFloat(intent.getStringExtra(Utils.STRING_WORLD_Y_MAX));
		}
		catch (Exception e)
		{
			visualizationFrequency = 10;
			worldXMin = 21.8f;
			worldXMax = 42.6f;
			worldYMin = 22.9f;
			worldYMax = 45.0f;
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		pViewerView = new PViewerView(this);
		
		setContentView(pViewerView);
		
		if (!isConnected)
		{
			Thread serverThread = new Thread(new ServerThread());
			
			serverThread.start();
		}
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	protected void onPause()
	{
		super.onPause();
		
		pViewerView.setFocus(false);
	}
	
	protected void onRestart()
	{
		super.onRestart();
	}
	
	protected void onResume()
	{
		super.onResume();
	}
	
	protected void onStart()
	{
		super.onStart();
		
		pViewerView.setFocus(true);
	}
	
	protected void onStop()
	{
		super.onStop();
		
		pViewerView.setFocus(false);
	}
}
