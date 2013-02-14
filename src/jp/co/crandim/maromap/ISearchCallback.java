package jp.co.crandim.maromap;



/**
 * 検索結果を受け取るためのCallbackインタフェース
 *
 */
public interface ISearchCallback {

	/**
	 * 検索中
	 */
	public static final String SEARCH_RESULT_SEARCHING= "SEARCHING";

	/**
	 * 検索エラー
	 */
	public static final String SEARCH_RESULT_ERR = "SEARCH_RESULT_ERR";

	/**
	 * 検索成功
	 */
	public static final String SEARCH_RESULT_OK = "SEARCH_RESULT_OK";

	void receiveSearchResult(String resultCode);
}
