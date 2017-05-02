package com.mccc.bodyw;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

import static com.mccc.bodyw.AttributeContentProvider.CONTENT_URI;

public class InputActivity extends AppCompatActivity {

    public static int sYear, sMonth, sDay;

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, sYear, sMonth - 1, sDay);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            sYear = year;
            sMonth = month + 1;
            sDay = day;

            ((InputActivity) getActivity()).setDate(sYear, sMonth, sDay);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        choseToday(null);
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
        setDate(sYear, sMonth, sDay);
    }

    public void setDate(int year, int month, int day) {
        EditText et = (EditText) findViewById(R.id.input_date);
        et.setText(year + "/" + month + "/" + day);
    }

    public void onOk(View view) {
        ContentValues values = new ContentValues();
        values.put(MainDatabaseHelper.RecordEntry.COL_DATE, Integer.parseInt(String.format("%4d%02d%02d", sYear, sMonth, sDay)));
        values.put(MainDatabaseHelper.RecordEntry.COL_WEIGHT, Integer.parseInt(((EditText) findViewById(R.id.input_weight)).getText().toString()));
        values.put(MainDatabaseHelper.RecordEntry.COL_BODY_FAT, Integer.parseInt(((EditText) findViewById(R.id.input_body_fat)).getText().toString()));
        getContentResolver().insert(CONTENT_URI, values);
        finish();
    }

    public void onCancel(View view) {
        finish();
    }
}
