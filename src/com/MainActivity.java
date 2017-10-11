package com;

import com.data.Race;
import com.data.Racer;
import com.events.Handler;
import com.managers.DataManager;
import com.managers.RaceManager;
import com.managers.SaveManager;
import com.usbDialog.UsbDeviceListDialog;
import com.view.AdvancedScrollPane;
import com.view.RaceView;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity  extends JPanel {

    private static JFrame _frame;

    private SaveManager _saveManager;

    private RaceManager _raceManager;
    private ArrayList<RaceView> _raceViews;

    private JPanel _raceViewsPanel;

    private DataManager _dataManager;
    private RacerForm _racerForm;


    private MainActivity() {
        super(new BorderLayout());
    }


    private void init() {
        UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0))); // remove border around text for jButton

        _instance = this;

        _saveManager = new SaveManager();
        _saveManager.init();

        _raceViews = new ArrayList<>();

        _raceManager = _saveManager.load();
        if (_raceManager == null) {
            _raceManager = new RaceManager();
        } else {
            _raceManager.initOnResume();
        }

        _raceManager.initEventHandler(_raceManagerEventHandler);

        _raceViewsPanel = new JPanel(new GridBagLayout());
        _raceViewsPanel.setBackground(Color.WHITE);

        _dataManager = new DataManager();
        _dataManager.initGetKeyHandler(_dataManagerEventHandler);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(_raceViewsPanel, BorderLayout.NORTH);

        JPanel menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.NORTH);

        AdvancedScrollPane scrollPane = new AdvancedScrollPane(container);
        scrollPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        updateRaceList();
    }

    private JPanel createMenuPanel() {
        JPanel result = new JPanel(new BorderLayout());
        result.setOpaque(false);

        ImageIcon icon = createImageIcon("res/add-3.png");
        JLabel addButton = new JLabel();
        addButton.setIcon(icon);
        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                onAddRaceButtonClick();
            }
        });

        ImageIcon usbIcon = createImageIcon("res/usb.png");
        JLabel usbButton = new JLabel();
        usbButton.setIcon(usbIcon);
        usbButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                onUsbButtonClick();
            }
        });

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 20, 10, 10);
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridy = 0;

        gridBagConstraints.gridx = 0;
        buttonPanel.add(addButton, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        buttonPanel.add(usbButton, gridBagConstraints);

        result.add(buttonPanel, BorderLayout.WEST);

        return result;
    }

    private ImageIcon createImageIcon(String path) {
        URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, "");
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    private void onAddRaceButtonClick() {
        _raceManager.createNewRace();
        save();
    }

    private void onUsbButtonClick() {
        openUsbDialog();
    }


    private void updateRaceList() {
        if (_raceManager == null) {
            return;
        }

        ArrayList<Race> races = _raceManager.getRaces();
        if (races.size() != _raceViews.size()) {
            if (races.size() < _raceViews.size()) {
                for (RaceView raceView : _raceViews) {
                    _raceViewsPanel.remove(raceView);
                }

                _raceViews.clear();
            }

            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(10, 10, 10, 10);
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = _raceViews.size();

            for (int i = _raceViews.size(); i < races.size(); i++) {
                RaceView raceView = createNewRaceView(_raceViewsPanel, gridBagConstraints);
                _raceViews.add(raceView);

                Race race = races.get(i);
                raceView.setRace(race);
            }
        }

        _raceViewsPanel.revalidate();
        _raceViewsPanel.repaint();
    }

    private RaceView createNewRaceView(JPanel container, GridBagConstraints gridBagConstraints) {
        RaceView raceView = new RaceView();
        raceView.initEventHandler(_raceViewEventHandler);

        container.add(raceView, gridBagConstraints);
        gridBagConstraints.gridy++;

        return raceView;
    }


    private void updateRaceView(int raceId) {
        RaceView raceView = null;
        for (int i = 0; i < _raceViews.size() && raceView == null; i++) {
            RaceView currentRaceView = _raceViews.get(i);
            Race currentRace = currentRaceView.getRace();
            if (currentRace != null && currentRace.getId() == raceId) {
                raceView = currentRaceView;
            }
        }

        if (raceView == null) {
            return;
        }

        raceView.update();
    }


    private Handler _raceViewEventHandler = new Handler(message -> {
        switch (message.what) {
            case RaceView.ADD_RACER_BUTTON_CLICK:
                Race race = (Race) message.obj;
                if (race != null) {
                    openAddRacerForm(race);
                }
                break;
            case RaceView.START_RACE_BUTTON_CLICK:
                onStartRaceButtonClick((Race) message.obj);
                break;
            case RaceView.REMOVE_RACE_BUTTON_CLICK:
                onRemoveRaceButtonClick((Race) message.obj);
                break;
            case RaceView.EDIT_RACER_BUTTON_CLICK:
                openEditRacerForm((Race) message.obj, message.arg1);
                break;
            case RaceView.REMOVE_RACER_BUTTON_CLICK:
                openRemoveRacerForm((Race) message.obj, message.arg1);
                break;
            case RaceView.COMPLETE_LAP_BUTTON_CLICK:
                onCompleteLapButtonClick((Race) message.obj, message.arg1);
                break;
            case RaceView.STOP_RACE_FOR_RACER_BUTTON_CLICK:
                openStopRaceForRacerForm((Race) message.obj, message.arg1);
                break;
        }

        return true;
    });

    private Handler _raceManagerEventHandler = new Handler(message -> {
        switch (message.what) {
            case RaceManager.NEED_UPDATE_RACE_LIST:
                updateRaceList();
                break;
            case RaceManager.NEED_UPDATE_RACE_VIEW:
                int raceId = message.arg1;
                updateRaceView(raceId);
                break;
        }

        return true;
    });

    private Handler _dataManagerEventHandler = new Handler(message -> {
        switch (message.what) {
            case DataManager.GET_TAG:
                if (_racerForm != null && _racerForm.isVisible()) {
                    _racerForm.setTag((String)message.obj);
                } else if (_raceManager != null) {
                    _raceManager.completeLap((String)message.obj);
                }

                save();
                break;
        }

        return true;
    });

    private void openAddRacerForm(Race race) {
        openRacerForm(race, null);
    }

    private void openEditRacerForm(Race race, int racerId) {
        if (race == null) {
            return;
        }

        Racer racer = race.getRacer(racerId);
        if (racer == null) {
            return;
        }

        openRacerForm(race, racer);
    }



    private void openRacerForm(Race race, Racer racer) {
        _racerForm = new RacerForm();
        _racerForm.setRace(race);
        _racerForm.setRacer(racer);
        _racerForm.initCompleteHandler(_racerFormEventHandler);

        _racerForm.setSize(390, 410);
        _racerForm.setLocationRelativeTo(_frame);
        _racerForm.setVisible(true);
    }

    private void openRemoveRacerForm(Race race, int racerId) {
        Racer racer = race.getRacer(racerId);
        if (racer == null) {
            return;
        }

        QuestionDialog dialog = new QuestionDialog();
        dialog.setLabel("Вы действительно хотите удалить участника " + racer.getLastName() + "?");
        dialog.setRace(race);
        dialog.setRacer(racer);
        dialog.initCompleteHandler(_removeDialogEventHandler);

        dialog.setSize(390, 170);
        dialog.setLocationRelativeTo(_frame);
        dialog.setVisible(true);
    }

    private void openStopRaceForRacerForm(Race race, int racerId) {
        Racer racer = race.getRacer(racerId);
        if (racer == null || racer.getIsRaceStoped() || racer.getCompletedLapsCount() == race.getLapCount()) {
            return;
        }

        QuestionDialog dialog = new QuestionDialog();
        dialog.setLabel("Вы действительно хотите завершить заезд для участника " + racer.getLastName() + "?");
        dialog.setRace(race);
        dialog.setRacer(racer);
        dialog.initCompleteHandler(_stopRaceForRacerDialogEventHandler);

        dialog.setSize(390, 170);
        dialog.setLocationRelativeTo(_frame);
        dialog.setVisible(true);
    }

    private void openUsbDialog() {
        UsbDeviceListDialog dialog = new UsbDeviceListDialog();

        dialog.setSize(300, 500);
        dialog.setLocationRelativeTo(_frame);
        dialog.setVisible(true);
    }

    private Handler _racerFormEventHandler = new Handler(message -> {
        Racer racer = (Racer) message.obj;
        int raceId = message.arg1;

        switch (message.what) {
            case RacerForm.RACER_CREATED:
                if (_raceManager != null) {
                    _raceManager.addRacer(raceId, racer);
                    save();
                }

                break;
            case RacerForm.RACER_EDIT_COMPLETE:
                updateRaceView(raceId);
                break;
        }

        return true;
    });

    private Handler _removeDialogEventHandler = new Handler(message -> {
        Racer racer = (Racer) message.obj;
        int raceId = message.arg1;

        switch (message.what) {
            case QuestionDialog.OK:
                if (_raceManager != null) {
                    _raceManager.removeRacer(raceId, racer);
                    save();
                }

                break;
        }

        return true;
    });

    private Handler _stopRaceForRacerDialogEventHandler = new Handler(message -> {
        Racer racer = (Racer) message.obj;
        int raceId = message.arg1;

        switch (message.what) {
            case QuestionDialog.OK:
                if (_raceManager != null) {
                    _raceManager.stopRaceForRacer(raceId, racer);
                    save();
                }

                break;
        }

        return true;
    });

    private Handler _removeRaceDialogEventHandler = new Handler(message -> {
        int raceId = message.arg1;

        switch (message.what) {
            case QuestionDialog.OK:
                if (_raceManager != null) {
                    _raceManager.removeRace(raceId);
                    save();
                }

                break;
        }

        return true;
    });


    private void onStartRaceButtonClick(Race race) {
        if (_raceManager == null || race == null) {
            return;
        }

        _raceManager.startRace(race);
        save();
    }

    private void onRemoveRaceButtonClick(Race race) {
        QuestionDialog dialog = new QuestionDialog();
        dialog.setLabel("Вы действительно хотите удалить заезд " + race.getName() + "?");
        dialog.setRace(race);
        dialog.initCompleteHandler(_removeRaceDialogEventHandler);

        dialog.setSize(390, 170);
        dialog.setLocationRelativeTo(_frame);
        dialog.setVisible(true);
    }

    private void onCompleteLapButtonClick(Race race, int racerId) {
        if (race == null) {
            return;
        }

        Racer racer = race.getRacer(racerId);
        if (racer == null) {
            return;
        }

        _raceManager.completeLap(racer);
        save();
    }


    private void save() {
        if (_saveManager != null) {
            _saveManager.save(_raceManager);
        }
    }



    private void onClose() {

    }


    public DataManager getDataManager() {
        return _dataManager;
    }


    private static MainActivity _instance;
    public static MainActivity instance() {
        return _instance;
    }


    private static void createAndShowGUI() {
        try {
            MainActivity mainActivity = new MainActivity();

            _frame = new JFrame();
            _frame.setContentPane(mainActivity);
            _frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            _frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    mainActivity.onClose();
                    System.exit(0);
                }
            });

            _frame.setSize(1280, 720);
            _frame.setMinimumSize(new Dimension(1024, 600));
            _frame.setVisible(true);

            _frame.setTitle("Let's Get Ready To Rumble");

            mainActivity.init();
        } catch (Throwable throwable) {
            JOptionPane.showMessageDialog(null, throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
            throw throwable;
        }
    }


    public static void main(String[] args) {
        Runnable runnable = MainActivity::createAndShowGUI;
        SwingUtilities.invokeLater(runnable);
    }

}
