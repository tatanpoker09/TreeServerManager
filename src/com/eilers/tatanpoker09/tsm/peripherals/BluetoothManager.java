package com.eilers.tatanpoker09.tsm.peripherals;

import com.eilers.tatanpoker09.tsm.Manager;
import com.eilers.tatanpoker09.tsm.server.ServerManager;
import com.eilers.tatanpoker09.tsm.server.Tree;
import com.intel.bluetooth.RemoteDeviceHelper;

import javax.bluetooth.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class BluetoothManager implements Callable, Manager {
    private final ServerManager serverManager;
    private DiscoveryAgent agent;
    private List<RemoteDevice> foundDevices;
    private boolean searching;

    public BluetoothManager(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    public boolean setup() {

        foundDevices = new ArrayList<RemoteDevice>();
        discoverDevices();
        return (foundDevices.size() > 0) ? true : false;
    }

    public void postSetup() {

    }

    public void discoverDevices() {
        final Object inquiryCompletedEvent = new Object();
        MyDiscoveryListener discoveryListener = new MyDiscoveryListener(this, inquiryCompletedEvent);

        synchronized (inquiryCompletedEvent) {
            try {
                this.agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
                setSearching(this.agent.startInquiry(DiscoveryAgent.GIAC, discoveryListener));
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

    public void connectDevice(RemoteDevice device) {
        //CONNECTING BLUETOOTH WISE.
    }

    public List<RemoteDevice> getFoundDevices() {
        return foundDevices;
    }

    public Boolean call() throws Exception {
        return setup();
    }

    public boolean pair(RemoteDevice device, String pin) {
        try {
            return RemoteDeviceHelper.authenticate(device, pin);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void searchServices(RemoteDevice device) {
        UUID[] uuidSet = new UUID[1];
        uuidSet[0] = new UUID(0x1105); //OBEX Object Push service
        int[] attrIDs = new int[]{
                0x0100 // Service name
        };

        final Object inquiryCompletedEvent = new Object();

        synchronized (inquiryCompletedEvent) {
            try {
                this.agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
                agent.searchServices(null, uuidSet, device, new MyDiscoveryListener(this, inquiryCompletedEvent));
            } catch (BluetoothStateException e) {
                e.printStackTrace();
            }
            if (isSearching()) {
                System.out.println("wait for service inquiry to complete...");
                try {
                    inquiryCompletedEvent.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isSearching() {
        return searching;
    }

    public void setSearching(boolean searching) {
        this.searching = searching;
    }
}


class MyDiscoveryListener implements DiscoveryListener {
    BluetoothManager bluetoothManager;
    Logger log = Tree.getLog();
    Object lock;

    public MyDiscoveryListener(BluetoothManager bl, Object lock){
        this.bluetoothManager = bl;
        this.lock = lock;
    }

    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass deviceClass) {
        log.info("Device " + btDevice.getBluetoothAddress() + " found");
        try {
            log.info("Details: " + btDevice.getFriendlyName(false) + ", " + btDevice.isTrustedDevice());
        } catch (IOException e) {
            e.printStackTrace();
        }
        bluetoothManager.getFoundDevices().add(btDevice);
    }

    public void inquiryCompleted(int i) {
        log.info("Finished device inquiry");
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void servicesDiscovered(int i, ServiceRecord[] serviceRecords) {

    }

    public void serviceSearchCompleted(int i, int i1) {
    }
}
