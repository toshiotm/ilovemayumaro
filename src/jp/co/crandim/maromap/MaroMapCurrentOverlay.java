package jp.co.crandim.maromap;

import java.util.ArrayList;
import java.util.List;

import jp.co.crandim.maromap.util.FilteredExtender;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MaroMapCurrentOverlay extends ItemizedOverlay<OverlayItem> implements SensorEventListener {

	private List<OverlayItem> items = new ArrayList<OverlayItem>();

	private final MaroMapActivity mapActivity;

	/**
	 * 端末の傾き
	 */
	private int roll;

	/**
	 * 端末の方角
	 */
	private int azimuth;

	/**
	 * 端末の向き
	 * {@link Configuration.ORIENTATION_LANDSCAPE}横長
	 * {@link Configuration.ORIENTATION_PORTRATE}縦長
	 * 初期値は、横長
	 */
	private int orientation = Configuration.ORIENTATION_PORTRAIT;

	/**
	 * コンストラクタ
	 * @param drawable
	 */
	public MaroMapCurrentOverlay(Drawable drawable, MaroMapActivity mapActivity) {
		super(drawable);

		this.mapActivity = mapActivity;

		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return items.get(i);
	}
	@Override
	public int size() {
		return items.size();
	}


	public void addItem(OverlayItem item) {
		items.add(item);
		populate();
	}


	public void clear() {
		items.clear();
		populate();
	}

	public static void boundCenterItem(Drawable drawable , OverlayItem item) {
		item.setMarker(boundCenter(drawable));
	}

	protected boolean onTap(int index) {
			return true;
	}

	/**
	 * 端末の向き（横長、縦長）をセット
	 * @param orientation
	 */
	public void setConfigurationOrientation(int orientation) {
		this.orientation = orientation;
	}

	//
	// SensorEventListenerのMethod
	//
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO 自動生成されたメソッド・スタブ

	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {

			// 方角を取得
			int oldAzimuth = this.azimuth;
			int newAzimuth = (int)event.values[0];

			// 端末の向きが縦長の場合
			if (Configuration.ORIENTATION_PORTRAIT == this.orientation) {
				if (90 <= newAzimuth && newAzimuth < 360) {
					newAzimuth = newAzimuth - 90;
				}
				else {
					newAzimuth = newAzimuth + 270;
				}
			}

			oldAzimuth = FilteredExtender.HIGH_PATH_FILTER.filtering(oldAzimuth, newAzimuth);
			if (this.azimuth != oldAzimuth) {
				MapView mapView = (MapView)this.mapActivity.findViewById(R.id.mapview);
				mapView.invalidate();
			}

			this.azimuth = oldAzimuth;
		}
	}

}
