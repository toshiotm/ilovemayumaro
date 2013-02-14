package jp.co.crandim.maromap;


/**
 * 検索結果のコンテンツ情報クラス
 * このクラス一つにつき、Viewに一つのマーカーが表示される
 *
 */
public class ContentsInfo {

	/**
	 * 外部Webサイトに接続するためのURL情報
	 */
	public String extra_url;


	/**
	 * 緯度情報
	 */
	private double latitude;

	/**
	 * 経度情報
	 */
	private double longitude;

	/**
	 * Viewに表示するマーカーイメージ
	 * R.drawableの値をセットする
	 */
	private int contentsImage;

	/**
	 * 観光地名
	 */
	private String name;

	/**
	 * 外部Webサイトに接続するためのURL情報を取得
	 * @return
	 */
	public String getExtra_url() {
		return extra_url;
	}

	/**
	 * 外部Webサイトに接続するためのURL情報をセット
	 * @param extra_url
	 */
	public void setExtra_url(String extra_url) {
		this.extra_url = extra_url;
	}

	/**
	 * 画像01のURL
	 */
	private String imageUrl01;

	/**
	 * 緯度情報を取得
	 * @return 緯度
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * 緯度情報をセット
	 * @param latitude 緯度
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * 経度情報を取得
	 * @return 経度
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * 経度情報をセット
	 * @param longitude 経度
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * コンテンツマーカーイメージを取得
	 * @return R.drawableの値
	 */
	public int getContentsImage() {
		return contentsImage;
	}

	/**
	 * コンテンツマーカーイメージを設定
	 * @param contentsImage R.drawableの値
	 */
	public void setContentsImage(int contentsImage) {
		this.contentsImage = contentsImage;
	}

	/**
	 * 画像URL01を取得
	 * @return 画像01
	 */
	public String getImageUrl01() {
		return imageUrl01;
	}

	/**
	 * 画像URL01をセット
	 * @param image01 画像01
	 */
	public void setImageUrl01(String imageUrl01) {
		this.imageUrl01 = imageUrl01;
	}
	
	/**
	 * 観光地名称を取得
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 観光地名称をセット
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
}
