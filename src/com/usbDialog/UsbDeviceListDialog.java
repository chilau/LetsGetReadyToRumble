package com.usbDialog;


import com.MainActivity;
import com.managers.DataManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;


public class UsbDeviceListDialog extends JDialog implements ListSelectionListener {

    private JPanel contentPane;
    private JList pairedDevicesList;
    private JButton searchButton;
    private JProgressBar searchProgressBar;



    public UsbDeviceListDialog() {
        setContentPane(contentPane);
        setModal(true);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(
                e -> onClose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_FOCUSED
        );

        searchButton.setFocusable(false);

        UsbDeviceTableCellRenderer renderer = new UsbDeviceTableCellRenderer();
        pairedDevicesList.setCellRenderer(renderer);
        pairedDevicesList.setLayoutOrientation(JList.VERTICAL);

        Thread initListThread = new Thread(this::updateList);
        initListThread.start();

        searchButton.addActionListener(e -> {
            searchButton.setVisible(false);
            searchProgressBar.setVisible(true);

            Thread thread = new Thread(() -> {
                searchProgressBar.setVisible(false);
                searchButton.setVisible(true);

                updateList();
            });

            thread.start();
        });

        pairedDevicesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ListSelectionModel listSelectionModel = pairedDevicesList.getSelectionModel();
        listSelectionModel.addListSelectionListener(this);
    }

    private void updateList() {
        DataManager dataManager = MainActivity.instance().getDataManager();
        if (dataManager == null) {
            return;
        }

        ArrayList<String> devices = dataManager.getUsbDevices();
        if (devices == null) {
            return;
        }

        ArrayList<UsbDeviceModel> devicesModels = new ArrayList<>();
        for (String portName : devices) {
            UsbDeviceModel deviceModel = new UsbDeviceModel();
            deviceModel.setPortName(portName);
            deviceModel.setIsSelected(false);

            devicesModels.add(deviceModel);
        }

        pairedDevicesList.setListData(devicesModels.toArray());
    }


    private void onClose() {
        dispose();
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        UsbDeviceModel usbDeviceModel;
        if (!listSelectionEvent.getValueIsAdjusting()) {
            usbDeviceModel = (UsbDeviceModel) pairedDevicesList.getSelectedValue();
            if (usbDeviceModel != null) {
                onItemClick(usbDeviceModel);
                pairedDevicesList.clearSelection();
            }
        }
    }

    private void onItemClick(UsbDeviceModel usbDeviceModel) {
        if (usbDeviceModel == null) {
            return;
        }

        usbDeviceModel.setIsSelected(!usbDeviceModel.getIsSelected());

        Thread connectionThread = new Thread(() -> {
            DataManager dataManager = MainActivity.instance().getDataManager();
            if (dataManager == null) {
                return;
            }

            if (dataManager.isUsbDeviceConnected(usbDeviceModel.getPortName())) {
                dataManager.disconnect(usbDeviceModel.getPortName());
            } else {
                dataManager.connect(usbDeviceModel.getPortName(), null);
            }
        });
        connectionThread.start();
    }

}
