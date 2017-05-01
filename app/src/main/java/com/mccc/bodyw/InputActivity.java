package com.mccc.bodyw;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class InputActivity extends AppCompatActivity {

    public static int sYear, sMonth, sDay;

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, sYear, sMonth, sDay);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            sYear = year;
            sMonth = month;
            sDay = day;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        choseToday(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setDate(sYear, sMonth, sDay);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void choseToday(View v) {
        final Calendar c = Calendar.getInstance();
        sYear = c.get(Calendar.YEAR);
        sMonth = c.get(Calendar.MONTH) + 1;
        sDay = c.get(Calendar.DAY_OF_MONTH);
    }

    public void setDate(int year, int month, int day) {
        EditText et = (EditText) findViewById(R.id.input_date);
        et.setText(year + "/" + month + "/" + day);
    }
}
