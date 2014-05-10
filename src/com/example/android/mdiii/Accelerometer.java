/**
 * 
 */
package com.example.android.mdiii;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * @author Anna
 *
 */
public class Accelerometer implements SensorEventListener {

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private float [] mAccel;
	private float[] linear_acceleration;
	private float[] gravity;	
	
	
	
	public Accelerometer(Context context) {
		gravity = new float[3];
		linear_acceleration = new float[3];
		mAccel = new float[3];
		
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		  // In this example, alpha is calculated as t / (t + dT),
		  // where t is the low-pass filter's time-constant and
		  // dT is the event delivery rate.

		  final float alpha = 0.8f;

		// Isolate the force of gravity with the low-pass filter.
		  gravity[0] = alpha * gravity[0] /* previous X gravity */ + (1 - alpha) * event.values[0];
		  gravity[1] = alpha * gravity[1] /* previous Y gravity */ + (1 - alpha) * event.values[1];
		  gravity[2] = alpha * gravity[2] /* previous Y gravity */ + (1 - alpha) * event.values[2];

		  // Remove the gravity contribution with the high-pass filter.
		  linear_acceleration[0] = event.values[0] - gravity[0];
		  linear_acceleration[1] = event.values[1] - gravity[1];
		  linear_acceleration[2] = event.values[2] - gravity[2];
	}
}
