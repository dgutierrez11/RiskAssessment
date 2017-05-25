package com.example.leoguti.riskassessment.vista;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by leoguti on 10/05/2017.
 */

public class DateTimePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Calendar dateSelected = Calendar.getInstance();

    public interface DateTimePickerInterface{
        void onSelectedDateTime(String dateTimeSelected);
    }

    private DateTimePickerInterface mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if(context instanceof DateTimePickerInterface) {
                mListener = (DateTimePickerInterface) context;
            }
        }catch (ClassCastException e){
            throw new ClassCastException("must implements onSelectedDateTime");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Se setea la fecha escogida
        dateSelected.set(Calendar.YEAR, year);
        dateSelected.set(Calendar.MONTH, month+1);
        dateSelected.set(Calendar.DATE, dayOfMonth);
        initTimeDialog();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //Se setea la hora escogida
        dateSelected.set(Calendar.HOUR_OF_DAY, hourOfDay);
        dateSelected.set(Calendar.MINUTE, minute);

        sendDateTimeToActivity();
    }

    private void initTimeDialog(){
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
        timePickerDialog.show();
    }

    private void sendDateTimeToActivity(){
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        this.mListener.onSelectedDateTime(df.format("yyyy-MM-dd hh:mm a", dateSelected.getTime()).toString());
    }
}
