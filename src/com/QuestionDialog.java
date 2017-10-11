package com;


import com.data.Race;
import com.data.Racer;
import com.events.Handler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class QuestionDialog extends JDialog {

    public static final int OK = 0;
    public static final int CANCEL = 1;

    private JPanel _mainPanel;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextPane _questionLabel;
    private JPanel _bottomPanel;
    private JPanel _buttonsPanel;

    private Handler _handler;

    private Race _race;
    private Racer _racer;


    public QuestionDialog() {
        setContentPane(_mainPanel);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setResizable(false);

        _mainPanel.setBackground(Color.WHITE);
        _bottomPanel.setOpaque(false);
        _buttonsPanel.setOpaque(false);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        _mainPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public void setLabel(String value) {
        if (_questionLabel != null) {
            _questionLabel.setText(value);
        }
    }

    void setRace(Race race) {
        _race = race;
    }

    void setRacer(Racer racer) {
        _racer = racer;
    }

    public void initCompleteHandler(Handler value) {
        _handler = value;
    }

    private void sendCompleteMessage(int what) {
        if (_handler != null) {
            _handler.obtainMessage(what, _race.getId(), -1, _racer).sendToTarget();
        }
    }

    private void onOK() {
        sendCompleteMessage(OK);
        dispose();
    }

    private void onCancel() {
        sendCompleteMessage(CANCEL);
        dispose();
    }

}
