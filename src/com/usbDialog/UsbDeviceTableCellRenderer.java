package com.usbDialog;

import com.MainActivity;
import com.managers.DataManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class UsbDeviceTableCellRenderer extends JPanel implements ListCellRenderer<UsbDeviceModel> {

    private JLabel _deviceNameLabel;
    private JCheckBox _steelCheckBox;


    UsbDeviceTableCellRenderer() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.black));

        JPanel textValuesPanel = new JPanel();
        JPanel switchPanel = new JPanel();

        textValuesPanel.setLayout(new BorderLayout());
        switchPanel.setLayout(new BorderLayout());

        add(textValuesPanel, BorderLayout.CENTER);
        add(switchPanel, BorderLayout.EAST);

        _deviceNameLabel = new JLabel();
        JLabel deviceAddressLabel = new JLabel();

        textValuesPanel.add(_deviceNameLabel, BorderLayout.NORTH);
        textValuesPanel.add(deviceAddressLabel, BorderLayout.SOUTH);

        Border deviceNameLabelBorder = _deviceNameLabel.getBorder();
        Border deviceNameLabelMargin = new EmptyBorder(10, 10, 0, 0);
        _deviceNameLabel.setBorder(new CompoundBorder(deviceNameLabelBorder, deviceNameLabelMargin));

        Border deviceAddressLabelBorder = _deviceNameLabel.getBorder();
        Border deviceAddressLabelMargin = new EmptyBorder(-10, 0, 15, 0);
        deviceAddressLabel.setBorder(new CompoundBorder(deviceAddressLabelBorder, deviceAddressLabelMargin));

        _deviceNameLabel.setFont(_deviceNameLabel.getFont().deriveFont(18f));
        deviceAddressLabel.setFont(deviceAddressLabel.getFont().deriveFont(16f));

        _steelCheckBox = new JCheckBox();
        _steelCheckBox.setText(" ");
        _steelCheckBox.setSelected(false);
        _steelCheckBox.setVerticalAlignment(SwingConstants.CENTER);

        switchPanel.add(_steelCheckBox, BorderLayout.CENTER);
    }


    @Override
    public Component getListCellRendererComponent(JList<? extends UsbDeviceModel> list, UsbDeviceModel value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value != null) {
            try {
                _deviceNameLabel.setText(value.getPortName());
            } catch (Exception ignored) {
            }

            DataManager dataManager = MainActivity.instance().getDataManager();
            boolean isConnected = dataManager.isUsbDeviceConnected(value.getPortName());

            _steelCheckBox.setVisible(true);
            _steelCheckBox.setSelected(isConnected);
        }

        return this;
    }

}
