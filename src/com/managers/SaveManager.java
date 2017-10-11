package com.managers;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.prefs.Preferences;


public class SaveManager {

    private final String DATA = "data";
    private final String DATA_LENGTH = "dataLength";

    private Preferences _preferences;


    public void init() {
        _preferences = Preferences.userRoot().node("LetsGetReadyToRumble");
    }


    public void save(RaceManager raceManager) {
        if (_preferences == null) {
            return;
        }

        try {
            String frequencyBandsString = new Gson().toJson(raceManager);
            int frequencyBandsStringLength = frequencyBandsString.length();
            _preferences.put(DATA_LENGTH, String.valueOf(frequencyBandsStringLength));

            int maxLength = Preferences.MAX_VALUE_LENGTH;
            int partCount = (frequencyBandsStringLength / maxLength) + 1;

            for (int i = 0; i < partCount; i++) {
                int startIndex = i * maxLength;
                int stopIndex = startIndex + maxLength < frequencyBandsStringLength ? startIndex + maxLength : startIndex + (frequencyBandsStringLength - startIndex);

                String part = frequencyBandsString.substring(startIndex, stopIndex);
                _preferences.put(DATA + "_" + String.valueOf(i), part);
            }
        } catch (Exception ignored) {
        }
    }


    public RaceManager load() {
        if (_preferences == null) {
            return null;
        }

        String dataTextValue = "";

        String frequencyBandsLengthString  = _preferences.get(DATA_LENGTH, "");
        if (frequencyBandsLengthString.isEmpty()) {
            dataTextValue = _preferences.get(DATA, "");
        } else {
            StringBuilder stringBuilder = new StringBuilder();

            int frequencyBandsStringLength = Integer.parseInt(frequencyBandsLengthString);
            int maxLength = Preferences.MAX_VALUE_LENGTH;
            int partCount = (frequencyBandsStringLength / maxLength) + 1;

            for (int i = 0; i < partCount; i++) {
                String part = _preferences.get(DATA + "_" + String.valueOf(i), "");
                stringBuilder.append(part);
            }

            dataTextValue = stringBuilder.toString();
        }

        RaceManager raceManager = null;
        if (!dataTextValue.isEmpty()) {
            raceManager = new Gson().fromJson(dataTextValue, new TypeToken<RaceManager>() {
            }.getType());
        }

        return raceManager;
    }

}
