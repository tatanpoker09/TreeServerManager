package com.eilers.tatanpoker09.tsm.peripherals;

import com.eilers.tatanpoker09.tsm.server.Tree;

import javax.bluetooth.*;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BluetoothManager {
    private DiscoveryAgent agent;
    private List<RemoteDevice> foundDevices;


    public BluetoothManager() {

    }

    public void setup(){
        foundDevices = new ArrayList<RemoteDevice>();
    }

    public void discoverDevices(){
        final Object inquiryCompletedEvent = new Object();

        DiscoveryListener discoveryListener = new DiscoveryListener() {
            Logger log = Tree.getLog();
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass deviceClass) {
                log.info("Device " + btDevice.getBluetoothAddress() + " found");
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

    public void pairDevice(RemoteDevice device){
        //PAIRING BLUETOOTH WISE.
    }
}
