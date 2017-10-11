package com.view;


import com.data.Race;
import com.data.Racer;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class RacersTableModel extends AbstractTableModel {

    private ArrayList<Racer> _racers;
    private Race _race;


    public void setRace(Race race) {
        _race = race;
        setRacers(_race.getRacers());
    }
    private void setRacers(ArrayList<Racer> racers) {
        if (_racers == null) {
            _racers = new ArrayList<>();
        }

        _racers.clear();
        _racers.addAll(racers);

        fireTableDataChanged();
    }


    @Override
    public int getRowCount() {
        return _racers != null ? _racers.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return _race == null ? 9 : 9 + _race.getLapCount();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (_racers == null) {
            return "";
        }

        Racer racer = _racers.get(rowIndex);

        if (columnIndex == 0) {
            return racer.getOnStart();
        }

        if (columnIndex == 1 ||
            columnIndex == 2) {
            return racer.getId();
        }

        if (columnIndex == 3) {
            return racer.getNumber();
        }

        if (columnIndex == 4) {
            String lastName = racer.getLastName();
            String name = racer.getName();
            String patronymic = racer.getPatronymic();

            String result = lastName;
            if (!name.isEmpty()) {
                result += " " + name.substring(0, 1) + ".";

                if (!patronymic.isEmpty()) {
                    result += patronymic.substring(0, 1) + ".";
                }
            }

            return result;
        }

        if (columnIndex == 5) {
            return racer.getCity();
        }

        if (columnIndex == 6) {
            return racer.getRfidIcon();
        }

        if (columnIndex == 7) {
            return racer.getId();
        }

        if (columnIndex == 8) {
            int position = racer.getPosition();
            if (position <= 0) {
                return "";
            }

            return String.valueOf(position);
        }

        if (columnIndex == _race.getLapCount() + 9) {
            long totalTime = racer.getTotalLapTime();
            if (totalTime == 0) {
                return "";
            }

            DateFormat formatter = new SimpleDateFormat("mm:ss:SSS");
            return formatter.format(totalTime);

        }

        if (columnIndex == _race.getLapCount() + 10) {
            int points = racer.getPoints();
            if (points == -1) {
                return "";
            } else {
                return points;
            }
        }

        if (columnIndex == _race.getLapCount() + 11) {
            return racer.getId();
        }


        long lapTime = racer.getLapTime(columnIndex - 9);
        if (lapTime == 0) {
            return "";
        }

        DateFormat formatter = new SimpleDateFormat("mm:ss:SSS");
        return formatter.format(lapTime);
    }

    @Override
    public String getColumnName(int c) {
        switch (c) {
            case 0:
            case 1:
            case 2:
            case 6:
            case 7:
                return "";
            case 3:
                return "Номер";
            case 4:
                return "Ф.И.О.";
            case 5:
                return  "Город";
            case 8:
                return  "#";
            default:
                return "";
        }
    }

    @Override
    public Class getColumnClass(int column) {
        if(column == 0){
            return Boolean.class;
        }

        if(column == 6){
            return ImageIcon.class;
        }

        return super.getColumnClass(column);

    }

    @Override
    public boolean isCellEditable(int row, int column){
        int columnCount = getColumnCount();
        return column == 0 || column == 1 || column == 2 || column == 7 || column == columnCount + 2;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            Racer racer = _racers.get(rowIndex);
            racer.setOnStart((boolean) aValue);

            fireTableCellUpdated(rowIndex, columnIndex);
        } else {
            super.setValueAt(aValue, rowIndex, columnIndex);
        }
    }

}
