package com.example.pviewer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity
{
	private static float worldXMin, worldXMax, worldYMin, worldYMax;
	private static int visualizationFrequency;
	
	public void doneEvent(View view)
	{
		try
		{
			EditText visualizationFrequencyEditText, worldXMinEditText, worldXMaxEditText, worldYMinEditText, worldYMaxEditText;
			
			visualizationFrequencyEditText = (EditText) findViewById(R.id.visualization_frequency);
			worldXMinEditText = (EditText) findViewById(R.id.world_x_min);
			worldXMaxEditText = (EditText) findViewById(R.id.world_x_max);
			worldYMinEditText = (EditText) findViewById(R.id.world_y_min);
			worldYMaxEditText = (EditText) findViewById(R.id.world_y_max);
			
			if (Integer.parseInt(visualizationFrequencyEditText.getText().toString()) <= 0)
			{
				Toast.makeText(this,"The visualization frequency must be greater than 0",Toast.LENGTH_LONG).show();
				
				return;
			}
			
			visualizationFrequency = Integer.parseInt(visualizationFrequencyEditText.getText().toString());
			
			if (Math.abs(Float.parseFloat(worldXMinEditText.getText().toString())) > 100.0f)
			{
				Toast.makeText(this,"World x min must be within [-100.0,100.0]",Toast.LENGTH_LONG).show();
				
				return;
			}
			
			worldXMin = Float.parseFloat(worldXMinEditText.getText().toString());
			
			if (Math.abs(Float.parseFloat(worldXMaxEditText.getText().toString())) > 100.0f)
			{
				Toast.makeText(this,"World x max must be within [-100.0,100.0]",Toast.LENGTH_LONG).show();
				
				return;
			}
			
			worldXMax = Float.parseFloat(worldXMaxEditText.getText().toString());
			
			if (Math.abs(Float.parseFloat(worldYMinEditText.getText().toString())) > 100.0f)
			{
				Toast.makeText(this,"World y min must be within [-100.0,100.0]",Toast.LENGTH_LONG).show();
				
				return;
			}
			
			worldYMin = Float.parseFloat(worldYMinEditText.getText().toString());
			
			if (Math.abs(Float.parseFloat(worldYMaxEditText.getText().toString())) > 100.0f)
			{
				Toast.makeText(this,"World y max must be within [-100.0,100.0]",Toast.LENGTH_LONG).show();
				
				return;
			}
			
			worldYMax = Float.parseFloat(worldYMaxEditText.getText().toString());
			
			if (worldXMin >= worldXMax)
			{
				Toast.makeText(this,"World x min must be lower than world x max",Toast.LENGTH_LONG).show();
				
				return;
			}
			
			if (worldYMin >= worldYMax)
			{
				Toast.makeText(this,"World y min must be lower than world y max",Toast.LENGTH_LONG).show();
				
				return;
			}
			
			Intent intent = new Intent(this,MainActivity.class);
			
			intent.putExtra(Utils.STRING_VISUALIZATION_FREQUENCY,"" + visualizationFrequency);
			intent.putExtra(Utils.STRING_WORLD_X_MIN,"" + worldXMin);
			intent.putExtra(Utils.STRING_WORLD_X_MAX,"" + worldXMax);
			intent.putExtra(Utils.STRING_WORLD_Y_MIN,"" + worldYMin);
			intent.putExtra(Utils.STRING_WORLD_Y_MAX,"" + worldYMax);
			
			startActivity(intent);
		}
		catch (Exception e) {;}
	}
	
	public void onBackPressed()
	{
		Intent intent = new Intent(this,MainActivity.class);
		
		if (visualizationFrequency != 0)
		{
			intent.putExtra(Utils.STRING_VISUALIZATION_FREQUENCY,"" + visualizationFrequency);
			intent.putExtra(Utils.STRING_WORLD_X_MIN,"" + worldXMin);
			intent.putExtra(Utils.STRING_WORLD_X_MAX,"" + worldXMax);
			intent.putExtra(Utils.STRING_WORLD_Y_MIN,"" + worldYMin);
			intent.putExtra(Utils.STRING_WORLD_Y_MAX,"" + worldYMax);
		}
		
		startActivity(intent);
	}
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_settings);
		
		if (visualizationFrequency == 0)
		{
			EditText visualizationFrequencyEditText, worldXMinEditText, worldXMaxEditText, worldYMinEditText, worldYMaxEditText;
			
			visualizationFrequencyEditText = (EditText) findViewById(R.id.visualization_frequency);
			worldXMinEditText = (EditText) findViewById(R.id.world_x_min);
			worldXMaxEditText = (EditText) findViewById(R.id.world_x_max);
			worldYMinEditText = (EditText) findViewById(R.id.world_y_min);
			worldYMaxEditText = (EditText) findViewById(R.id.world_y_max);
			
			visualizationFrequency = Integer.parseInt(visualizationFrequencyEditText.getText().toString());
			worldXMin = Float.parseFloat(worldXMinEditText.getText().toString());
			worldXMax = Float.parseFloat(worldXMaxEditText.getText().toString());
			worldYMin = Float.parseFloat(worldYMinEditText.getText().toString());
			worldYMax = Float.parseFloat(worldYMaxEditText.getText().toString());
		}
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	protected void onResume()
	{
		super.onResume();
		
		try
		{
			if (visualizationFrequency != 0)
			{
				EditText visualizationFrequencyEditText, worldXMinEditText, worldXMaxEditText, worldYMinEditText, worldYMaxEditText;
				
				visualizationFrequencyEditText = (EditText) findViewById(R.id.visualization_frequency);
				worldXMinEditText = (EditText) findViewById(R.id.world_x_min);
				worldXMaxEditText = (EditText) findViewById(R.id.world_x_max);
				worldYMinEditText = (EditText) findViewById(R.id.world_y_min);
				worldYMaxEditText = (EditText) findViewById(R.id.world_y_max);
				
				visualizationFrequencyEditText.setText("" + visualizationFrequency);
				worldXMinEditText.setText("" + worldXMin);
				worldXMaxEditText.setText("" + worldXMax);
				worldYMinEditText.setText("" + worldYMin);
				worldYMaxEditText.setText("" + worldYMax);
			}
		}
		catch (Exception e) {;}
	}
}
