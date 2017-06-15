package com.eilers.tatanpoker09.tsm;

import com.eilers.tatanpoker09.tsm.peripherals.Peripheral;

import javax.bluetooth.RemoteDevice;

public class LightSection {
    private Peripheral peripheral;
    private String name;

    public LightSection(String name){
        this.name = name;
    }

    public LightSection(String name, Peripheral peripheral){
        this.name = name;
        this.peripheral = peripheral;
    }


    public void turn(boolean on){
        RemoteDevice remoteDevice = peripheral.getBtDevice();
    }
}
