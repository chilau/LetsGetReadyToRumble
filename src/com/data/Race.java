package com.data;


import com.events.Handler;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class Race {

    public static final int RACER_PROPERTY_CHANGED = 0;
    public static final int NEED_UPDATE_LAP_TIME = 1;

    private static final int LAP_TIME_UPDATE_TIMEOUT_IN_MILISECONSD = 100;

    private static int _currentId = 0;


    private int _id;
    private String _name;

    private int _lapCount;

    private ArrayList<Racer> _racers;

    private transient Handler _raceEventHandler;

    private transient Timer _time;


    public Race() {
        _id = _currentId++;
        _racers = new ArrayList<>();

        setName("Заезд №" + (_id + 1));

        start();
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


    public void addRacer(Racer racer) {
        if (racer != null) {
            racer.setLapCount(_lapCount);
            _racers.add(racer);

            racer.initPropertyChangedHandler(_propertyChangedHandler);
        }
    }
    public void removeRacer(Racer racer) {
        if (racer != null && _racers != null && _racers.indexOf(racer) != -1) {
            _racers.remove(racer);
        }
    }

    public ArrayList<Racer> getRacers() {
        return _racers;
    }


    public Racer getRacer(int raceId) {
        for (Racer racer : _racers) {
            if (racer.getId() == raceId) {
                return racer;
            }
        }

        return null;
    }


    public void setLapCount(int value) {
        _lapCount = value;
        if (_racers != null) {
            for (Racer racer : _racers) {
                racer.setLapCount(_lapCount);
            }
        }
    }

    public int getLapCount() {
        return  _lapCount;
    }


    private transient Handler _propertyChangedHandler = new Handler(message -> {
        Racer racer = (Racer) message.obj;
        if (racer == null) {
            return true;
        }

        switch (message.what) {
            case Racer.ON_START_PROPERTY_CHANGED:
                sendPropertyChangedMessage(racer);
                break;
        }

        return true;
    });


    private void start() {
        if (_time == null) {
            _time = new Timer();
            TimerTask nonRespondingCommandsSearchTask = new TimerTask() {
                @Override
                public void run() {
                    sendNeedUpdateLapTimeMessage();
                }
            };
            _time.schedule(nonRespondingCommandsSearchTask, 0, LAP_TIME_UPDATE_TIMEOUT_IN_MILISECONSD);
        }
    }


    public void initRaceEventHandler(Handler handler) {
        _raceEventHandler = handler;
    }

    private void sendPropertyChangedMessage(Racer racer) {
        if (_raceEventHandler != null) {
            _raceEventHandler.obtainMessage(RACER_PROPERTY_CHANGED, -1, -1, racer).sendToTarget();
        }
    }

    private void sendNeedUpdateLapTimeMessage() {
        if (_raceEventHandler != null) {
            _raceEventHandler.obtainMessage(NEED_UPDATE_LAP_TIME, -1, -1, this).sendToTarget();
        }
    }

    public void initOnResume() {
        for (int i = 0; i < _racers.size(); i++) {
            Racer racer = _racers.get(i);
            racer.initPropertyChangedHandler(_propertyChangedHandler);
        }
    }

}
