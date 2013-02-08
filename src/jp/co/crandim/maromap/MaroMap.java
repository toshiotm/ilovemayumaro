package jp.co.crandim.maromap;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MaroMap extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maro_map);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_maro_map, menu);
        return true;
    }
}
