package com.eilers.tatanpoker09.tsm.peripherals;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.StreamConnection;

import com.eilers.tatanpoker09.tsm.server.Tree;
import com.intel.bluetooth.MicroeditionConnector;

public class Peripheral extends Thread{
    private String type;
    private RemoteDevice btDevice; //Bluetooth.
    private PeripheralStatus status;
    private DataOutputStream os;
    
    
    public Peripheral(String type){
        this.type = type;
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
		StreamConnection sc;
		try {
			sc = (StreamConnection)MicroeditionConnector.open(serverURL);
			DataOutputStream os = sc.openDataOutputStream();
			this.os = os;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeStream(){
    	if(this.os!=null){
            try {
                this.os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Stream is already closed!");
        }
	}
	public void openStream() {
		openStream(getServerURL());
	}
	
	public boolean isConnected() {
		return os!=null;
	}
	
	
	public void send(String info) {
		try {
			os.write(info.getBytes());
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public static Peripheral getByName(String type, String name) {
	    Peripheral p = new Peripheral(type);
	    for(RemoteDevice device : Tree.getServer().getbManager().getFoundDevices()){
            try {
                System.out.println(device.getFriendlyName(true)+","+name);
                if(device.getFriendlyName(true).equals(name)) {
                    System.out.println(3);
                    p.registerBtDevice(device);
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return p;
    }
}
