
package com.example.fxos.androidtips07;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private GridView mGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<Integer> gridList = new ArrayList<Integer>();
        for (int i = 0; i < 50; i++) {
            gridList.add(R.drawable.ic_launcher);
        }

        // set adapter
        BitmapAdapter adapter = new BitmapAdapter(this, R.layout.grid_item, gridList);
        mGrid = (GridView)findViewById(R.id.grid);
        mGrid.setAdapter(adapter);
    }
}
