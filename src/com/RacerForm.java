package com;

import com.data.Race;
import com.data.Racer;
import com.events.Handler;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;

public class RacerForm extends JDialog {

    public static final int RACER_CREATED = 0;
    public static final int RACER_EDIT_COMPLETE = 1;

    private JPanel _mainPanel;
    private JButton _okButton;
    private JButton _cancelButton;
    private JPanel _container;
    private JPanel _bottomPanel;
    private JPanel _buttonsPanel;

    private JLabel _titleLabel;
    private JTextPane _raceNameLabel;

    private JTextField _numberTextField;
    private JTextField _lastNameTextField;
    private JTextField _nameTextField;
    private JTextField _patronymicTextField;
    private JTextField _cityTextField;
    private JTextArea _tagLabel;
    private JLabel _removeTagLabel;

    private Race _race;
    private Racer _racer;

    private Handler _completeHandler;


    RacerForm() {
        setContentPane(_mainPanel);
        setModal(true);
        getRootPane().setDefaultButton(_okButton);
        setResizable(false);

        _mainPanel.setBackground(Color.WHITE);
        _container.setOpaque(false);
        _bottomPanel.setOpaque(false);
        _buttonsPanel.setOpaque(false);

        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0);
        Action closeAction = new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                dispose();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(esc, "closex");
        getRootPane().getActionMap().put("closex", closeAction);

        createUI();
        updateOkButtonState();
    }


    void setRace(Race race) {
        _race = race;

        updateValue();
    }

    void setRacer(Racer racer) {
        _racer = racer;

        updateValue();
        updateOkButtonState();
    }


    private void createUI() {
        _container.setLayout(new BorderLayout());

        JPanel containerPanel = new JPanel(new GridBagLayout());
        containerPanel.setOpaque(false);

        createTitlePanel(containerPanel);
        createInputPanel(containerPanel);

        _container.add(containerPanel, BorderLayout.NORTH);

        addListeners();
        updateValue();
    }

    private void createTitlePanel(JPanel gridBagLayoutContainer) {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        _titleLabel = new JLabel();
        _titleLabel.setFont(_titleLabel.getFont().deriveFont(Font.PLAIN));
        container.add(_titleLabel, BorderLayout.WEST);

        _raceNameLabel = new JTextPane();
        _raceNameLabel.setFont(_raceNameLabel.getFont().deriveFont(Font.BOLD));
        _raceNameLabel.setEditable(false);
        _raceNameLabel.setFocusable(false);

        container.add(_raceNameLabel);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;

        gridBagLayoutContainer.add(container, gridBagConstraints);
    }

    private void createInputPanel(JPanel gridBagLayoutContainer) {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JLabel numberLabel = new JLabel();
        numberLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        numberLabel.setText("Номер:");

        _numberTextField = new JTextField();
        _numberTextField.setPreferredSize(new Dimension(250, 25));

        JLabel lastNameLabel = new JLabel();
        lastNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lastNameLabel.setText("Фамилия:");

        _lastNameTextField = new JTextField();
        _lastNameTextField.setPreferredSize(new Dimension(250, 25));

        JLabel nameLabel = new JLabel();
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN));
        nameLabel.setText("Имя:");

        _nameTextField = new JTextField();
        _nameTextField.setPreferredSize(new Dimension(250, 25));

        JLabel patronymicLabel = new JLabel();
        patronymicLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        patronymicLabel.setFont(patronymicLabel.getFont().deriveFont(Font.PLAIN));
        patronymicLabel.setText("Отчество:");

        _patronymicTextField = new JTextField();
        _patronymicTextField.setPreferredSize(new Dimension(250, 25));

        JLabel cityLabel = new JLabel();
        cityLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        cityLabel.setFont(cityLabel.getFont().deriveFont(Font.PLAIN));
        cityLabel.setText("Город:");

        _cityTextField = new JTextField();
        _cityTextField.setPreferredSize(new Dimension(250, 25));

        JLabel tagLabel = new JLabel();
        tagLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        tagLabel.setFont(tagLabel.getFont().deriveFont(Font.PLAIN));
        tagLabel.setText("Метка:");

        _tagLabel = new JTextArea();
        _tagLabel.setLineWrap(true);

        _removeTagLabel = new JLabel();
        _removeTagLabel.setText("");
        _removeTagLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (_tagLabel != null) {
                    _tagLabel.setText("");
                    updateOkButtonState();
                }
            }
        });

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);

        GridBagConstraints inputPanelGridBagConstraints = new GridBagConstraints();
        inputPanelGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        inputPanelGridBagConstraints.insets = new Insets(10, 0, 10, 10);

        inputPanelGridBagConstraints.fill = GridBagConstraints.NONE;
        inputPanelGridBagConstraints.anchor = GridBagConstraints.LINE_END;
        inputPanelGridBagConstraints.weightx = 0;
        inputPanelGridBagConstraints.gridx = 0;
        inputPanelGridBagConstraints.gridy = 0;
        inputPanel.add(numberLabel, inputPanelGridBagConstraints);

        inputPanelGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        inputPanelGridBagConstraints.anchor = GridBagConstraints.LINE_START;
        inputPanelGridBagConstraints.weightx = 1;
        inputPanelGridBagConstraints.gridx = 1;
        inputPanelGridBagConstraints.gridy = 0;
        inputPanel.add(_numberTextField, inputPanelGridBagConstraints);

        inputPanelGridBagConstraints.fill = GridBagConstraints.NONE;
        inputPanelGridBagConstraints.anchor = GridBagConstraints.LINE_END;
        inputPanelGridBagConstraints.weightx = 0;
        inputPanelGridBagConstraints.gridx = 0;
        inputPanelGridBagConstraints.gridy = 1;
        inputPanel.add(lastNameLabel, inputPanelGridBagConstraints);

        inputPanelGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        inputPanelGridBagConstraints.anchor = GridBagConstraints.LINE_START;
        inputPanelGridBagConstraints.weightx = 1;
        inputPanelGridBagConstraints.gridx = 1;
        inputPanelGridBagConstraints.gridy = 1;
        inputPanel.add(_lastNameTextField, inputPanelGridBagConstraints);

        inputPanelGridBagConstraints.fill = GridBagConstraints.NONE;
        inputPanelGridBagConstraints.anchor = GridBagConstraints.LINE_END;
        inputPanelGridBagConstraints.weightx = 0;
        inputPanelGridBagConstraints.gridx = 0;
        inputPanelGridBagConstraints.gridy = 2;
        inputPanel.add(nameLabel, inputPanelGridBagConstraints);

        inputPanelGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        inputPanelGridBagConstraints.anchor = GridBagConstraints.LINE_START;
        inputPanelGridBagConstraints.weightx = 1;
        inputPanelGridBagConstraints.gridx = 1;
        inputPanelGridBagConstraints.gridy = 2;
        inputPanel.add(_nameTextField, inputPanelGridBagConstraints);

        inputPanelGridBagConstraints.fill = GridBagConstraints.NONE;
        inputPanelGridBagConstraints.anchor = GridBagConstraints.LINE_END;
        inputPanelGridBagConstraints.weightx = 0;
        inputPanelGridBagConstraints.gridx = 0;
        inputPanelGridBagConstraints.gridy = 3;
        inputPanel.add(patronymicLabel, inputPanelGridBagConstraints);

        inputPanelGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        inputPanelGridBagConstraints.anchor = GridBagConstraints.LINE_START;
        inputPanelGridBagConstraints.weightx = 1;
        inputPanelGridBagConstraints.gridx = 1;
        inputPanelGridBagConstraints.gridy = 3;
        inputPanel.add(_patronymicTextField, inputPanelGridBagConstraints);

        inputPanelGridBagConstraints.fill = GridBagConstraints.NONE;
        inputPanelGridBagConstraints.anchor = GridBagConstraints.LINE_END;
        inputPanelGridBagConstraints.weightx = 0;
        inputPanelGridBagConstraints.gridx = 0;
        inputPanelGridBagConstraints.gridy = 4;
        inputPanel.add(cityLabel, inputPanelGridBagConstraints);

        inputPanelGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        inputPanelGridBagConstraints.anchor = GridBagConstraints.LINE_START;
        inputPanelGridBagConstraints.weightx = 1;
        inputPanelGridBagConstraints.gridx = 1;
        inputPanelGridBagConstraints.gridy = 4;
        inputPanel.add(_cityTextField, inputPanelGridBagConstraints);

        inputPanelGridBagConstraints.fill = GridBagConstraints.NONE;
        inputPanelGridBagConstraints.anchor = GridBagConstraints.PAGE_START;
        inputPanelGridBagConstraints.weightx = 0;
        inputPanelGridBagConstraints.gridx = 0;
        inputPanelGridBagConstraints.gridy = 5;
        inputPanel.add(tagLabel, inputPanelGridBagConstraints);

        inputPanelGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        inputPanelGridBagConstraints.anchor = GridBagConstraints.LINE_START;
        inputPanelGridBagConstraints.weightx = 1;
        inputPanelGridBagConstraints.gridx = 1;
        inputPanelGridBagConstraints.gridy = 5;
        inputPanel.add(_tagLabel, inputPanelGridBagConstraints);

        inputPanelGridBagConstraints.fill = GridBagConstraints.NONE;
        inputPanelGridBagConstraints.anchor = GridBagConstraints.PAGE_START;
        inputPanelGridBagConstraints.weightx = 0;
        inputPanelGridBagConstraints.gridx = 2;
        inputPanelGridBagConstraints.gridy = 5;
        inputPanel.add(_removeTagLabel, inputPanelGridBagConstraints);

        container.add(inputPanel, BorderLayout.CENTER);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;

        gridBagLayoutContainer.add(container, gridBagConstraints);
    }

    private void addListeners() {
        _okButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOkButtonClick();
            }
        });

        _cancelButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancelButtonClick();
            }
        });

        addTextFieldValueChangedListener(_numberTextField);
        addTextFieldValueChangedListener(_lastNameTextField);
        addTextFieldValueChangedListener(_nameTextField);
        addTextFieldValueChangedListener(_patronymicTextField);
    }

    private void addTextFieldValueChangedListener(JTextField textField) {
        textField.getDocument().addDocumentListener(new DocumentListener() {
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
    }

    private void onTextFieldValueChanged() {
        updateOkButtonState();
    }

    private void updateOkButtonState() {
        String number = _numberTextField.getText();
        String lastName = _lastNameTextField.getText();
        String tag = _tagLabel.getText();

        if (tag.isEmpty()) {
            _removeTagLabel.setText("      ");
            _removeTagLabel.setIcon(null);
        } else {
            _removeTagLabel.setText("");
            URL imgURL = getClass().getResource("res/remove.png");
            if (imgURL != null) {
                ImageIcon imageIcon = new ImageIcon(imgURL, "");
                _removeTagLabel.setIcon(imageIcon);
            }
        }

        boolean okButtonEnable = !number.isEmpty() && !lastName.isEmpty();// && !tag.isEmpty();
        _okButton.setEnabled(okButtonEnable);
    }


    private void onOkButtonClick() {
        String number = _numberTextField.getText();
        String lastName = _lastNameTextField.getText();
        String name = _nameTextField.getText();
        String patronymic = _patronymicTextField.getText();
        String city = _cityTextField.getText();
        String tag = _tagLabel.getText();

        Racer racer;
        if (_racer == null) {
            racer = new Racer();
        } else {
            racer = _racer;
        }

        racer.setNumber(number);
        racer.setLastName(lastName);
        racer.setName(name);
        racer.setPatronymic(patronymic);
        racer.setCity(city);
        racer.setTag(tag);

        if (_racer == null) {
            sendCompleteMessage(RACER_CREATED, racer);
        } else {
            sendCompleteMessage(RACER_EDIT_COMPLETE, racer);
        }

        dispose();
    }

    private void onCancelButtonClick() {
        dispose();
    }


    public void initCompleteHandler(Handler value) {
        _completeHandler = value;
    }

    private void sendCompleteMessage(int what, Racer racer) {
        if (_completeHandler != null && _race != null) {
            _completeHandler.obtainMessage(what, _race.getId(), -1, racer).sendToTarget();
        }
    }

    public void setTag(String tag) {
        if (_tagLabel != null) {
            _tagLabel.setText(tag);
        }

        try {
            updateOkButtonState();
        } catch (Exception ignored) {
        }
    }


    private void updateValue() {
        if (_titleLabel != null) {
            if (_racer == null) {
                _titleLabel.setText("Добавление участника для:  ");
            } else {
                _titleLabel.setText("Редактирование участника для:  ");
            }
        }

        if (_raceNameLabel != null) {
            if (_race != null) {
                _raceNameLabel.setText(_race.getName());
            } else {
                _raceNameLabel.setText("");
            }
        }

        if (_racer != null) {
            if (_numberTextField != null) {
                _numberTextField.setText(_racer.getNumber());
            }
            if (_lastNameTextField != null) {
                _lastNameTextField.setText(_racer.getLastName());
            }
            if (_nameTextField != null) {
                _nameTextField.setText(_racer.getName());
            }
            if (_patronymicTextField != null) {
                _patronymicTextField.setText(_racer.getPatronymic());
            }
            if (_cityTextField != null) {
                _cityTextField.setText(_racer.getCity());
            }
            if (_tagLabel != null) {
                _tagLabel.setText(_racer.getTag());
            }

            if (_okButton != null) {
                _okButton.setText("Редактировать");
            }
        } else {
            if (_tagLabel != null) {
                _tagLabel.setText("");
            }

            if (_okButton != null) {
                _okButton.setText("Добавить");
            }
        }
    }

}
