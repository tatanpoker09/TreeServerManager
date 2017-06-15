package com.eilers.tatanpoker09.tsm.peripherals;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to handle bluetooth and other kinds of connections.
 * @author tatanpoker09
 *
 */
public class PeripheralManager {
    private List<Peripheral> peripherals;

    public PeripheralManager(){

    }

    public void setup(){
        this.peripherals = new ArrayList<Peripheral>();
    }

    public void addPeripheral(Peripheral peripheral){
        this.getPeripherals().add(peripheral);
    }


    public List<Peripheral> getPeripherals() {
        return peripherals;
    }

    public void setPeripherals(List<Peripheral> peripherals) {
        this.peripherals = peripherals;
    }
}
