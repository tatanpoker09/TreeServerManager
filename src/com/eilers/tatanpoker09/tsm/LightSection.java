package com.eilers.tatanpoker09.tsm;

import com.eilers.tatanpoker09.tsm.database.DatabaseManager;
import com.eilers.tatanpoker09.tsm.peripherals.Peripheral;
import com.eilers.tatanpoker09.tsm.server.MQTTManager;
import com.eilers.tatanpoker09.tsm.server.Tree;
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
        dm.executeCommand("CREATE TABLE IF NOT EXISTS `tree`.`LightSections` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(40) NOT NULL , `device_id` VARCHAR(36) NOT NULL , `device_name` VARCHAR(40) NOT NULL , `permission_level` INT(32) NOT NULL,PRIMARY KEY (`id`)) ENGINE = InnoDB;");
        loadFromDatabase();
    }

    private static void loadFromDatabase() {
        Logger log = Tree.getLog();

        DatabaseManager dm = Tree.getServer().getdManager();
        ResultSet rs = dm.getFromDatabase("SELECT * FROM `tree`.`LightSections`");

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
                ls.register();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        Logger log = Tree.getLog();
        if(lights==null){
            lights = new ArrayList<LightSection>();
        }
        lights.add(this);
        MQTTManager.getClient().subscribe(new Subscription[]{new Subscription("module/lights/"+this.name, QoS.AT_LEAST_ONCE)});

        DatabaseManager dm = Tree.getServer().getdManager();
        PreparedStatement ps = dm.prepareStatement("INSERT INTO `tree`.`LightSections` (name, device_id, device_name, permission_level) VALUES (?,?,?,?)");
        if (ps != null) {
            try {
                ps.setString(0, this.name);
                ps.setString(1, peripheral.getBtDevice().getBluetoothAddress());
                ps.setString(2, peripheral.getBtDevice().getFriendlyName(true));
                ps.setInt(3, permissionLevel);
                ps.executeUpdate();
                log.info("Added lightsection succesfully");
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
}