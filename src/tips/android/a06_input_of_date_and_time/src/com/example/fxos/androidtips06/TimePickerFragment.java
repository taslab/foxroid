
package com.example.fxos.androidtips06;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class TimePickerFragment extends DialogFragment {
    public static final String TAG = TimePickerFragment.class.getSimpleName();

    public static final String ARGS_HOUR = "hour";

    public static final String ARGS_MINUTE = "minute";

    private int hour;

    private int minute;

    OnTimeSetListener onTimeSet;

    public TimePickerFragment() {
    }

    public void setCallBack(OnTimeSetListener onTime) {
        onTimeSet = onTime;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        // set parameter
        hour = args.getInt(ARGS_HOUR);
        minute = args.getInt(ARGS_MINUTE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), onTimeSet, hour, minute, false);
    }

}
