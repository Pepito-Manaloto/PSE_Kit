package com.aaron.pseplanner.service.implementation;

import android.content.Context;
import android.content.SharedPreferences;

import com.aaron.pseplanner.RobolectricTest;
import com.aaron.pseplanner.bean.SettingsDto;
import com.aaron.pseplanner.constant.PSEPlannerPreference;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Created by Aaron on 04/01/2018.
 */

public class DefaultSettingsServiceTest extends RobolectricTest
{
    private DefaultSettingsService service;

    @Before
    public void beforeTest()
    {
        service = new DefaultSettingsService(RuntimeEnvironment.application.getApplicationContext());
    }

    @Test
    public void givenSettings_whenSaveSettings_thenSettingsShouldBeSavedInSharedPreference()
    {
        boolean autoRefresh = true;
        int refreshInterval = 60;
        boolean notifyStopLoss = true;
        boolean notifyTargetPrice = true;
        boolean notiftyTimeStop = true;
        boolean notifySoundEffect = true;
        String proxyHost = "www.abc.zxc.pq";
        int proxyPort = 90801;
        SettingsDto dto = givenSettingsDto(autoRefresh, refreshInterval, notifyStopLoss, notifyTargetPrice, notiftyTimeStop,
                notifySoundEffect, proxyHost, proxyPort);

        whenSettingsIsSaved(dto);
        thenSharedPreferenceShouldContainTheSavedSettings(dto);
    }

    @Test
    public void givenNullSettings_whenSaveSettings_thenSharedPreferenceShouldNotContainAnySettings()
    {
        whenSettingsIsSaved(null);
        thenSharedPreferenceShouldNotContainTheSavedSettings();
    }

    @Test
    public void givenSettingsInSharedPreference_whenGetSettings_thenShouldReturnTheSettings()
    {
        boolean autoRefresh = true;
        int refreshInterval = 60;
        boolean notifyStopLoss = true;
        boolean notifyTargetPrice = true;
        boolean notiftyTimeStop = true;
        boolean notifySoundEffect = true;
        String proxyHost = "www.abc.zxc.pq";
        int proxyPort = 90801;
        givenSettingsInSharedPreference(autoRefresh, refreshInterval, notifyStopLoss, notifyTargetPrice, notiftyTimeStop,
                notifySoundEffect, proxyHost, proxyPort);

        SettingsDto settings = whenGetSettings();

        thenTheSettingsShouldMatchTheSharedPreference(settings);
    }

    @Test
    public void givenNoSettingsInSharedPreference_whenGetSettings_thenShouldReturnUnsetSettings()
    {
        SettingsDto settings = whenGetSettings();

        thenSettingsShouldHaveDefaultPrimitiveValues(settings);
    }

    private SettingsDto givenSettingsDto(boolean autoRefresh, int refreshInterval, boolean notifyStopLoss, boolean notifyTargetPrice, boolean notiftyTimeStop,
            boolean notifySoundEffect, String proxyHost, int proxyPort)
    {
        SettingsDto dto = new SettingsDto();

        dto.setAutoRefresh(autoRefresh);
        dto.setRefreshInterval(refreshInterval);
        dto.setNotifyStopLoss(notifyStopLoss);
        dto.setNotifyTargetPrice(notifyTargetPrice);
        dto.setNotiftyTimeStop(notiftyTimeStop);
        dto.setNotifySoundEffect(notifySoundEffect);
        dto.setProxyHost(proxyHost);
        dto.setProxyPort(proxyPort);

        return dto;
    }

    private void givenSettingsInSharedPreference(boolean autoRefresh, int refreshInterval, boolean notifyStopLoss, boolean notifyTargetPrice,
            boolean notiftyTimeStop, boolean notifySoundEffect, String proxyHost, int proxyPort)
    {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(PSEPlannerPreference.AUTO_REFRESH.toString(), autoRefresh);
        editor.putInt(PSEPlannerPreference.REFRESH_INTERVAL.toString(), refreshInterval);
        editor.putBoolean(PSEPlannerPreference.NOTIFY_TARGET_PRICE.toString(), notifyTargetPrice);
        editor.putBoolean(PSEPlannerPreference.NOTIFY_STOP_LOSS.toString(), notifyStopLoss);
        editor.putBoolean(PSEPlannerPreference.NOTIFY_TIME_STOP.toString(), notiftyTimeStop);
        editor.putBoolean(PSEPlannerPreference.NOTIFY_SOUND_EFFECT.toString(), notifySoundEffect);
        editor.putString(PSEPlannerPreference.PROXY_HOST.toString(), proxyHost);
        editor.putInt(PSEPlannerPreference.PROXY_PORT.toString(), proxyPort);

        editor.apply();
    }

    private void whenSettingsIsSaved(SettingsDto dto)
    {
        service.saveSettings(dto);
    }

    private SettingsDto whenGetSettings()
    {
        return service.getSettings();
    }

    private void thenSharedPreferenceShouldNotContainTheSavedSettings()
    {
        SharedPreferences sharedPreferences = getSharedPreferences();

        assertFalse(sharedPreferences.contains(PSEPlannerPreference.AUTO_REFRESH.toString()));
        assertFalse(sharedPreferences.contains(PSEPlannerPreference.REFRESH_INTERVAL.toString()));
        assertFalse(sharedPreferences.contains(PSEPlannerPreference.NOTIFY_TARGET_PRICE.toString()));
        assertFalse(sharedPreferences.contains(PSEPlannerPreference.NOTIFY_STOP_LOSS.toString()));
        assertFalse(sharedPreferences.contains(PSEPlannerPreference.NOTIFY_TIME_STOP.toString()));
        assertFalse(sharedPreferences.contains(PSEPlannerPreference.NOTIFY_SOUND_EFFECT.toString()));
        assertFalse(sharedPreferences.contains(PSEPlannerPreference.PROXY_HOST.toString()));
        assertFalse(sharedPreferences.contains(PSEPlannerPreference.PROXY_PORT.toString()));
    }

    private void thenSettingsShouldHaveDefaultPrimitiveValues(SettingsDto dto)
    {
        assertFalse(dto.isAutoRefresh());
        assertEquals(0, dto.getRefreshInterval());
        assertFalse(dto.isNotifyTargetPrice());
        assertFalse(dto.isNotifyStopLoss());
        assertFalse(dto.isNotiftyTimeStop());
        assertFalse(dto.isNotifySoundEffect());
        assertNull(dto.getProxyHost());
        assertEquals(0, dto.getProxyPort());
    }

    private void thenSharedPreferenceShouldContainTheSavedSettings(SettingsDto dto)
    {
        assertSettingsWithSharedPreference(dto);
    }

    private void thenTheSettingsShouldMatchTheSharedPreference(SettingsDto dto)
    {
        assertSettingsWithSharedPreference(dto);
    }

    private void assertSettingsWithSharedPreference(SettingsDto dto)
    {
        SharedPreferences sharedPreferences = getSharedPreferences();

        boolean autoRefresh = sharedPreferences.getBoolean(PSEPlannerPreference.AUTO_REFRESH.toString(), false);
        int refreshInterval = sharedPreferences.getInt(PSEPlannerPreference.REFRESH_INTERVAL.toString(), 0);
        boolean notifyTargetPrice = sharedPreferences.getBoolean(PSEPlannerPreference.NOTIFY_TARGET_PRICE.toString(), false);
        boolean notifyStopLoss = sharedPreferences.getBoolean(PSEPlannerPreference.NOTIFY_STOP_LOSS.toString(), false);
        boolean notifyTimeStop = sharedPreferences.getBoolean(PSEPlannerPreference.NOTIFY_TIME_STOP.toString(), false);
        boolean notifySoundEffect = sharedPreferences.getBoolean(PSEPlannerPreference.NOTIFY_SOUND_EFFECT.toString(), false);
        String proxyHost = sharedPreferences.getString(PSEPlannerPreference.PROXY_HOST.toString(), "");
        int proxyPort = sharedPreferences.getInt(PSEPlannerPreference.PROXY_PORT.toString(), 0);

        assertEquals(autoRefresh, dto.isAutoRefresh());
        assertEquals(refreshInterval, dto.getRefreshInterval());
        assertEquals(notifyTargetPrice, dto.isNotifyTargetPrice());
        assertEquals(notifyStopLoss, dto.isNotifyStopLoss());
        assertEquals(notifyTimeStop, dto.isNotiftyTimeStop());
        assertEquals(notifySoundEffect, dto.isNotifySoundEffect());
        assertEquals(proxyHost, dto.getProxyHost());
        assertEquals(proxyPort, dto.getProxyPort());
    }

    private SharedPreferences getSharedPreferences()
    {
        return RuntimeEnvironment.application.getSharedPreferences(PSEPlannerPreference.class.getSimpleName(), Context.MODE_PRIVATE);
    }
}
