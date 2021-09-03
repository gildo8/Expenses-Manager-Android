package com.example.gil.expensesmanager.model;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Gil on 17/05/2016.
 */
public class DateEditText extends EditText{
    int tYear;
    int tMonth;
    int tDay;
    OnDateSetListener onDateSetListener;

    public interface OnDateSetListener{
        public void  dateSet(int year,int month,int day);
    }

    public void init(){
        Calendar calendar = new GregorianCalendar();
        tYear = calendar.get(Calendar.YEAR);
        tMonth = calendar.get(Calendar.MONTH);
        tDay = calendar.get(Calendar.DAY_OF_MONTH);
        setText("" + tDay + "/" + (1+tMonth) + "/" + tYear);
    }


    public DateEditText(Context context) {
        super(context);
        init();
    }

    public DateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DateEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void setOnDateSetListener(OnDateSetListener listener) {
        this.onDateSetListener = listener;
    }

    public int gettYear() {
        return tYear;
    }

    public void settYear(int tYear) {
        this.tYear = tYear;
    }

    public int gettMonth() {
        return tMonth;
    }

    public void settMonth(int tMonth) {
        this.tMonth = tMonth;
    }

    public int gettDay() {
        return tDay;
    }

    public void settDay(int tDay) {
        this.tDay = tDay;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            InnerDatePicker datePicker = new InnerDatePicker();
            datePicker.init(tYear, tMonth, tDay);
            datePicker.setListener(new InnerDatePicker.Listener() {
                @Override
                public void done(int year, int month, int day) {
                    DateEditText.this.tYear = year;
                    DateEditText.this.tMonth = month;
                    DateEditText.this.tDay = day;
                    setText("" + day + "/" + (++month) + "/" + year);
                    if (onDateSetListener != null) {
                        onDateSetListener.dateSet(year, month, day);
                    }
                }
            });
            datePicker.show(((Activity) getContext()).getFragmentManager(), "DateChanged");
        }
        return super.onTouchEvent(event);
    }

    public static class InnerDatePicker extends DialogFragment {
        int year;
        int month;
        int day;

        public void init(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        interface Listener{
            public void done(int year, int month, int day);
        }

        Listener listener;

        public void setListener(Listener listener){
            this.listener = listener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Log.d("TA", "date set" + year + "/" + monthOfYear + "/" + dayOfMonth);
                    listener.done(year,monthOfYear,dayOfMonth);
                }
            },year,month,day);

            return dialog;
        }
    }
}
