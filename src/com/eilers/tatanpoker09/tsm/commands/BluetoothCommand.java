package com.eilers.tatanpoker09.tsm.commands;

import com.eilers.tatanpoker09.tsm.LightSection;
import com.eilers.tatanpoker09.tsm.commandmanagement.BaseCommand;
import com.eilers.tatanpoker09.tsm.commandmanagement.CommandTrigger;
import com.eilers.tatanpoker09.tsm.commandmanagement.SubCommand;
import com.eilers.tatanpoker09.tsm.peripherals.BluetoothManager;
import com.eilers.tatanpoker09.tsm.peripherals.Peripheral;
import com.eilers.tatanpoker09.tsm.server.MQTTManager;
import com.eilers.tatanpoker09.tsm.server.Tree;
import net.sf.xenqtt.client.PublishMessage;
import net.sf.xenqtt.message.QoS;

public class BluetoothCommand extends BaseCommand {
    private static final String TOPIC = "manager/bluetooth";
    public BluetoothCommand() {
        super(TOPIC);
    }

    @Override
    public void setup() {

        CommandTrigger searchTrigger = new CommandTrigger() {
            @Override
            public void call(String topic, String[] info) {
                BluetoothManager bm = Tree.getServer().getbManager();
                bm.discoverDevices();
            }
        };
        SubCommand searchCmd = new SubCommand("search", searchTrigger);

        CommandTrigger retrieveTrigger = new CommandTrigger() {
            @Override
            public void call(String topic, String[] args) {
                byte[][] deviceBytes = BluetoothManager.convertToBytes(Tree.getServer().getbManager().getFoundDevices());
                for(byte[] array : deviceBytes) {
                    MQTTManager.getClient().publish(new PublishMessage("manager/bluetooth/devices", QoS.AT_LEAST_ONCE, array));
                }
            }
        };
        SubCommand retrieveCmd = new SubCommand("retrieve", retrieveTrigger);
        addSubCommand(retrieveCmd);
        addSubCommand(searchCmd);
    }

    @Override
    public void defaultTrigger(String topic, String[] args) {

    }
}
