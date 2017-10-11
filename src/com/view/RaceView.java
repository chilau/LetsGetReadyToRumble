package com.view;


import com.data.Race;
import com.data.Racer;
import com.enums.RaceTypeEnum;
import com.events.Handler;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class RaceView extends JPanel implements ItemListener, ChangeListener {

    public static final int ADD_RACER_BUTTON_CLICK = 1;
    public static final int START_RACE_BUTTON_CLICK = 2;
    public static final int REMOVE_RACE_BUTTON_CLICK = 3;

    public static final int EDIT_RACER_BUTTON_CLICK = 4;
    public static final int REMOVE_RACER_BUTTON_CLICK = 5;
    public static final int COMPLETE_LAP_BUTTON_CLICK = 6;
    public static final int STOP_RACE_FOR_RACER_BUTTON_CLICK = 7;


    private Race _race;
    private Handler _eventHandler;

    private JTextField _titleTextField;
    private JTable _raceTable;
    private JButton _startRaceButton;
    private AdvancedScrollPane _tableScrollPane;

    private AdvancedComboBox _comboBox;
    private JPanel _raceParamPanel;
    private JSpinner _lapCountSpinner;


    public RaceView() {
        super(new BorderLayout());

        setBackground(new Color(238, 238, 238));
        setBorder(BorderFactory.createEmptyBorder());
        setOpaque(true);

        Border containerBorder = getBorder();
        Border containerMargin = new EmptyBorder(0, 0, 0, 0);
        setBorder(new CompoundBorder(containerBorder, containerMargin));

        setMinimumSize(new Dimension(100, 100));
        setMaximumSize(new Dimension(100, 100));
    }


    public void setRace(Race value) {
        _race = value;
        if (_race != null) {
            _race.initRaceEventHandler(_raceEventHandler);
        }

        updateView();
    }
    public Race getRace() {
        return _race;
    }


    public void initEventHandler(Handler value) {
        _eventHandler = value;
    }


    private void updateView() {
        removeAll();

        if (_race != null) {
            createRacePanel();
            updateRaceParamPanel();
        }
    }

    private void createRacePanel() {
        setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridx = 0;

        gridBagConstraints.gridy = 0;
        JPanel topPanel = createTopPanel();
        add(topPanel, gridBagConstraints);

        gridBagConstraints.gridy = 1;
        JPanel paramsPanel = createParamsPanel();
        add(paramsPanel, gridBagConstraints);

        gridBagConstraints.gridy = 2;
        JPanel tablePanel = createTablePanel();
        add(tablePanel, gridBagConstraints);

        gridBagConstraints.gridy = 3;
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, gridBagConstraints);

        _titleTextField.setText(_race.getName());
    }

    private JPanel createTopPanel() {
        JLabel titleLabel = new JLabel();
        titleLabel.setText("Имя:");

        _titleTextField = new JTextField();
        _titleTextField.setPreferredSize(new Dimension(250, 25));
        _titleTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                onTextFieldValueChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                onTextFieldValueChanged();
            }

            public void insertUpdate(DocumentEvent e) {
                onTextFieldValueChanged();
            }
        });

        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false);

        GridBagConstraints titlePanelGridBagConstraints = new GridBagConstraints();
        titlePanelGridBagConstraints.insets = new Insets(10, 10, 10, 10);
        titlePanelGridBagConstraints.fill = GridBagConstraints.BOTH;
        titlePanelGridBagConstraints.weightx = 1;
        titlePanelGridBagConstraints.gridx = 0;
        titlePanelGridBagConstraints.gridy = 0;

        titlePanel.add(titleLabel, titlePanelGridBagConstraints);

        titlePanelGridBagConstraints.gridx = 1;
        titlePanel.add(_titleTextField, titlePanelGridBagConstraints);

        _startRaceButton = new JButton();
        _startRaceButton.setText("Старт");
        _startRaceButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onStartRaceButtonClick();
            }
        });

        titlePanelGridBagConstraints.insets = new Insets(10, 30, 10, 10);
        titlePanelGridBagConstraints.gridx = 2;
        titlePanel.add(_startRaceButton, titlePanelGridBagConstraints);

        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        container.add(titlePanel);

        JPanel result = new JPanel(new BorderLayout());
        //result.setBackground(Color.GREEN);
        result.setOpaque(false);
        result.add(container, BorderLayout.WEST);

        JButton addRacerButton = new JButton();
        addRacerButton.setText("Добавить участника");
        addRacerButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddRacerButtonClick();
            }
        });

        JPanel racerButtonsPanel = new JPanel(new GridBagLayout());
        racerButtonsPanel.setOpaque(false);

        GridBagConstraints racerButtonsPanelGridBagConstraints = new GridBagConstraints();
        racerButtonsPanelGridBagConstraints.insets = new Insets(10, 10, 10, 10);
        racerButtonsPanelGridBagConstraints.fill = GridBagConstraints.BOTH;
        racerButtonsPanelGridBagConstraints.weightx = 1;
        racerButtonsPanelGridBagConstraints.gridx = 0;
        racerButtonsPanelGridBagConstraints.gridy = 0;

        racerButtonsPanel.add(addRacerButton, racerButtonsPanelGridBagConstraints);

        result.add(racerButtonsPanel, BorderLayout.EAST);

        return result;
    }

    private JPanel createParamsPanel() {
        JPanel result = new JPanel(new BorderLayout());
        //result.setBackground(Color.MAGENTA);

        JPanel componentsPanel = new JPanel(new GridBagLayout());
        HashMap<Integer, String> dictionary = RaceTypeEnum.getDictionary();

        java.util.List<String> adapterData = new ArrayList<>();
        Map<Integer, String> treeMap = new TreeMap<>(dictionary);
        for (Integer key : treeMap.keySet()) {
            String value = treeMap.get(key);
            adapterData.add(value);
        }

        JLabel raceTypeLabel = new JLabel();
        raceTypeLabel.setText("Тип проведения:");

        _comboBox = new AdvancedComboBox<>(adapterData.toArray());
        _comboBox.setSelectedIndex(0);
        _comboBox.setFont(_comboBox.getFont().deriveFont(Font.PLAIN));
        _comboBox.addItemListener(this);
        _comboBox.setPreferredSize(new Dimension(178, _comboBox.getPreferredSize().height));

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridy = 0;

        gridBagConstraints.gridx = 0;
        componentsPanel.add(raceTypeLabel, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        componentsPanel.add(_comboBox, gridBagConstraints);

        _raceParamPanel = new JPanel(new GridBagLayout());
        gridBagConstraints.gridx = 2;
        componentsPanel.add(_raceParamPanel, gridBagConstraints);

        result.add(componentsPanel, BorderLayout.WEST);

        return result;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new GridBagLayout());

        _raceTable = new JTable(new RacersTableModel());
        _raceTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        _raceTable.getColumnModel().getColumn(0).setPreferredWidth(20);
        _raceTable.getColumnModel().getColumn(1).setPreferredWidth(25);
        _raceTable.getColumnModel().getColumn(2).setPreferredWidth(25);
        _raceTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        _raceTable.getColumnModel().getColumn(4).setPreferredWidth(250);
        _raceTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        _raceTable.getColumnModel().getColumn(6).setPreferredWidth(25);
        _raceTable.getColumnModel().getColumn(7).setPreferredWidth(25);
        _raceTable.getColumnModel().getColumn(8).setPreferredWidth(25);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        _raceTable.getColumnModel().getColumn(7).setCellRenderer( centerRenderer );

        _raceTable.setRowHeight(25);

        _raceTable.setRowSelectionAllowed(false);
        _raceTable.setFocusable(false);
        /*
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(_raceTable.getModel());
        sorter.setSortable(0, false);
        sorter.setSortable(1, false);
        sorter.setSortable(2, false);
        sorter.setSortable(6, false);
        sorter.setSortable(7, false);
        _raceTable.setRowSorter(sorter);
        //*/
        TableColumn tc = _raceTable.getColumnModel().getColumn(0);
        tc.setHeaderRenderer(new CheckBoxHeaderRenderer(_raceTable, 0));

        new ButtonColumn(_raceTable, _editAction,     1, "res/edit.png");
        new ButtonColumn(_raceTable, _deleteAction,   2, "res/trash.png");
        new ButtonColumn(_raceTable, _stopLapAction,  7, "res/watch.png");

        Dimension raceTablePreferredSize = _raceTable.getPreferredSize();
        raceTablePreferredSize.height += 18;

        _raceTable.setPreferredScrollableViewportSize(raceTablePreferredSize);

        GridBagConstraints tablePanelGridBagConstraints = new GridBagConstraints();
        tablePanelGridBagConstraints.insets = new Insets(10, 10, 10, 10);
        tablePanelGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        tablePanelGridBagConstraints.weightx = 1;
        tablePanelGridBagConstraints.gridx = 0;
        tablePanelGridBagConstraints.gridy = 0;

        _tableScrollPane = new AdvancedScrollPane(_raceTable);
        tablePanel.add(_tableScrollPane, tablePanelGridBagConstraints);

        _tableScrollPane.setMaximumSize(new Dimension(500, _tableScrollPane.getMaximumSize().height));
        _tableScrollPane.setMinimumSize(new Dimension(500, _tableScrollPane.getMinimumSize().height));
        _tableScrollPane.setPreferredSize(new Dimension(500, _tableScrollPane.getPreferredSize().height));

        return tablePanel;
    }

    private JPanel createBottomPanel() {
        JPanel result = new JPanel(new GridBagLayout());

        JLabel removeButton = new JLabel();
        removeButton.setIcon(createImageIcon("res/remove.png"));
        removeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                onRemoveRaceButtonClick();
            }
        });

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 0, 10, 10);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;

        result.add(removeButton, gridBagConstraints);

        JPanel container = new JPanel(new BorderLayout());
        container.add(result, BorderLayout.EAST);

        return container;
    }

    private ImageIcon createImageIcon(String path) {
        URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, "");
        } else {
            return null;
        }
    }

    private Action _editAction = new AbstractAction() {
        public void actionPerformed(ActionEvent event) {
            sendRacerMessage(EDIT_RACER_BUTTON_CLICK, event.getActionCommand());
        }
    };

    private Action _deleteAction = new AbstractAction() {
        public void actionPerformed(ActionEvent event) {
            sendRacerMessage(REMOVE_RACER_BUTTON_CLICK, event.getActionCommand());
        }
    };

    private Action _stopLapAction = new AbstractAction() {
        public void actionPerformed(ActionEvent event) {
            sendRacerMessage(COMPLETE_LAP_BUTTON_CLICK, event.getActionCommand());
        }
    };

    private Action _stopRaceAction = new AbstractAction() {
        public void actionPerformed(ActionEvent event) {
            sendRacerMessage(STOP_RACE_FOR_RACER_BUTTON_CLICK, event.getActionCommand());
        }
    };

    private void sendRacerMessage(int what, String racerIdStringValue) {
        if (_eventHandler != null) {
            int racerId = Integer.parseInt(racerIdStringValue);
            _eventHandler.obtainMessage(what, racerId, -1, _race).sendToTarget();
        }
    }


    private void onTextFieldValueChanged() {
        if (_race != null) {
            String newName = _titleTextField.getText();
            _race.setName(newName);
        }
    }

    private void onLapCountChanged() {
        if (_race != null) {
            int lapCount = (int) _lapCountSpinner.getValue();
            _race.setLapCount(lapCount);

            TableColumnModel columnModel = _raceTable.getColumnModel();
            if (columnModel.getColumnCount() > 9) {
                int columnCount = columnModel.getColumnCount();
                for (int i = columnCount - 1; i >= 9; i--) {
                    columnModel.removeColumn(columnModel.getColumn(i));
                }
            }

            for (int i = 0; i < lapCount; i++) {
                TableColumn tableColumn = new TableColumn(i + 9);
                tableColumn.setHeaderValue("Круг " + String.valueOf(i + 1));

                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(JLabel.CENTER);
                tableColumn.setCellRenderer(centerRenderer);

                columnModel.addColumn(tableColumn);
            }

            TableColumn totalTimeColumn = new TableColumn(columnModel.getColumnCount());
            totalTimeColumn.setHeaderValue("Общее время");
            totalTimeColumn.setPreferredWidth(85);

            DefaultTableCellRenderer totalTimeColumnRenderer = new DefaultTableCellRenderer();
            totalTimeColumnRenderer.setHorizontalAlignment(JLabel.CENTER);
            totalTimeColumn.setCellRenderer(totalTimeColumnRenderer);

            columnModel.addColumn(totalTimeColumn);

            TableColumn pointsColumn = new TableColumn(columnModel.getColumnCount());
            pointsColumn.setHeaderValue("Очки");
            pointsColumn.setPreferredWidth(40);

            DefaultTableCellRenderer pointsColumnRenderer = new DefaultTableCellRenderer();
            pointsColumnRenderer.setHorizontalAlignment(JLabel.CENTER);
            pointsColumn.setCellRenderer(pointsColumnRenderer);

            columnModel.addColumn(pointsColumn);


            TableColumn stopColumn = new TableColumn(columnModel.getColumnCount());
            stopColumn.setHeaderValue("");
            stopColumn.setPreferredWidth(25);

            columnModel.addColumn(stopColumn);

            new ButtonColumn(_raceTable, _stopRaceAction, columnModel.getColumnCount() - 1, "res/stop.png");
            columnModel.getColumn(columnModel.getColumnCount() - 1).setPreferredWidth(25);

            update();
        }
    }

    public void update() {
        if (_raceTable == null || _raceTable.getModel() == null || !_raceTable.getModel().getClass().equals(RacersTableModel.class)) {
            return;
        }

        RacersTableModel tableModel = (RacersTableModel)_raceTable.getModel();
        tableModel.setRace(_race);

        updateSrartRaceButtonState();

        revalidate();

        Dimension raceTablePreferredSize = _raceTable.getPreferredSize();
        raceTablePreferredSize.height += 18;
        _raceTable.setPreferredScrollableViewportSize(raceTablePreferredSize);

        _tableScrollPane.setMaximumSize(new Dimension(500, raceTablePreferredSize.height + 25));
        _tableScrollPane.setMinimumSize(new Dimension(500, raceTablePreferredSize.height + 25));
        _tableScrollPane.setPreferredSize(new Dimension(500, raceTablePreferredSize.height + 25));
    }

    private void updateSrartRaceButtonState() {
        if (_startRaceButton == null) {
            return;
        }

        if (_race.getRacers().size() == 0) {
            _startRaceButton.setEnabled(false);
            return;
        }

        boolean enabled = false;
        for (Racer racer: _race.getRacers()) {
            enabled = enabled || racer.getOnStart();
        }

        _startRaceButton.setEnabled(enabled);
    }


    private void onAddRacerButtonClick() {
        sendEventMessage(ADD_RACER_BUTTON_CLICK);
    }
    private void onStartRaceButtonClick() {
        sendEventMessage(START_RACE_BUTTON_CLICK);
    }
    private void onRemoveRaceButtonClick() {
        sendEventMessage(REMOVE_RACE_BUTTON_CLICK);
    }

    private void sendEventMessage(int what) {
        if (_eventHandler != null) {
            _eventHandler.obtainMessage(what, -1, -1, _race).sendToTarget();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        updateRaceParamPanel();
    }

    private void updateRaceParamPanel() {
        if (_raceParamPanel == null) {
            return;
        }

        _raceParamPanel.removeAll();

        int raceType = _comboBox.getSelectedIndex();
        if (raceType == RaceTypeEnum.TYPE_A) {
            createParamPanelForTypeA();
            onLapCountChanged();
        } else if (raceType == RaceTypeEnum.TYPE_B){
            createParamPanelForTypeB();
        }

        update();
    }

    private void createParamPanelForTypeA() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridy = 0;

        JLabel lapCountLabel = new JLabel();
        lapCountLabel.setText("Кол-во кругов:");

        SpinnerModel spinnerNumberModel = new SpinnerNumberModel(5, 1, 100, 1);
        _lapCountSpinner = new JSpinner(spinnerNumberModel);
        _lapCountSpinner.addChangeListener(this);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new Insets(10, 20, 0, 0);
        _raceParamPanel.add(lapCountLabel, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        _raceParamPanel.add(_lapCountSpinner, gridBagConstraints);
    }

    private void createParamPanelForTypeB() {

    }

    @Override
    public void stateChanged(ChangeEvent e) {
        onLapCountChanged();
    }


    private Handler _raceEventHandler = new Handler(message -> {
        switch (message.what) {
            case Race.RACER_PROPERTY_CHANGED:
                update();
                break;
            case  Race.NEED_UPDATE_LAP_TIME:
                if (_raceTable != null && _raceTable.getModel().getClass().equals(RacersTableModel.class)) {
                    RacersTableModel tableModel = (RacersTableModel)_raceTable.getModel();

                    if (tableModel != null) {
                        try {
                            tableModel.fireTableDataChanged();
                        } catch (Exception ignored) {
                        }
                    }
                }
                break;
        }

        return true;
    });

}
