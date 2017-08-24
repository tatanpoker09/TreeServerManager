package com.eilers.tatanpoker09.tsm;

import com.eilers.tatanpoker09.tsm.database.DatabaseManager;
import com.eilers.tatanpoker09.tsm.peripherals.Peripheral;
import com.eilers.tatanpoker09.tsm.server.MQTTManager;
import com.eilers.tatanpoker09.tsm.server.Tree;
import net.sf.xenqtt.client.Subscription;
import net.sf.xenqtt.message.QoS;

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

    public static LightSection getByName(String lsname) {
        for (LightSection ls : lights) {
            if (ls.name.equals(lsname)) {
                return ls;
            }
        }
        return null;
    }

    public static void setupDatabase() {
        DatabaseManager dm = Tree.getServer().getdManager();
        dm.executeCommand("CREATE TABLE IF NOT EXISTS `tree`.`LightSections` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(40) NOT NULL , `device_id` VARCHAR(36) NOT NULL , `device_name` VARCHAR(40) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;");
    }

    public void turn(boolean on){
        peripheral.openStream();
        if(on) {
        	peripheral.send("PrenderLED");
        } else {
        	peripheral.send("ApagarLED");
        }
        peripheral.closeStream();
    }

    public void register() {
        if(lights==null){
            lights = new ArrayList<LightSection>();
        }
        lights.add(this);
        MQTTManager.getClient().subscribe(new Subscription[]{new Subscription("module/lights/"+this.name, QoS.AT_LEAST_ONCE)});

        DatabaseManager dm = Tree.getServer().getdManager();
        dm.executeCommand("INSERT INTO ");
        //TODO Add to the database.
    }

    public Peripheral getPeripheral() {
        return peripheral;
    }
}