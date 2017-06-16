package com.eilers.tatanpoker09.tsm.peripherals;

import com.eilers.tatanpoker09.tsm.Manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Class used to handle bluetooth and other kinds of connections.
 * @author tatanpoker09
 *
 */
public class PeripheralManager implements Callable,Manager{
    private List<Peripheral> peripherals;

    public PeripheralManager(){

    }

    public boolean setup(){
        this.peripherals = new ArrayList<Peripheral>();
        return true;
    }

    public void postSetup() {

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

    public Boolean call() throws Exception {
        return setup();
    }
}
