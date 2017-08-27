package com.eilers.tatanpoker09.tsm.peripherals;

import com.eilers.tatanpoker09.tsm.server.Tree;
import com.intel.bluetooth.MicroeditionConnector;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.StreamConnection;
import java.io.DataOutputStream;
import java.io.IOException;

public class Peripheral extends Thread{
    private String type;
    private RemoteDevice btDevice; //Bluetooth.
    private PeripheralStatus status;
    private DataOutputStream os;
    private StreamConnection streamConnection;
    
    
    public Peripheral(String type){
        this.type = type;
    }

    public static Peripheral getByName(String type, String name) {
        Peripheral p = new Peripheral(type);
        for (RemoteDevice device : Tree.getServer().getpManager().getBtManager().getFoundDevices()) {
            try {
                if (device.getFriendlyName(true).equals(name)) {
                    p.registerBtDevice(device);
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return p;
    }

    public static Peripheral getByAddress(String type, String address) {
        Peripheral p = new Peripheral(type);
        for (RemoteDevice device : Tree.getServer().getpManager().getBtManager().getFoundDevices()) {
            if (device.getBluetoothAddress().equals(address)) {
                p.registerBtDevice(device);
                break;
            }
        }
        return p;
    }

    public void registerBtDevice(RemoteDevice device){
        this.btDevice = device;
    }

    public RemoteDevice getBtDevice() {
        return btDevice;
    }

    public String getServerURL() {
		return "btspp://"+btDevice.getBluetoothAddress()+":1;authenticate=false;encrypt=false;master=false";
	}

	public void openStream(String serverURL) {
        if(this.os==null) {
            StreamConnection sc;
            try {
                sc = (StreamConnection) MicroeditionConnector.open(serverURL);
                DataOutputStream os = sc.openDataOutputStream();
                this.os = os;
                this.streamConnection = sc;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
	}

	public void closeStream(){
    	if(this.os!=null){
            try {
                this.os.close();
                this.os = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Stream is already closed!");
        }
        if(this.streamConnection!=null){
            try{
                this.streamConnection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Stream is closed!");
        }
	}

    public void openStream() {
		openStream(getServerURL());
	}
	
	public boolean isConnected() {
		return os!=null;
	}

    public void send(String info) {
	    if(this.os==null){
            openStream();
        }
		try {
			os.write(info.getBytes());
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
