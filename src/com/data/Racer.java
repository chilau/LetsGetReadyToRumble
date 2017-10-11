package com.data;


import com.events.Handler;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;


public class Racer {

    public transient static final int ON_START_PROPERTY_CHANGED = 0;

    private transient static int _currentId = 0;


    private int _id;

    private String _name;
    private String _patronymic;
    private String _lastName;

    private String _city;

    private String _number;
    private String _tag;

    private boolean _onStart;

    private int _lapCount;
    private int _position;
    private int _points;

    private long _lapStartTime;
    private ArrayList<Long> _lapsTime;

    private boolean _raceStarted;
    private boolean _raceStoped;
    private transient ImageIcon _rfidIcon;

    private transient Handler _propertyChangedHandler;


    public Racer() {
        _id = _currentId++;

        _position = 0;
        _points = -1;

        _lapStartTime = 0;
        _lapsTime = new ArrayList<>();
    }


    public int getId() {
        return _id;
    }

    public void setName(String value) {
        _name = value;
    }
    public String getName() {
        return _name;
    }

    public void setPatronymic(String value) {
        _patronymic = value;
    }
    public String getPatronymic() {
        return _patronymic;
    }

    public void setLastName(String value) {
        _lastName = value;
    }
    public String getLastName() {
        return _lastName;
    }

    public void setCity(String value) {
        _city = value;
    }
    public String getCity() {
        return _city;
    }

    public void setNumber(String value) {
        _number = value;
    }
    public String getNumber() {
        return _number;
    }

    public void setTag(String value) {
        _tag = value;
    }
    public String getTag() {
        return _tag;
    }

    public void setOnStart(boolean value) {
        _onStart = value;
        sendPropertyChangedMessage();
    }
    public boolean getOnStart() {
        return _onStart;
    }

    public void setPosition(int value) {
        _position = value;
        sendPropertyChangedMessage();
    }
    public int getPosition() {
        return _position;
    }

    public void setPoints(int value) {
        _points = value;
        sendPropertyChangedMessage();
    }
    public int getPoints() {
        return _points;
    }

    public void setLapCount(int value) {
        _lapCount = value;
    }
    public int getLapCount() {
        return  _lapCount;
    }
    public int getCompletedLapsCount() {
        return _lapsTime.size();
    }


    public void startRace() {
        _raceStarted = true;
    }
    public boolean getIsRaceStarted() {
        return _raceStarted;
    }

    public void stopRace() {
        _lapStartTime = 0;
        _position = 0;
        _raceStoped = true;
    }
    public boolean getIsRaceStoped() {
        return _raceStoped;
    }

    public void startLap() {
        _lapStartTime = new Date().getTime();
    }
    public boolean completeLap() {
        if (_lapStartTime == 0) {
            return false;
        }

        long now = new Date().getTime();
        long lapTime = now - _lapStartTime;

        if (lapTime < 5000) {
            return false;
        }

        _lapStartTime = 0;
        _lapsTime.add(lapTime);

        return true;
    }

    public long getLapTime(int lapIndex) {
        if (lapIndex < 0 || lapIndex >= _lapCount) {
            return 0;
        }

        if (lapIndex > _lapsTime.size()) {
            return 0;
        }

        if (lapIndex == _lapsTime.size()) {
            if (_lapStartTime != 0) {
                long now = new Date().getTime();
                return now - _lapStartTime;
            } else {
                return 0;
            }
        }

        return _lapsTime.get(lapIndex);
    }

    public long getTotalCompleteLapTime() {
        long result = 0;
        for (Long lapsTime : _lapsTime) {
            result += lapsTime;
        }

        return result;
    }

    public long getTotalLapTime() {
        long lapTime = 0;
        if (_lapStartTime != 0) {
            long now = new Date().getTime();
            lapTime = now - _lapStartTime;
        }

        long result = getTotalCompleteLapTime();
        result += lapTime;

        return result;
    }

    public ImageIcon getRfidIcon() {
        if (_tag == null || _tag.isEmpty()) {
            return null;
        }

        if (_rfidIcon == null) {
            URL imgURL = getClass().getResource("../res/rfid.png");
            if (imgURL != null) {
                _rfidIcon = new ImageIcon(imgURL, "");
            }
        }

        return _rfidIcon;
    }


    public void initPropertyChangedHandler(Handler handler) {
        _propertyChangedHandler = handler;
    }

    private void sendPropertyChangedMessage() {
        if (_propertyChangedHandler != null) {
            _propertyChangedHandler.obtainMessage(ON_START_PROPERTY_CHANGED, -1, -1, this).sendToTarget();
        }
    }

}
