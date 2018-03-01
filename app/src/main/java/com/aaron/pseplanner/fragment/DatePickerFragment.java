package com.aaron.pseplanner.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.service.LogManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.util.Calendar;

/**
 * Created by aaron.asuncion on 2/3/2017.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    public static final String CLASS_NAME = DatePickerFragment.class.getSimpleName();
    private static final String DATE_PATTERN = "MM-dd-yyyy";
    public static final FastDateFormat DATE_FORMATTER = FastDateFormat.getInstance(DATE_PATTERN);

    private EditText editText;

    /**
     * Gets a new instance of DatePickerFragment with the EditText's id.
     *
     * @param id the EditText of this date picker
     * @return DatePickerFragment
     */
    public static DatePickerFragment newInstance(int id)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(DataKey.EXTRA_ID.toString(), id);

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(bundle);

        return datePickerFragment;
    }

    /**
     * Creates the date picker dialog. Sets the date to the given date argument.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LogManager.debug(CLASS_NAME, "onCreateDialog", "");

        Bundle args = getArguments();
        Activity activity = getActivity();
        if(args != null)
        {
            int editTextId = args.getInt(DataKey.EXTRA_ID.toString());

            if(activity != null)
            {
                this.editText = activity.findViewById(editTextId);

                String selectedDate = this.editText.getText().toString();
                final Calendar calendar = createFormattedCalendar(selectedDate);

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(activity, android.R.style.Theme_Holo_Light_Dialog, this, year, month, day);
                // Add none button for clearing out current selected date
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, activity.getText(R.string.none_button), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        editText.setText("");
                    }
                });

                return dialog;
            }
        }

        // Return some dialog, this should never happen
        return getDialog();
    }

    private Calendar createFormattedCalendar(String selectedDate)
    {
        Calendar calendar = Calendar.getInstance();

        try
        {
            if(StringUtils.isNotBlank(selectedDate))
            {
                calendar.setTime(DATE_FORMATTER.parse(selectedDate));
            }
        }
        catch(ParseException e)
        {
            LogManager.error(CLASS_NAME, "onCreateDialog", "Error parsing date, will use current date instead.", e);
        }

        return calendar;
    }

    /**
     * Sets the date to return to the calling Activity.
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        String selectedDate = DATE_FORMATTER.format(calendar);
        this.editText.setText(selectedDate);

        LogManager.debug(CLASS_NAME, "onDateSet", "Date: " + selectedDate);

        Activity activity = getActivity();
        if(activity != null)
        {
            // Clear focus, because Date EditText is not focusable it is awkward for the focus to be on a different View
            View focusedView = activity.getCurrentFocus();
            if(focusedView != null)
            {
                focusedView.clearFocus();
            }
        }
    }
}
