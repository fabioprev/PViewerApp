package com.example.pviewer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.net.DatagramSocket;

public class MainActivity extends Activity
{
	public static final int SERVER_PORT = 6000;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_main);
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	protected void onPause()
	{
		super.onPause();
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
	}
	
	protected void onStop()
	{
		super.onStop();
	}
	
	public void startEvent(View view)
	{
		Intent mainActivityIntent = getIntent();
		Intent intent = new Intent(this,PViewerActivity.class);
		
		intent.putExtra(Utils.STRING_VISUALIZATION_FREQUENCY,mainActivityIntent.getStringExtra(Utils.STRING_VISUALIZATION_FREQUENCY));
		intent.putExtra(Utils.STRING_WORLD_X_MIN,mainActivityIntent.getStringExtra(Utils.STRING_WORLD_X_MIN));
		intent.putExtra(Utils.STRING_WORLD_X_MAX,mainActivityIntent.getStringExtra(Utils.STRING_WORLD_X_MAX));
		intent.putExtra(Utils.STRING_WORLD_Y_MIN,mainActivityIntent.getStringExtra(Utils.STRING_WORLD_Y_MIN));
		intent.putExtra(Utils.STRING_WORLD_Y_MAX,mainActivityIntent.getStringExtra(Utils.STRING_WORLD_Y_MAX));
		
		try
		{
			if (PViewerActivity.serverSocket != null)
			{
				PViewerActivity.serverSocket.close();
			}
			
			PViewerActivity.serverSocket = new DatagramSocket(SERVER_PORT);
			
			Toast.makeText(this,"UDP server started. Listening on port " + MainActivity.SERVER_PORT,Toast.LENGTH_LONG).show();
		}
		catch (Exception e)
		{
			Toast.makeText(this,"Could not listen on port: " + MainActivity.SERVER_PORT,Toast.LENGTH_LONG).show();
			
			return;
		}
		
		startActivity(intent);
	}
	
	public void settingsEvent(View view)
	{
		Intent intent = new Intent(this,SettingsActivity.class);
		
		startActivity(intent);
	}
}
