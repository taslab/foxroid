
package com.example.fxos.androidtips14;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter<String> array = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(
                        R.array.list_high_speed_scroll_sample));
        setListAdapter(array);

        // set this if you want to show fast scroll
        getListView().setFastScrollEnabled(true);
    }
}
