package com.eilers.tatanpoker09.tsm.peripherals;

import com.eilers.tatanpoker09.tsm.Manager;
import com.eilers.tatanpoker09.tsm.server.MQTTManager;
import com.eilers.tatanpoker09.tsm.server.Tree;
import com.intel.bluetooth.RemoteDeviceHelper;
import net.sf.xenqtt.client.PublishMessage;
import net.sf.xenqtt.message.QoS;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class BluetoothManager implements Callable, Manager {
	private DiscoveryAgent agent;
	private List<RemoteDevice> foundDevices;
	private boolean searching;

    public BluetoothManager() {

	}

    public static byte[][] convertToBytes(List<RemoteDevice> devices) {
        byte[][] data = new byte[devices.size()][];
        for (int i = 0; i < devices.size(); i++) {
            String string = null;
            try {
                string = devices.get(i).getFriendlyName(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            data[i] = string.getBytes();
        }
        return data;
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
        foundDevices.clear();
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

    public void publishDevices() {
        byte[][] deviceBytes = convertToBytes(foundDevices);
        for (byte[] array : deviceBytes) {
            MQTTManager.getClient().publish(new PublishMessage("server/peripheral/bluetooth/devices", QoS.AT_LEAST_ONCE, array));
        }
    }

    public String getBluetoothAddress(RemoteDevice rd) {
        return rd.getBluetoothAddress();
    }

    public String getFriendlyName(RemoteDevice btDevice, boolean alwaysAsk) {
        try {
            return btDevice.getFriendlyName(alwaysAsk);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    private static void sendMessageToDevice(String serverURL) {
        try {
            System.out.println("Connecting to " + serverURL);
            ClientSession clientSession = (ClientSession) Connector.open(serverURL);
            HeaderSet hsConnectReply = clientSession.connect(null);
            if (hsConnectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
                System.out.println("Failed to connect");
                return;
            }
            HeaderSet hsOperation = clientSession.createHeaderSet();
            hsOperation.setHeader(HeaderSet.NAME, "Hello.txt");
            hsOperation.setHeader(HeaderSet.TYPE, "text");
            //Create PUT Operation
            Operation putOperation = clientSession.put(hsOperation);
            // Sending the message
            byte data[] = "Hello World !!!".getBytes("iso-8859-1");
            OutputStream os = putOperation.openOutputStream();
            os.write(data);
            os.close();
            putOperation.close();
            clientSession.disconnect(null);
            clientSession.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

	public void servicesDiscovered(int j, ServiceRecord[] services) {
        System.out.println(services);
        for (int i = 0; i < services.length; i++) {
			String url = services[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
			if (url == null) {
				continue;
			}
			DataElement serviceName = services[i].getAttributeValue(0x0100);
			if (serviceName != null) {
				System.out.println("service " + serviceName.getValue() + " found " + url);
			} else {
				System.out.println("service found " + url);
			}
			if(serviceName.getValue().equals("OBEX Object Push")){
                sendMessageToDevice(url);
            }
        }
    }

	public void serviceSearchCompleted(int arg0, int arg1) { 
		synchronized (lock) {
			lock.notify();
		}
	}
}
