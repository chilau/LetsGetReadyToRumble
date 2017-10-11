package com.usbDialog;


class UsbDeviceModel {

    private String _portName;
    private boolean _isSelected;


    void setPortName(String value) {
        _portName = value;
    }

    String getPortName() {
        return _portName;
    }


    void setIsSelected(boolean value) {
        _isSelected = value;
    }

    boolean getIsSelected() {
        return _isSelected;
    }

}
