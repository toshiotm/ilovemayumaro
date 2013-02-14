package jp.co.crandim.maromap;

import java.util.List;

import jp.co.crandim.maromap.util.FilteredExtender;
import jp.co.crandim.maromap.util.GEOUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * 地図表示用Activityクラス
 *
 */
public class MaroMapActivity extends MapActivity  implements SensorEventListener, LocationListener, ISearchCallback {


	private SensorManager sensorMng;
	private LocationManager gpsManager;
	private LocationManager networkManager;
	private MapView mapView;
	private MaroMapCurrentOverlay mapCurrentOverlay;
	private MaroMapContentsOverlay mapContentsOverlay;
	/**
	 * Y軸方向の現在の端末の傾き
	 */
	private int roll;

	/**
	 * HttpRequestの検索結果をUIに通知するためのHandler
	 */
	private Handler requestHandler;

    /**
     * メッセージ表示用のTextView
     */
    private TextView msgText;

    /**
     * 現在地
     */
    private GeoPoint currentGeoPoint;


	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.map);


        // Senserを取得
        //TODO Senserが使用可能かどうか確認する
        this.sensorMng = (SensorManager) getSystemService(SENSOR_SERVICE);

        // LocationManagerでGPSの値を取得するための設定
        //TODO LocationでGPSかNETWORKが使用できるかどうか確認する。できなければ、メッセージを出して終了する
        this.gpsManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.networkManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Handlerを生成
        this.requestHandler = new Handler();

        //Viewを生成
        //...MapViewを生成
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        //...初回のZOOMレベルを設定
        mapView.getController().setZoom(mapView.getMaxZoomLevel()-2);

// 2013/02/13 とりあえず今はコメント        
//        //..コンテンツ表示用のOverlayを生成
//        Drawable beer = getResources().getDrawable(R.drawable.main_super_light_);
//        beer.setBounds(0, 0, beer.getIntrinsicWidth(), beer.getIntrinsicHeight());
//        mapContentsOverlay = new MaroMapContentsOverlay(beer, this);
//        mapView.getOverlays().add(mapContentsOverlay);

        //...現在地表示用のOverlayを生成
        Drawable current = getResources().getDrawable(android.R.drawable.arrow_up_float);
        current.setBounds(0, 0, current.getIntrinsicWidth(), current.getIntrinsicHeight());
        mapCurrentOverlay = new MaroMapCurrentOverlay(current, this);
        mapView.getOverlays().add(mapCurrentOverlay);

// 2013/02/11　とりあえず今はコメント
//        //...呼び出し元から渡された現在地を元に現在地アイコンを表示
//		final Intent intent = getIntent();
//		final double[] currentLocation = intent.getDoubleArrayExtra(TelegnosisExtrasKeys.CUREENT_POSITION.getKey());
//		if (currentLocation != null && currentLocation.length != 0) {
//			this.currentGeoPoint = new GeoPoint((int)(currentLocation[0] * 1E6), (int)(currentLocation[1] * 1E6));
//		}

        //...メッセージ表示用のテキストを登録
        msgText = new TextView(this);
        msgText.setTextColor(Color.BLACK);
        msgText.setBackgroundColor(Color.argb(100, 150, 150, 150));
        msgText.setText(getResources().getString(R.string.search_gps));

        LinearLayout searchCndBtnView = new LinearLayout(this);
        searchCndBtnView.setGravity(Gravity.RIGHT);
        addContentView(searchCndBtnView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        searchCndBtnView.addView(msgText, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}


	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}


    /**
     * Activity起動時に必要な全ての処理をまとめて行う
     */
    private void startAllAction() {
    	
		// LocationManagerへの登録処理
		startLocation();

		// センサーへの登録処理
		startSensor();
    }


    /**
     * Activity停止時に必要な全ての処理をまとめて行う
     */
    private void stopAllAction() {

		// センサーの解除処理
		stopSensor();

	    // LocationManagerの解除処理
	    stopLocation();

    }

    /**
     * LocationManagerへの登録処理
     */
    private void startLocation() {
		// LocatioManagerへの登録処理
		gpsManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GEOUtil.MIN_MINUTE, GEOUtil.MIN_DISTANCE, this);
		networkManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GEOUtil.MIN_MINUTE, GEOUtil.MIN_DISTANCE, this);

    }

    /**
     * LocationManagerへ停止処理
     */
    private void stopLocation() {
    	gpsManager.removeUpdates(this);
	    networkManager.removeUpdates(this);

    }

    /**
     * Sensorへの登録処理
     */
    private void startSensor() {
    	 List<Sensor> sensors = sensorMng.getSensorList(Sensor.TYPE_ORIENTATION);
    	 Sensor sensor = sensors.get(0);
	     sensorMng.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
	     sensorMng.registerListener(mapCurrentOverlay, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Sensorへ停止処理
     */
    private void stopSensor() {
        sensorMng.unregisterListener(this);
        sensorMng.unregisterListener(mapCurrentOverlay);
    }

    @Override
    protected void onStart() {
    	super.onStart();

    }


	@Override
	protected void onResume() {

		startAllAction();

		super.onResume();

		this.mapCurrentOverlay.setConfigurationOrientation(this.getResources().getConfiguration().orientation);


		// GPS機能がONかどうかを確認する
		boolean isProviderEnable = gpsManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!isProviderEnable) {
			isProviderEnable = networkManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (!isProviderEnable) {
				// GPS機能がOFFになっている
				setMessageText(getResources().getString(R.string.search_err_system_setup));
			}
			else {
				// 画面に状態を表示
				setMessageText(getResources().getString(R.string.search_gps));
			}
		}

		// 前回取得したGPS情報があれば、画面に表示する
		//...まずは、GPSプロバイダより取得
		Location lastLocation = gpsManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastLocation == null) {

			//...だめならNETWORK_PROVIDERより取得
			if (lastLocation == null) {
				lastLocation = networkManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
		}

		//...取得できたなら現在地を表示して検索する
		if (lastLocation != null) {

			//...現在地として保持
			this.currentGeoPoint = GEOUtil.toGeoPoint(lastLocation);

			double latitude = lastLocation.getLatitude();
			double longitude = lastLocation.getLongitude();

			//...現在地情報を反映
			reflectCurrentPlace(latitude, longitude);
		}
		//...取得できない場合、メッセージを表示
		else {
			// 画面に最後に取得したGPSで表示していることを通知
			Toast.makeText(this, getResources().getString(R.string.search_gpserror), Toast.LENGTH_SHORT).show();
		}

        new Handler().post(new Runnable(){

        	@Override
        	public void run() {

        		if (currentGeoPoint != null) {
        			mapView.getController().animateTo(currentGeoPoint);
        			mapView.invalidate();
        		}
        	}
        });

	}

	@Override
	protected void onPause() {
		stopAllAction();

		super.onPause();
	}

	@Override
	protected void onStop() {

		stopAllAction();

		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	}


	/**
	 * 現在地情報を画面に反映する
	 * @param latitude
	 * @param longitude
	 */
	private void reflectCurrentPlace(double latitude, double longitude) {

		if (latitude == 0d && longitude == 0d)  {
			setMessageText(getResources().getString(R.string.search_gpserror));
		}


		final int latitudeE6 = (int)(latitude * 1E6);
		final int longetudeE6 = (int)(longitude * 1E6);
		currentGeoPoint = new GeoPoint(latitudeE6, longetudeE6);
		Point currentPtPx = new Point();
		mapView.getProjection().toPixels(currentGeoPoint, currentPtPx);

		//...現在地アイコンを表示
		final OverlayItem item = new OverlayItem(currentGeoPoint, "現在地", "現在地");
		mapCurrentOverlay.clear();
		mapCurrentOverlay.addItem(item);

		mapView.invalidate();
	}


	/**
	 * 検索結果を画面に反映する
	 * @param infos コンテンツ一覧
	 */
	private void reflectSearchResult( List<ContentsInfo> infos) {

		// 古いデータをクリア
		mapContentsOverlay.clear();

		GeoPoint pointGeo = null;

		for (ContentsInfo info : infos) {

			pointGeo = GEOUtil.toGeoPoint(info.getLatitude(), info.getLongitude());

			//...現在地アイコンを表示
			final OverlayItem item = new OverlayItem(pointGeo, info.getExtra_url(), info.getName());
			mapContentsOverlay.addItem(item);
		}
		mapView.invalidate();
	}

	/**
	 * メッセージを画面に表示
	 * @param msg メッセージ
	 */
	private void setMessageText(String msg) {

		msgText.setText(msg);
		msgText.invalidate();
	}


	// ISearchCallbackのMethod
	/**
	 * WebAPIにて検索状態を受け取る
	 * @param message 検索エンジンからの状態を表すメッセージ
	 */
	public void reciveStatus(final String message) {

		this.requestHandler.post(new Runnable(){
			@Override
			public void run() {

				setMessageText(message);
			}

		});
	};

	//　API呼び出しと違って、すでにSQLiteでデータを持っているので、onResumeの時にでも呼ばれればよい
	/**
	 * WebAPIにて検索された結果を受け取る
	 * @param result {@code List<ContentsInfo>}で検索結果を格納
	 */
	@Override
	public void receiveSearchResult(final String resultCode) {
//TOOD SQLiteから観光情報を取得する
//		// 検索結果を取得
//		final List<ContentsInfo> infos = ContentsStrage.getInstance().getContentsInfo();
//
//		this.requestHandler.post(new Runnable(){
//			@Override
//			public void run() {
//
//				//表示状態を検索結果に応じて更新
//				setMessageText("");
//
//				if (resultCode == ISearchCallback.SEARCH_RESULT_SEARCHING) {
//					setMessageText(getResources().getString(R.string.search_progress));
//					return;
//				}
//				else if (resultCode == ISearchCallback.SEARCH_RESULT_ERR) {
//					setMessageText(getResources().getString(R.string.search_err));
//					return;
//				}
//				else {
//					setMessageText(String.format(getResources().getString(R.string.search_result), infos.size()));
//				}
//
//				if (!isFinishing()) {
//					//検索結果を表示に反映する
//					reflectSearchResult(infos);
//				}
//			}
//
//		});
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
			//...端末の傾きについての処理
			int oldRoll = this.roll;
			int newRoll = (int)event.values[2];
			oldRoll = FilteredExtender.HIGH_PATH_FILTER.filtering(oldRoll, newRoll);

			this.roll = oldRoll;
		}
	}

	//
	//LocationListenerのMethod
	//
	@Override
	public void onLocationChanged(Location location) {

		double latitude = location.getLatitude();
		double longitude = location.getLongitude();

//　TODO 画面に表示する観光地の情報を絞り込むならここで行う。
//		if (latitude == 0d && longitude == 0d) {
//			setMessageText(getResources().getString(R.string.search_gpserror));
//		}
//		else {
//			//...検索を行う
//			MaroSearchEngine.getInstance().search(latitude, longitude);
//		}

		// 現在地情報を反映
		reflectCurrentPlace(latitude, longitude);
	}

	@Override
	public void onProviderDisabled(String s) {
		// TODO 自動生成されたメソッド・スタブ

	}
	@Override
	public void onProviderEnabled(String s) {
		// TODO 自動生成されたメソッド・スタブ

	}
	@Override
	public void onStatusChanged(String s, int i, Bundle bundle) {
		// TODO 自動生成されたメソッド・スタブ
	}

}
