package jp.co.crandim.maromap.util;

import com.google.android.maps.GeoPoint;

import android.location.Location;

public class GEOUtil {

	/**
	 * カメラの画角（度）
	 */
	public static int  ANGLE = 40;

	/**
	 * 角度180度の定数値
	 */
	public static int ONE_EIGHTY = 180;

	/**
	 * 角度270度の定数値
	 */
	public static int TWO_SEVENTY = 270;

	/**
	 * 角度360度の定数値
	 */
	public static int THREE_SIXTY = 360;

	/**
	 * Locationの変更時の最小移動距離（ｍ）
	 */
	public static final float MIN_DISTANCE = 1f;

	/**
	 * Locationの変更確認通知時間の最小時間
	 */
	public static final long MIN_MINUTE = 0;

	/**
	 * 実際の距離を画面のX座標系に変換する
	 * 検索範囲を半径とする円が、カメラの画角の範囲の扇形に外接する三角形の底辺の長さを求め
	 * それを、画面の座標系に縮小した値を返す
	 * @param canvasW 画面の幅
	 * @param rudius 検索範囲（半径）
	 * @param distance 変換したい実際の距離（ただし、現在地からターゲットまでのX座標の相対距離）
	 * @return 引数{@link distance}を画面のサイズに変換した値
	 */
	public static double convertToDisplaySizeW(int canvasW, double rudius, double distance) {

		if (distance == 0) {
			return distance;
		}

		final double halfBaseDistance = rudius * Math.tan(Math.toRadians(ANGLE / 2));
		if (halfBaseDistance == 0) {
			return halfBaseDistance;
		}

//		Logger.Error("実際の距離:"+distance);

		final int halfBaseCanvasW = canvasW / 2;

		// 引数distanceが中心からの相対距離のため、画面サイズの半分のサイズを加算する
		double distCanvasW =  (halfBaseCanvasW * distance / halfBaseDistance) + halfBaseCanvasW;

//		Logger.Error("画面上の距離:"+(distCanvasW));
//		Logger.Error("画面の幅:"+(canvasW));

		return distCanvasW;
	}

	/**
	 * 実際の距離を画面のY座標系に変換する
	 * @param canvasH 画面の高さ
	 * @param rudius 検索範囲（半径）
	 * @param distance 変換したい実際の距離（ただし、現在地からターゲットまでのY座標）
	 * @return 引数{@link distance}を画面のサイズに変換した値
	 */
	public static double convertToDisplaySizeH(int canvasH, double rudius, double distance) {

		if (distance == 0 ){
			return distance;
		}

		// 画面の下側からの距離を算出するため、計算結果を画面の高さからマイナスする
		double distCanvasH =  canvasH - (canvasH * distance / rudius);

		return distCanvasH;
	}


	/**
	 * 現在地からターゲットまでのX座標の相対距離を計算する。
	 * 端末が向いている方角と直角に交わる線に対して、ターゲットから直角に線を引いた際のX座標の距離を計算する
	 * @param target 判定したい緯度経度
	 * @param origin 原点とする緯度経度
	 * @param azimuth 端末の向いている方角（東を0度（南に行くほど大きくなる）とした時の角度）
	 * @return 現在地からターゲットまでのX座標の相対距離（原点より左にターゲットがある場合は、「-（マイナス値」を返す）
	 */
	public static double calcDistanceBetweenOriginAndTarget(Location target, Location origin, int azimuth, String provider) {

		// Targetと原点との距離
		double distanceToTarget = origin.distanceTo(target);

		// Targetの方角を計算
		double targetAzimuth = calcTargetAzimuth(target, origin, azimuth, distanceToTarget, provider);

		double distance = 0d;
		double divAzimuth = 0d;


		//...Targetが端末の向いている方角より左にある場合
		if (azimuth >= targetAzimuth) {
			divAzimuth = 90 - (azimuth - targetAzimuth);

			// 相対位置なので、現在地より左のためマイナスする
			distance = - (distanceToTarget * Math.cos(Math.toRadians(divAzimuth)));
		}
		//...Targetが端末の向いている方角より右にある場合
		else {

			int startAzimuth = azimuth - ONE_EIGHTY / 2;
			int endAzimuth = THREE_SIXTY;

			//Targetの方角が、270度以上360度未満の場合
			if (startAzimuth <= targetAzimuth && targetAzimuth < endAzimuth) {
				divAzimuth = 90 - (targetAzimuth - azimuth);
				distance = distanceToTarget * Math.cos(Math.toRadians(divAzimuth));
			}
			//Targetの方角が、360以上（0度）の場合
			else {
				divAzimuth = (90 + azimuth) - (THREE_SIXTY + targetAzimuth);
				distance = distanceToTarget * Math.cos(Math.toRadians(divAzimuth));
			}
		}
		return distance;
	}

	/**
	 * {@link target}が原点{@link origin}、半径{@link radius}、角度{@link azimuth}の扇形内に存在するかを判定する
	 * 円弧の境界も含む
	 * @param target 判定したい緯度経度
	 * @param origin 原点とする緯度経度
	 * @param azimuth 端末の向いている方角（東を0度（南に行くほど大きくなる）とした時の角度）
	 * @param rudius 検索範囲（半径）
	 * @return true:{@link target}が範囲内に存在する
	 */
	public static boolean isInside(Location target, Location origin, int azimuth, double rudius, String provider) {

		// Targetの方角を計算
		double targetAzimuth = calcTargetAzimuth(target, origin, azimuth, rudius, provider);

//		Logger.Debug("端末の方角："+azimuth);
//		Logger.Debug("Targetの方角："+targetAzimuth);
//		Logger.Debug("Bearingの方角："+origin.bearingTo(target));


		//端末の向いている方角が270度未満の場合
		if (azimuth < TWO_SEVENTY) {
			int startAzimuth = azimuth - ONE_EIGHTY / 2;
			int endAzimuth = azimuth + ONE_EIGHTY / 2;


			// 端末の方角が90～270度の場合
			if (startAzimuth >= 0) {
				if (startAzimuth <= targetAzimuth && targetAzimuth <= endAzimuth) {
					return true;
				}
				else {
					return false;
				}
			}
			// 端末の方角が0～90度の場合
			else {
				// 端末の方角が、0～90度の間の場合startAzimuthがマイナス値になるため360度換算に変換
				startAzimuth = startAzimuth + THREE_SIXTY;
				if ((startAzimuth <= targetAzimuth && targetAzimuth < THREE_SIXTY) ||
						(0 <= targetAzimuth && targetAzimuth <= endAzimuth)) {
					return true;
				}
				else {
					return false;
				}
			}
		}
		//端末の向いている方角が270度以上360度未満の場合
		else {
			int startAzimuth = azimuth - ONE_EIGHTY / 2;
			int endAzimuth1 = THREE_SIXTY;
			int endAzimuth2 = (azimuth + ONE_EIGHTY / 2) - THREE_SIXTY;

			//Targetの方角が、270度以上360度未満の場合
			if (startAzimuth <= targetAzimuth && targetAzimuth < endAzimuth1) {
				return true;
			}
			//Targetの方角が、360以上（0度）の場合
			else if (0 <= targetAzimuth && targetAzimuth < endAzimuth2) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	/**
	 * 東を0度とした時の、{@link target}の方角を計算する
	 * @param target 判定したい緯度経度
	 * @param origin 原点とする緯度経度
	 * @param azimuth 端末の向いている方角（東を0度（南に行くほど大きくなる）とした時の角度）
	 * @param rudius 検索範囲（半径）
	 * @return
	 */
	//TODO 重力センサーの向きを考慮していないので、端末の上下を入れ替えると表示される店舗が180度入れ替わる
	//     行く行くは、考慮に入れること
	public static double calcTargetAzimuth(Location target, Location origin, int azimuth, double rudius, String provider) {

		//北0度、東90度、南180度、西-90度としてTargetの方角が返ってくる
		float bearingToTarget = origin.bearingTo(target);

		double targetAzimuth;

		//北を0度とした考え方から、東を0度とした考え方に変換（端末を横にして使用するため）
		//..北西と南西の場合
		if (bearingToTarget < 0) {
			targetAzimuth = bearingToTarget + TWO_SEVENTY;
		}
		//...北東の場合
		else if (0 <= bearingToTarget && bearingToTarget < 90) {
			targetAzimuth = bearingToTarget + TWO_SEVENTY;
		}
		//...南東の場合
		else {
			targetAzimuth = bearingToTarget - 90;
		}

		return targetAzimuth;
	}


	/**
	 * {@link Location}を{@link GeoPoint}に変換する
	 * @param location
	 * @return
	 */
	public static GeoPoint toGeoPoint(Location location) {
		int latitudeE6 = 0;
		int longitudeE6 = 0;

		latitudeE6 = (int)(location.getLatitude() * 1E6);
		longitudeE6 = (int)(location.getLongitude() * 1E6);

		return new GeoPoint(latitudeE6, longitudeE6);
	}

	/**
	 * 引数を{@link GeoPoint}に変換する
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public static GeoPoint toGeoPoint(double latitude, double longitude) {
		int latitudeE6 = 0;
		int longitudeE6 = 0;

		latitudeE6 = (int)(latitude * 1E6);
		longitudeE6 = (int)(longitude * 1E6);

		return new GeoPoint(latitudeE6, longitudeE6);
	}
}
