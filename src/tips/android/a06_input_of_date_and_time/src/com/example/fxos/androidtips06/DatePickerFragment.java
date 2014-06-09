
package com.example.fxos.androidtips06;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {
    public static final String TAG = DatePickerFragment.class.getSimpleName();

    public static final String ARGS_YEAR = "year";

    public static final String ARGS_MONTH = "month";

    public static final String ARGS_DAY = "day";

    OnDateSetListener onDateSet;

    public DatePickerFragment() {
    }

    public void setCallBack(OnDateSetListener onDate) {
        onDateSet = onDate;
    }

    private int year, month, day;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        // set parameter
        year = args.getInt(ARGS_YEAR);
        month = args.getInt(ARGS_MONTH);
        day = args.getInt(ARGS_DAY);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), onDateSet, year, month, day);
    }

}
