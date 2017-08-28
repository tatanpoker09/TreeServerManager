package com.eilers.tatanpoker09.tsm.commands;

import com.eilers.tatanpoker09.tsm.LightSection;
import com.eilers.tatanpoker09.tsm.commandmanagement.BaseCommand;
import com.eilers.tatanpoker09.tsm.commandmanagement.CommandTrigger;
import com.eilers.tatanpoker09.tsm.commandmanagement.SubCommand;
import com.eilers.tatanpoker09.tsm.peripherals.Peripheral;

public class LightsCommand extends BaseCommand {
    private static final String TOPIC = "server/modules/lights";

    public LightsCommand() {
		super(TOPIC);
	}

    public void defaultTrigger(String topic, String[] args) {
        String lightsection = topic.replace(TOPIC+"/", "");
        LightSection lsection = LightSection.getByName(lightsection);
        boolean on = Boolean.parseBoolean(args[0]);
        lsection.turn(on);
    }
    /**
	 * Used to load subcommands.
	 */
	@Override
	public void setup() {
        CommandTrigger createTrigger = new CommandTrigger() {
            @Override
            public void call(String topic, String[] info) {
                Peripheral p = Peripheral.getByName("Lights", info[1]);
                LightSection ls = new LightSection(info[0], p, Integer.parseInt(info[2]));
                ls.register();
                ls.attemptConnect();
            }
        };
        SubCommand createSc = new SubCommand("create", createTrigger);
        addSubCommand(createSc);

        CommandTrigger retrieveTrigger = new CommandTrigger() {
            @Override
            public void call(String topic, String[] args) {
                LightSection.publishLights();
            }
        };
    }
}
