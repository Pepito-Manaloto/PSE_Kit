package com.aaron.pseplanner.listener;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Aaron on 11/30/2016.
 * Hides the keyboard upon losing focus on the view.
 */
public class EditTextOnFocusChangeHideKeyboard implements View.OnFocusChangeListener
{
    private Context context;

    public EditTextOnFocusChangeHideKeyboard(Context context)
    {
        this.context = context;
    }

    /**
     * If not focused then hide keyboard.
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        if(!hasFocus)
        {
            hideKeyboard(v);
        }
    }

    /**
     * Hides the keyboard from the view.
     */
    private void hideKeyboard(View view)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) this.context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
