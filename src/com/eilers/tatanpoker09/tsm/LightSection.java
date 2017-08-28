package com.eilers.tatanpoker09.tsm;

import com.eilers.tatanpoker09.tsm.database.DatabaseManager;
import com.eilers.tatanpoker09.tsm.peripherals.Peripheral;
import com.eilers.tatanpoker09.tsm.server.MQTTManager;
import com.eilers.tatanpoker09.tsm.server.Tree;
import net.sf.xenqtt.client.PublishMessage;
import net.sf.xenqtt.client.Subscription;
import net.sf.xenqtt.message.QoS;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LightSection {
    private static List<LightSection> lights;

    private Peripheral peripheral;
    private String name;
    private int permissionLevel;
    private boolean connected;

    public LightSection(String name){
        this.name = name;
    }

    public LightSection(String name, Peripheral peripheral, int permissionLevel) {
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
        dm.executeCommand("CREATE TABLE IF NOT EXISTS `Tree`.`LightSections` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(40) NOT NULL , `device_id` VARCHAR(36) NOT NULL , `device_name` VARCHAR(40) NOT NULL , `permission_level` INT(32) NOT NULL,PRIMARY KEY (`id`)) ENGINE = InnoDB;");
        loadFromDatabase();
    }

    private static void loadFromDatabase() {
        Logger log = Tree.getLog();

        DatabaseManager dm = Tree.getServer().getdManager();
        ResultSet rs = dm.getFromDatabase("SELECT * FROM `Tree`.`LightSections`");

        if (rs == null) {
            log.severe("There was an error loading up LightSections from the Database!");
            return;
        }
        try {
            while (rs.next()) {
                String name = rs.getString("name");
                Peripheral peripheral = Peripheral.getByAddress("Lights", rs.getString("device_id"));
                int permissionLevel = rs.getInt("permission_level");

                LightSection ls = new LightSection(name, peripheral, permissionLevel);
                ls.localRegister();
                ls.attemptConnect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<LightSection> getLightSections() {
        return lights;
    }

    public static void publishLights() {
        for (LightSection lights : lights) {
            String payload = lights.name + "," + lights.permissionLevel;
            MQTTManager.getClient().publish(new PublishMessage("server/modules/lights/retrieve_callback", QoS.AT_LEAST_ONCE, payload));
        }
    }

    public boolean attemptConnect() {
        Tree.getLog().info("Attempting to connect via bluetooth.");
        try {
            peripheral.openStream();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void turn(boolean on) {
        if(on) {
            peripheral.send("PrenderLED");
        } else {
        	peripheral.send("ApagarLED");
        }
    }

    public void localRegister() {
        Logger log = Tree.getLog();
        if(lights==null){
            lights = new ArrayList<LightSection>();
        }
        lights.add(this);
        Tree.getServer().getpManager().addPeripheral(getPeripheral());
        MQTTManager.getClient().subscribe(new Subscription[]{new Subscription("server/modules/lights/" + this.name, QoS.AT_LEAST_ONCE)});
        log.info("Lights registered locally!");
    }

    public void databaseRegister() {
        Logger log = Tree.getLog();
        DatabaseManager dm = Tree.getServer().getdManager();
        PreparedStatement ps = dm.prepareStatement("INSERT INTO `Tree`.`LightSections` (name, device_id, device_name, permission_level) VALUES (?,?,?,?)");
        if (ps != null) {
            try {
                ps.setString(1, this.name);
                ps.setString(2, peripheral.getBtDevice().getBluetoothAddress());
                ps.setString(3, peripheral.getBtDevice().getFriendlyName(true));
                ps.setInt(4, permissionLevel);
                ps.executeUpdate();
                log.info("Added lightsection to the database succesfully");
                return;
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.severe("There was an error inserting the LightSection into the database.");
        } else {
            log.severe("There was an error creating the SQL syntax to insert the LightSection.");
        }
    }

    public Peripheral getPeripheral() {
        return peripheral;
    }

    public void register() {
        localRegister();
        databaseRegister();
    }
}