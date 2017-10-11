package com.managers;


import com.data.Race;
import com.data.Racer;
import com.events.Handler;

import java.util.ArrayList;
import java.util.Comparator;


public class RaceManager {

    public static final int NEED_UPDATE_RACE_LIST = 0;
    public static final int NEED_UPDATE_RACE_VIEW = 1;


    private ArrayList<Race> _races;

    private transient Handler _eventHandler;


    public RaceManager() {
        _races = new ArrayList<>();
    }


    public void initEventHandler(Handler value) {
        _eventHandler = value;
    }


    public void createNewRace() {
        Race newRace = new Race();
        _races.add(newRace);

        if (_eventHandler != null) {
            _eventHandler.obtainMessage(NEED_UPDATE_RACE_LIST, -1, -1, null).sendToTarget();
        }
    }
    public ArrayList<Race> getRaces() {
        return _races;
    }


    public void addRacer(int raceId, Racer racer) {
        Race race = getRace(raceId);
        if (race == null) {
            return;
        }

        race.addRacer(racer);
        if (_eventHandler != null) {
            _eventHandler.obtainMessage(NEED_UPDATE_RACE_VIEW, raceId, -1, null).sendToTarget();
        }

        updateRacersPosition(race);
        updatePoints(race);
    }

    public void removeRacer(int raceId, Racer racer) {
        Race race = getRace(raceId);
        if (race == null) {
            return;
        }

        race.removeRacer(racer);
        if (_eventHandler != null) {
            _eventHandler.obtainMessage(NEED_UPDATE_RACE_VIEW, raceId, -1, null).sendToTarget();
        }

        updateRacersPosition(race);
        updatePoints(race);
    }

    public void stopRaceForRacer(int raceId, Racer racer) {
        Race race = getRace(raceId);
        if (race == null) {
            return;
        }

        if (racer.getCompletedLapsCount() == race.getLapCount()) {
            return;
        }

        racer.stopRace();
        racer.setPoints(0);

        updateRacersPosition(race);
        updatePoints(race);
    }

    public void removeRace(int raceId) {
        Race race = getRace(raceId);
        if (race == null) {
            return;
        }

        _races.remove(race);

        if (_eventHandler != null) {
            _eventHandler.obtainMessage(NEED_UPDATE_RACE_LIST, -1, -1, null).sendToTarget();
        }
    }

    private Race getRace(int raceId) {
        Race race = null;
        for (int i = 0; i < _races.size() && race == null; i++) {
            Race currentRace = _races.get(i);
            if (currentRace.getId() == raceId) {
                race = currentRace;
            }
        }

        return race;
    }


    public void startRace(Race race) {
        if (race == null) {
            return;
        }

        boolean raceStarting = false;
        for (Racer racer: race.getRacers()) {
            if (racer.getOnStart()) {
                if (!racer.getIsRaceStarted() && !racer.getIsRaceStoped()) {
                    raceStarting = true;
                    racer.startRace();
                    racer.startLap();
                }

                racer.setOnStart(false);
            }
        }

        if (raceStarting) {
            //race.start();
            updateRacersPosition(race);
        }
    }


    public void completeLap(Racer racer) {
        if (racer == null) {
            return;
        }

        for (int i = 0; i < _races.size(); i++) {
            Race race = _races.get(i);
            for (int j = 0; j < race.getRacers().size(); j++) {
                Racer currentRacer = race.getRacers().get(j);
                if (racer.getId() == currentRacer.getId()) {
                    completeLap(race, racer);
                }
            }
        }
    }

    public void completeLap(String tag) {
        for (int i = 0; i < _races.size(); i++) {
            Race race = _races.get(i);
            for (int j = 0; j < race.getRacers().size(); j++) {
                Racer racer = race.getRacers().get(j);
                if (racer.getTag().equals(tag)) {
                    completeLap(race, racer);
                }
            }
        }
    }

    private void completeLap(Race race, Racer racer) {
        if (race == null || racer == null) {
            return;
        }

        if (!racer.getIsRaceStarted() || racer.getIsRaceStoped()) {
            return;
        }

        boolean lapCompleted = racer.completeLap();
        if (lapCompleted) {
            if (racer.getLapCount() > racer.getCompletedLapsCount()) {
                racer.startLap();
            }

            updateRacersPosition(race);

            if (racer.getCompletedLapsCount() == race.getLapCount()) {
                updatePoints(race);
            }
        }
    }


    private void updateRacersPosition(Race race) {
        for (int i = 0; i < race.getRacers().size(); i++) {
            Racer racer = race.getRacers().get(i);
            racer.setPosition(0);
        }

        int position = 1;
        for (int i = 0; i <= race.getLapCount(); i++) {
            ArrayList<Racer> racers = getRacersCompleteLap(race, race.getLapCount() - i);
            if (racers.isEmpty()) {
                continue;
            }

            if (i == race.getLapCount()) {
                racers.sort(Comparator.comparingLong(racer -> racer.getLapTime(0)));
            } else {
                racers.sort(Comparator.comparingLong(Racer::getTotalCompleteLapTime));
            }

            for (int j = 0; j < racers.size(); j++) {
                Racer racer = racers.get(j);
                racer.setPosition(position);
                position++;
            }
        }
    }

    private ArrayList<Racer> getRacersCompleteLap(Race race, int lapCount) {
        ArrayList<Racer> result = new ArrayList<>();

        for (int i = 0; i < race.getRacers().size(); i++) {
            Racer racer = race.getRacers().get(i);
            if (racer.getIsRaceStarted() && !racer.getIsRaceStoped() && racer.getCompletedLapsCount() == lapCount) {
                result.add(racer);
            }
        }

        return result;
    }

    private void updatePoints(Race race) {
        int racersCount = 0;
        for (int i = 0; i < race.getRacers().size(); i++) {
            Racer racer = race.getRacers().get(i);
            if (racer.getIsRaceStarted() && !racer.getIsRaceStoped()) {
                racersCount++;
            }
        }

        for (int i = 0; i < race.getRacers().size(); i++) {
            Racer racer = race.getRacers().get(i);
            int points = -1;

            if (racer.getIsRaceStoped()) {
                points = 0;
            } else {
                if (racer.getIsRaceStarted() && racer.getCompletedLapsCount() == race.getLapCount()) {
                    if (racer.getPosition() == 1) {
                        points = racersCount + 5;
                    } else if (racer.getPosition() == 2) {
                        points = racersCount + 2;
                    } else if (racer.getPosition() == 3) {
                        points = racersCount;
                    } else if (racer.getPosition() == 4) {
                        points = racersCount - 2;
                    } else {
                        points = racersCount - racer.getPosition() + 1;
                    }

                    if (points < 0) {
                        points = 0;
                    }
                }
            }

            racer.setPoints(points);
        }
    }

    public void initOnResume() {
        for (int i = 0; i < _races.size(); i++) {
            Race race = _races.get(i);
            race.initOnResume();
        }
    }

}
