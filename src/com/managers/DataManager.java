package com.managers;


import com.events.Handler;
import jssc.*;

import java.util.ArrayList;
import java.util.Collections;


public class DataManager implements SerialPortEventListener {

    private static final String TAG = DataManager.class.getSimpleName();

    public static final int GET_TAG = 0;

    private SerialPort _serialPort;

    private int _messageIndex = 0;
    private byte[] _message = new byte[2048];

    private Handler _connectionStateHandler;

    private final Object _locker = new Object();

    private Handler _onConnectedHandler;
    private ArrayList<String> _deviceResponse;

    private byte[] _key;
    private int _keyIndex;
    private int _tagIndex;

    private Handler _getKeyHandler;


    public DataManager() {
        _key = new byte[512];
        _keyIndex = -1;
    }


    public void initUsbConnectionStateHandler(Handler handler) {
        _connectionStateHandler = handler;
    }

    private void sendConnectionStateEventMessage(int event, int productId) {
        if (_connectionStateHandler != null) {
            _connectionStateHandler.obtainMessage(event, productId, -1, null).sendToTarget();
        }
    }


    public void connect() {

    }

    public void connect(String portName, Handler handler) {
        connect();

        _serialPort = new SerialPort(portName);
        try {
            _serialPort.openPort();
            _serialPort.setParams(
                SerialPort.BAUDRATE_9600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE
            );

            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;
            _serialPort.setEventsMask(mask);
            _serialPort.addEventListener(this);
        } catch (SerialPortException ignored) {
            _serialPort = null;
        }

        if (_serialPort != null) {
            _onConnectedHandler = handler;
        }
    }

    public void disconnect() {
        disconnect(_serialPort);
        _serialPort = null;
    }

    private void disconnect(SerialPort serialPort) {
        if (serialPort != null) {
            try {
                serialPort.closePort();
            } catch (SerialPortException ignored) {
            }
        }

        //sendConnectionStateEventMessage(ConnectionStateEnum.USB_DISCONNECTED, productId);
    }


    public void disconnect(String portName) {
        if (_serialPort != null && _serialPort.getPortName().contentEquals(portName)) {
            disconnect(_serialPort);
            _serialPort = null;
        }
    }


    public boolean isEnabled() {
        return true;
    }


    public boolean isConnected() {
        return _serialPort != null;
    }

    public boolean isUsbDeviceConnected(String portName) {
        return _serialPort != null && _serialPort.getPortName().contentEquals(portName);
    }

    public void write(byte[] out) {
        if (!isConnected()) {
            return;
        }

        SerialPort device = _serialPort;

        synchronized (_locker) {
            try {
                device.writeBytes(out);
            } catch (SerialPortException ignored) {
            }
        }
    }


    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.isRXCHAR()) {
            SerialPort serialPort = _serialPort;

            byte buffer[] = null;
            int bytesReceived = serialPortEvent.getEventValue();

            try {
                buffer = serialPort.readBytes(bytesReceived);
            } catch (SerialPortException ignored) {
            }

            if (buffer != null && bytesReceived >= -1) {
                parseMessage(buffer, bytesReceived);
            }
        } else if (serialPortEvent.isCTS()) {
            if (serialPortEvent.getEventValue() == 1) {
                System.out.println("CTS - ON");
            } else {
                System.out.println("CTS - OFF");
            }
        } else if (serialPortEvent.isDSR()) {
            if (serialPortEvent.getEventValue() == 1) {
                System.out.println("DSR - ON");
            } else {
                System.out.println("DSR - OFF");
            }
        }
    }



    private void parseMessage(byte[] buffer, int bytesReceived) {
        for (int i = 0; i < bytesReceived; i++) {
            byte byteValue = buffer[i];

            /*
            if (byteValue == (byte) 0xCC) {
                System.out.println(" ");
            }
            System.out.print(String.format("%02X", byteValue & 0xff) + " ");
            //*/

            if (byteValue == (byte) 0xCC) {
                _keyIndex = 0;
            }

            if (_keyIndex != -1) {
                if (_keyIndex == 5) {
                    _tagIndex = 0;
                }

                _key[_keyIndex] = byteValue;

                if (_keyIndex == 6 + (14 * (_tagIndex + 1))) {
                    sendGetKeyMessage(_tagIndex);
                    _tagIndex++;
                }

                _keyIndex++;
            }
        }
    }


    private void test() {
        byte[] key = new byte[512];

        key[0] = (byte) 0xCC;
        key[1] = (byte) 0xFF;
        key[2] = (byte) 0xFF;
        key[3] = (byte) 0x11;
        key[4] = (byte) 0x32;

        key[5] = (byte) 0x01;

        key[6] = (byte) 0x0E;
        key[7] = (byte) 0x01;
        key[8] = (byte) 0xE2;
        key[9] = (byte) 0x00;
        key[10] = (byte) 0x20;
        key[11] = (byte) 0x80;
        key[12] = (byte) 0x79;
        key[13] = (byte) 0x16;
        key[14] = (byte) 0x01;
        key[15] = (byte) 0x66;
        key[16] = (byte) 0x12;
        key[17] = (byte) 0x40;
        key[18] = (byte) 0x9A;
        key[19] = (byte) 0x18;
        key[20] = (byte) 0x83;

        parseMessage(key, 21);
    }

    private void test2() {
        byte[] key = new byte[512];

        key[0]  = (byte) 0xCC;
        key[1]  = (byte) 0xFF;
        key[2]  = (byte) 0xFF;
        key[3]  = (byte) 0x11;
        key[4]  = (byte) 0x32;

        key[5]  = (byte) 0x02;
        key[6]  = (byte) 0x0E;

        key[7]  = (byte) 0x01;
        key[8]  = (byte) 0xE2;
        key[9]  = (byte) 0x00;
        key[10] = (byte) 0x20;
        key[11] = (byte) 0x80;
        key[12] = (byte) 0x79;
        key[13] = (byte) 0x16;
        key[14] = (byte) 0x01;
        key[15] = (byte) 0x56;
        key[16] = (byte) 0x12;
        key[17] = (byte) 0x40;
        key[18] = (byte) 0x9A;
        key[19] = (byte) 0x07;
        key[20] = (byte) 0xA4;

        key[21] = (byte) 0x01;
        key[22] = (byte) 0xE2;
        key[23] = (byte) 0x00;
        key[24] = (byte) 0x20;
        key[25] = (byte) 0x80;
        key[26] = (byte) 0x79;
        key[27] = (byte) 0x16;
        key[28] = (byte) 0x01;
        key[29] = (byte) 0x66;
        key[30] = (byte) 0x12;
        key[31] = (byte) 0x40;
        key[32] = (byte) 0x9A;
        key[33] = (byte) 0x18;
        key[34] = (byte) 0x83;

        parseMessage(key, 35);
    }

    /*
    private void checkKey() {
        int sum = 0;
        for (int i = 5; i < _key.length - 1; i++) {
            sum += (_key[i] & 0xFF);
        }

        int reminder = sum & 0xFF;
        int notReminder = ~reminder;
        int sum1 = notReminder + 1;

        int keySum = _key[_key.length - 1] & 0xFF;

        int i = 0;
        i++;
    }
    //*/


    public void initGetKeyHandler(Handler handler) {
        _getKeyHandler = handler;

        //test();
        //test2();
    }

    private void sendGetKeyMessage(int tagIndex) {
        if (_getKeyHandler != null) {
            StringBuilder stringBuilder = new StringBuilder();
            int startIndex = 7 + (14 * tagIndex);
            int tagLength = 14;

            for (int i = startIndex; i < startIndex + tagLength; i++) {
                stringBuilder.append(String.format("%02X", _key[i] & 0xFF));
                stringBuilder.append(" ");
            }

            String tag = stringBuilder.toString();
            _getKeyHandler.obtainMessage(GET_TAG, -1, -1, tag).sendToTarget();
        }
    }

    public ArrayList<String> getUsbDevices () {
        String[] portNames = SerialPortList.getPortNames();

        ArrayList<String> result = new ArrayList<>();
        Collections.addAll(result, portNames);

        return result;
    }

}
