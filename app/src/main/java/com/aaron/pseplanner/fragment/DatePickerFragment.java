package com.aaron.pseplanner.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

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
        int editTextId = getArguments().getInt(DataKey.EXTRA_ID.toString());
        this.editText = (EditText) getActivity().findViewById(editTextId);

        String selectedDate = this.editText.getText().toString();

        final Calendar calendar = Calendar.getInstance();

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

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog, this, year, month, day);

        return datePickerDialog;
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

        // Clear focus, because Date EditText is not focusable it is awkward for the focus to be on a different View
        View focusedView = getActivity().getCurrentFocus();
        if(focusedView != null)
        {
            focusedView.clearFocus();
        }
    }
}
