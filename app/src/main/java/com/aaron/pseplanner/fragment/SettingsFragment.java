package com.aaron.pseplanner.fragment;

import android.app.Activity;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aaron.pseplanner.R;
import com.aaron.pseplanner.bean.SettingsDto;
import com.aaron.pseplanner.bean.TickerDto;
import com.aaron.pseplanner.constant.DataKey;
import com.aaron.pseplanner.service.LogManager;
import com.aaron.pseplanner.service.SettingsService;
import com.aaron.pseplanner.service.implementation.DefaultSettingsService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by aaron.asuncion on 11/18/2016.
 */
public class SettingsFragment extends Fragment
{
    public static final String CLASS_NAME = SettingsFragment.class.getSimpleName();

    private Dialog intervalDialog;
    private SettingsDto settingsDto;
    private SettingsService service;

    private CheckBox autoRefreshCheck;
    private TextView refreshIntervalText;
    private CheckBox notifyStopLossCheck;
    private CheckBox notifyTargetPriceCheck;
    private CheckBox notiftyTimeStopCheck;
    private CheckBox notifySoundEffectCheck;
    private TextView proxyText;

    /**
     * Initializes non-fragment user interface.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.service = new DefaultSettingsService(getActivity());

        if(savedInstanceState != null && savedInstanceState.containsKey(DataKey.EXTRA_SETTINGS.toString()))
        {
            this.settingsDto = savedInstanceState.getParcelable(DataKey.EXTRA_SETTINGS.toString());
        }
        else
        {
            this.settingsDto = this.service.getSettings();
        }

        if(this.settingsDto == null)
        {
            this.settingsDto = new SettingsDto();
        }

        LogManager.debug(CLASS_NAME, "onCreate", "");
    }

    /**
     * Initializes the fragment's user interface.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, parent, false);

        LinearLayout refreshIntervalLayout = (LinearLayout) view.findViewById(R.id.layout_refresh_interval);
        this.refreshIntervalText = (TextView) view.findViewById(R.id.textview_refresh_interval);

        String refreshInterval = this.settingsDto.getRefreshInterval() == 0 ? getResources().getStringArray(R.array.refresh_intervals)[0] : String.valueOf(this.settingsDto.getRefreshInterval());
        this.refreshIntervalText.setText(refreshInterval);
        refreshIntervalLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showIntervalOptionsAlertDialog(refreshIntervalText);
            }
        });

        this.autoRefreshCheck = (CheckBox) view.findViewById(R.id.checkbox_auto_refresh);
        this.autoRefreshCheck.setChecked(this.settingsDto.isAutoRefresh());
        this.notifyStopLossCheck = (CheckBox) view.findViewById(R.id.checkbox_notify_stop_loss);
        this.notifyStopLossCheck.setChecked(this.settingsDto.isNotifyStopLoss());
        this.notifyTargetPriceCheck = (CheckBox) view.findViewById(R.id.checkbox_notify_target_price);
        this.notifyTargetPriceCheck.setChecked(this.settingsDto.isNotifyTargetPrice());
        this.notiftyTimeStopCheck = (CheckBox) view.findViewById(R.id.checkbox_notify_time_stop);
        this.notiftyTimeStopCheck.setChecked(this.settingsDto.isNotiftyTimeStop());
        this.notifySoundEffectCheck = (CheckBox) view.findViewById(R.id.checkbox_notify_with_sound_effect);
        this.notifySoundEffectCheck.setChecked(this.settingsDto.isNotifySoundEffect());

        LinearLayout proxyLayout = (LinearLayout) view.findViewById(R.id.layout_proxy);
        proxyLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createAndShowProxyDialog();
            }
        });

        this.proxyText = (TextView) view.findViewById(R.id.textview_proxy);
        updateProxyText();

        LogManager.debug(CLASS_NAME, "onCreateView", "");

        return view;
    }

    /**
     * Dismisses the dialog to prevent android.view.WindowLeaked.
     */
    @Override
    public void onStop()
    {
        this.updateSettingsDto(this.settingsDto);
        this.service.saveSettings(this.settingsDto);

        LogManager.debug(CLASS_NAME, "onStop", "Saved: " + this.settingsDto);

        if(this.intervalDialog != null)
        {
            this.intervalDialog.dismiss();
            this.intervalDialog = null;
        }

        super.onStop();
    }

    /**
     * Saves current state in memory.
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(this.settingsDto != null)
        {
            outState.putParcelable(DataKey.EXTRA_SETTINGS.toString(), this.settingsDto);
            LogManager.debug(CLASS_NAME, "onSaveInstanceState", "Settings: " + this.settingsDto);
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
                intervalDialog.dismiss();
            }
        });


        this.intervalDialog = builder.create();
        this.intervalDialog.show();
    }

    /**
     * Creates and show the proxy dialog.
     */
    private void createAndShowProxyDialog()
    {
        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_proxy, null);
        builder.setView(view);

        final EditText proxyHost = (EditText) view.findViewById(R.id.edittext_proxy_host);
        if(StringUtils.isNotBlank(this.settingsDto.getProxyHost()))
        {
            proxyHost.setText(this.settingsDto.getProxyHost());
        }

        final EditText proxyPort = (EditText) view.findViewById(R.id.edittext_proxy_port);
        if(this.settingsDto.getProxyPort() > 0)
        {
            proxyPort.setText(String.valueOf(this.settingsDto.getProxyPort()));
        }

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                settingsDto.setProxyHost(proxyHost.getText().toString());
                settingsDto.setProxyPort(Integer.parseInt(proxyPort.getText().toString()));
                updateProxyText();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    /**
     * Updates the settings dto with the current values of the settings.
     *
     * @param dto the settings to update
     */
    private void updateSettingsDto(SettingsDto dto)
    {
        dto.setAutoRefresh(this.autoRefreshCheck.isChecked());

        String interval = this.refreshIntervalText.getText().toString();
        if(StringUtils.isNumeric(interval))
        {
            dto.setRefreshInterval(Integer.parseInt(interval));
        }

        dto.setNotifyStopLoss(this.notifyStopLossCheck.isChecked());
        dto.setNotifyTargetPrice(this.notifyTargetPriceCheck.isChecked());
        dto.setNotiftyTimeStop(this.notiftyTimeStopCheck.isChecked());
        dto.setNotifySoundEffect(this.notifySoundEffectCheck.isChecked());
    }

    private void updateProxyText()
    {
        if(StringUtils.isNotBlank(this.settingsDto.getProxyHost()) && this.settingsDto.getProxyPort() > 0)
        {
            this.proxyText.setText(this.settingsDto.getProxyHost() + ":" + this.settingsDto.getProxyPort());
        }
        else
        {
            this.proxyText.setText(R.string.label_no_proxy);
        }
    }
}
