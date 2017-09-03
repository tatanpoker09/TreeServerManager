package com.eilers.tatanpoker09.tsm.commands;

import com.eilers.tatanpoker09.tsm.commandmanagement.BaseCommand;
import com.eilers.tatanpoker09.tsm.commandmanagement.CommandTrigger;
import com.eilers.tatanpoker09.tsm.commandmanagement.SubCommand;
import com.eilers.tatanpoker09.tsm.peripherals.BluetoothManager;
import com.eilers.tatanpoker09.tsm.server.Tree;

public class BluetoothCommand extends BaseCommand {
    private static final String TOPIC = "server/peripheral/bluetooth";
    public BluetoothCommand() {
        super(TOPIC);
    }

    @Override
    public void setup() {
        CommandTrigger searchTrigger = new CommandTrigger() {
            @Override
            public void call(String topic, String[] info) {
                BluetoothManager bm = Tree.getServer().getpManager().getBtManager();
                bm.discoverDevices();
                bm.publishDevices();
            }

            @Override
            public String buildCallback(String... args) {
                return null;
            }
        };
        SubCommand searchCmd = new SubCommand("search", searchTrigger, this);

        CommandTrigger retrieveTrigger = new CommandTrigger() {
            @Override
            public void call(String topic, String[] args) {
                BluetoothManager bm = Tree.getServer().getpManager().getBtManager();
                bm.publishDevices();
            }

            @Override
            public String buildCallback(String... args) {
                return null;
            }
        };
        SubCommand retrieveCmd = new SubCommand("retrieve", retrieveTrigger, this);
        addSubCommand(retrieveCmd);
        addSubCommand(searchCmd);
    }

    @Override
    public void defaultTrigger(String topic, String[] args) {

    }
}
