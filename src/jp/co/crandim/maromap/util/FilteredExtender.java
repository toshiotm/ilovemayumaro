package jp.co.crandim.maromap.util;

/**
 * センサーをフィルタリングする
 *
 */
public class FilteredExtender {


	/**
	 * ハイパスフィルタ
	 */
	public static final FilteredExtender HIGH_PATH_FILTER = new FilteredExtender(0.2);

	/**
	 * ローパスフィルタ
	 */
	public static final FilteredExtender LOW_PATH_FILTER = new FilteredExtender(0.1);



	private double filterFactor;

	private FilteredExtender(double filterFactor) {
		this.filterFactor = filterFactor;
	}

	/**
	 * 入力値にフィルタをかけた値を返します
	 * @param oldValue 前回値
	 * @param newValue 今回値
	 * @return フィルタリング後の値
	 */
	public int filtering(int oldValue, int newValue) {

		//...0度と360度の境目の場合極端に値が変わるため平坦化しない
		if (!(Math.abs(newValue - oldValue) >= 300)) {
			oldValue = new Double(newValue * filterFactor + oldValue * (1- filterFactor)).intValue();
		}
		else {
			oldValue = newValue;
		}

		return oldValue;
	}
}
