package com.eilers.tatanpoker09.tsm.peripherals;

import javax.bluetooth.RemoteDevice;

public class Peripheral {
    private String type;
    private RemoteDevice btDevice; //Bluetooth.
    private PeripheralStatus status;

    public Peripheral(String type){
        this.type = type;
    }

    public void registerBtDevice(RemoteDevice device){
        this.btDevice = device;
    }

    public RemoteDevice getBtDevice() {
        return btDevice;
    }
}
