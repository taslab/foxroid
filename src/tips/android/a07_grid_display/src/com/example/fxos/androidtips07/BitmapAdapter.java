
package com.example.fxos.androidtips07;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class BitmapAdapter extends ArrayAdapter<Integer> {

    private int resourceId;

    public BitmapAdapter(Context context, int resource, List<Integer> integerArray) {
        super(context, resource, integerArray);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceId, null);
        }

        ImageView view = (ImageView)convertView;
        view.setImageResource(getItem(position));

        return view;
    }
}
