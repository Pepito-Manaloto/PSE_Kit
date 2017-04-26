package com.aaron.pseplanner.service;

import com.aaron.pseplanner.bean.SettingsDto;

/**
 * Created by aaron.asuncion on 4/12/2017.
 */

public interface SettingsService
{
    /**
     * Saves the current settings.
     */
    void saveSettings(SettingsDto dto);

    /**
     * Retrieves the current settings.
     */
    SettingsDto getSettings();
}
