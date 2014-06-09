
package com.example.fxos.androidtips06;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends FragmentActivity {

    private TextView mTextDateTime;

    private int mYear;

    private int mMonth;

    private int mDay;

    private int mHour;

    private int mMinute;

    OnDateSetListener mOndateSetListener = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // set input value
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;

            // update text label
            updateDateAndTimeInfo();
        }
    };

    OnTimeSetListener mOnTimeSetListener = new OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // set input value
            mHour = hourOfDay;
            mMinute = minute;

            // update text label
            updateDateAndTimeInfo();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextDateTime = (TextView)findViewById(R.id.textDateTime);

        // set current date and time
        initializeValue();

        // set value for text label
        updateDateAndTimeInfo();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonInputDate:
                // input date
                showDatePicker();
                break;
            case R.id.buttonInputTime:
                // input time
                showTimePicker();
                break;
        }
    }

    private void initializeValue() {
        // get current date and time
        Calendar calender = Calendar.getInstance();

        mYear = calender.get(Calendar.YEAR);
        mMonth = calender.get(Calendar.MONTH);
        mDay = calender.get(Calendar.DAY_OF_MONTH);
        mHour = calender.get(Calendar.HOUR_OF_DAY);
        mMinute = calender.get(Calendar.MINUTE);
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();

        // set parameter
        Bundle args = new Bundle();
        args.putInt(DatePickerFragment.ARGS_YEAR, mYear);
        args.putInt(DatePickerFragment.ARGS_MONTH, mMonth);
        args.putInt(DatePickerFragment.ARGS_DAY, mDay);
        date.setArguments(args);

        // if set button pushed callback method will call.
        date.setCallBack(mOndateSetListener);
        date.show(getSupportFragmentManager(), DatePickerFragment.TAG);
    }

    private void showTimePicker() {
        TimePickerFragment time = new TimePickerFragment();

        // set parameter
        Bundle args = new Bundle();
        args.putInt(TimePickerFragment.ARGS_HOUR, mHour);
        args.putInt(TimePickerFragment.ARGS_MINUTE, mMinute);
        time.setArguments(args);

        // if set button pushed callback method will call.
        time.setCallBack(mOnTimeSetListener);
        time.show(getSupportFragmentManager(), TimePickerFragment.TAG);
    }

    private void updateDateAndTimeInfo() {
        // set text label
        mTextDateTime.setText(mYear + String.format("-%1$02d", (mMonth + 1))
                + String.format("-%1$02d", mDay) + String.format(" %1$02d", mHour)
                + String.format(":%1$02d", mMinute));
    }

}
