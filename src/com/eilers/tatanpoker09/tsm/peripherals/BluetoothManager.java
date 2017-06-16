package com.eilers.tatanpoker09.tsm.peripherals;

import com.eilers.tatanpoker09.tsm.server.ServerManager;
import com.eilers.tatanpoker09.tsm.server.Tree;

import javax.bluetooth.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BluetoothManager {
    private final ServerManager serverManager;
    private DiscoveryAgent agent;
    private List<RemoteDevice> foundDevices;
    private boolean searching;

    public BluetoothManager(ServerManager serverManager) {
        this.serverManager = serverManager;
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
                try {
                    log.info("Details: "+btDevice.getFriendlyName(true)+", "+btDevice.isTrustedDevice());
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
            try {
                setSearching(LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, discoveryListener));
            } catch (BluetoothStateException e) {
                e.printStackTrace();
            }
            if (isSearching()) {
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

    public List<RemoteDevice> getFoundDevices() {
        return foundDevices;
    }

	public boolean isSearching() {
		return searching;
	}

	public void setSearching(boolean searching) {
		this.searching = searching;
	}
}
