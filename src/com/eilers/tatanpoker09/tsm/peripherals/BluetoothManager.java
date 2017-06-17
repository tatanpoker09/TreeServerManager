package com.eilers.tatanpoker09.tsm.peripherals;

import com.eilers.tatanpoker09.tsm.Manager;
import com.eilers.tatanpoker09.tsm.server.ServerManager;
import com.eilers.tatanpoker09.tsm.server.Tree;
import com.intel.bluetooth.RemoteDeviceHelper;

import javax.bluetooth.*;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class BluetoothManager implements Callable,Manager{
    private final ServerManager serverManager;
    private DiscoveryAgent agent;
    private List<RemoteDevice> foundDevices;


    public BluetoothManager(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    public boolean setup(){

        foundDevices = new ArrayList<RemoteDevice>();
        discoverDevices();
        return (foundDevices.size()>0)? true:false;
    }

    public void postSetup() {

    }

    public void discoverDevices(){
        final Object inquiryCompletedEvent = new Object();

        DiscoveryListener discoveryListener = new DiscoveryListener() {
            Logger log = Tree.getLog();
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass deviceClass) {
                log.info("Device " + btDevice.getBluetoothAddress() + " found");
                try {
                    log.info("Details: "+btDevice.getFriendlyName(false)+", "+btDevice.isTrustedDevice());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                foundDevices.add(btDevice);
            }

            public void inquiryCompleted(int i) {
                log.info("Finished device inquiry");
                synchronized(inquiryCompletedEvent){
                    inquiryCompletedEvent.notifyAll();
                }
            }

            public void servicesDiscovered(int i, ServiceRecord[] serviceRecords) {
                
            }
            public void serviceSearchCompleted(int i, int i1) {
            }
        };
        synchronized(inquiryCompletedEvent) {
            boolean started = false;
            try {
                started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, discoveryListener);
            } catch (BluetoothStateException e) {
                e.printStackTrace();
            }
            if (started) {
                System.out.println("wait for device inquiry to complete...");
                try {
                    inquiryCompletedEvent.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(foundDevices.size() + " device(s) found");
            }
        }
    }

    public void connectDevice(RemoteDevice device){
        //CONNECTING BLUETOOTH WISE.
    }

    public List<RemoteDevice> getFoundDevices() {
        return foundDevices;
    }

    public Boolean call() throws Exception {
        return setup();
    }

    public boolean pair(RemoteDevice device, String pin){
        try {
            return RemoteDeviceHelper.authenticate(device, pin);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
