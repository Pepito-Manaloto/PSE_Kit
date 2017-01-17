package com.aaron.pseplanner.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aaron.pseplanner.R;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */
public class SettingsFragment extends Fragment
{
    private LinearLayout refreshIntervalLayout;
    private Dialog intervalDialog;

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, parent, false);

        this.refreshIntervalLayout = (LinearLayout) view.findViewById(R.id.layout_refresh_interval);
        final TextView refreshIntervalTextView = (TextView) view.findViewById(R.id.textview_refresh_interval);
        this.refreshIntervalLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showIntervalOptionsAlertDialog(refreshIntervalTextView);
            }
        });


        return view;
    }

    /**
     * Dismisses the dialog to prevent android.view.WindowLeaked.
     */
    @Override
    public void onStop()
    {
        super.onStop();

        if(this.intervalDialog != null)
        {
            this.intervalDialog.dismiss();
            this.intervalDialog = null;
        }
    }

    /**
     * Creates the alert dialog for refresh interval option.
     *
     * @param refreshIntervalTextView the text view to update once an interval is selected
     */
    protected void showIntervalOptionsAlertDialog(final TextView refreshIntervalTextView)
    {
        Context activity = getActivity();
        final Resources resources = getResources();
        final CharSequence[] items = resources.getTextArray(R.array.refresh_intervals);

        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(activity, R.style.AlertDialogTheme);
        AlertDialog.Builder builder = new AlertDialog.Builder(contextThemeWrapper);
        builder.setTitle(resources.getString(R.string.interval_dialog_title));

        builder.setItems(items, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int item)
            {
                refreshIntervalTextView.setText(items[item]);

                //TODO: store items[item] in settings bean

                intervalDialog.dismiss();
            }
        });


        this.intervalDialog = builder.create();
        this.intervalDialog.show();
    }
}
