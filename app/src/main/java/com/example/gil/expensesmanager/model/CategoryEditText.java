package com.example.gil.expensesmanager.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;

import com.example.gil.expensesmanager.R;

/**
 * Created by Gil on 17/05/2016.
 */
public class CategoryEditText extends EditText{

    String[] categoryList = {"Health","Food And Drink","Leisure","Transportation","Other"};
    private String choice = null;
    OnCategorySetListener onCategorySetListener;

    public interface OnCategorySetListener{
        public void  categorySet(String choice);
    }

    public void init(){
//        setText("" + tDay + "/" + (1+tMonth) + "/" + tYear);
    }


    public CategoryEditText(Context context) {
        super(context);
        init();
    }

    public CategoryEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CategoryEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    public void setOnCategorySetListener(OnCategorySetListener listener) {
        this.onCategorySetListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            InnerCategoryPicker categoryPicker = new InnerCategoryPicker();
            categoryPicker.init(categoryList);
            categoryPicker.setListener(new InnerCategoryPicker.Listener() {
                @Override
                public void done(String choice) {
                    setChoice(choice);
                    setText(getChoice());
                    if (onCategorySetListener != null) {
                        onCategorySetListener.categorySet(choice);
                    }
                }
            });
            categoryPicker.show(((Activity) getContext()).getFragmentManager(), "CategoryChanged");
        }
        return super.onTouchEvent(event);
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public static class InnerCategoryPicker extends DialogFragment {
        String[] categoryList = {"Health","Food And Drink","Leisure","Transportation","Other"};
        boolean[] checked = new boolean[5];
        public void init(String[] categoryList) {
            this.categoryList = categoryList;
        }

        interface Listener {
            public void done(String choice);
        }

        Listener listener;

        public void setListener(Listener listener) {
            this.listener = listener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new
                    AlertDialog.Builder(getActivity());
            builder.setTitle("Category")
                    .setItems(categoryList,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    checked[which] = true;
                                    String select = "";
                                    for(int i = 0 ; i<checked.length ; i++){
                                        if(checked[i]){
                                            select = categoryList[i];
                                        }
                                    }
                                    listener.done(select);
                                }
                            });
            builder.setIcon(R.drawable.category);
            return builder.create();
        }
    }
}
