package jp.co.crandim.maromap;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;

import com.google.android.maps.MapView;

public class MaroMapView extends MapView implements SensorEventListener {


	private int roll;

	public MaroMapView(Context context, String ApiKey) {
		super(context, ApiKey);
	}

	public MaroMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	public MaroMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	//
	// SenserEventListenerのMethod
	//
	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {
		// TODO 自動生成されたメソッド・スタブ

	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			// Y軸の回転角
			this.roll = (int)event.values[2];
		}
	}

}
