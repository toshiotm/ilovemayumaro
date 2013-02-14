package jp.co.crandim.maromap;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MaroMapContentsOverlay extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> items = new ArrayList<OverlayItem>();

	private final MaroMapActivity mapActivity;

	/**
	 * テキスト表示用のPaint
	 */
	private Paint textPaint;

	/**
	 * コンストラクタ
	 * @param drawable
	 */
	public MaroMapContentsOverlay(Drawable drawable, MaroMapActivity mapActivity) {
		super(drawable);

		this.mapActivity = mapActivity;

		textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(12f);

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
}
