package com.eilers.tatanpoker09.tsm.commands;

import com.eilers.tatanpoker09.tsm.LightSection;
import com.eilers.tatanpoker09.tsm.commandmanagement.BaseCommand;
import com.eilers.tatanpoker09.tsm.commandmanagement.CommandTrigger;
import com.eilers.tatanpoker09.tsm.commandmanagement.SubCommand;
import com.eilers.tatanpoker09.tsm.peripherals.BluetoothManager;
import com.eilers.tatanpoker09.tsm.peripherals.Peripheral;
import com.eilers.tatanpoker09.tsm.server.Tree;

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
        SubCommand createSc = new SubCommand("search", searchTrigger);
        addSubCommand(createSc);
    }

    @Override
    public void defaultTrigger(String topic, String[] args) {

    }
}
