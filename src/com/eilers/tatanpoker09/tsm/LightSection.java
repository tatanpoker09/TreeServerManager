package com.eilers.tatanpoker09.tsm;

import com.eilers.tatanpoker09.tsm.peripherals.Peripheral;

import javax.bluetooth.RemoteDevice;
import java.util.ArrayList;
import java.util.List;

public class LightSection {
    private static List<LightSection> lights;
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
        peripheral.openStream();
        if(on) {
        	peripheral.send("PrenderLED");
        } else {
        	peripheral.send("ApagarLED");
        }
    }

    public void register() {
        if(lights==null){
            lights = new ArrayList<LightSection>();
        }
        lights.add(this);
        //TODO Add to the database.
    }
}