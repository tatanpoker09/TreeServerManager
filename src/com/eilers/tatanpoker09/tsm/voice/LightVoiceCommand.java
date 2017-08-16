package com.eilers.tatanpoker09.tsm.voice;

import com.eilers.tatanpoker09.tsm.LightSection;

import java.io.File;

public class LightVoiceCommand extends VoiceCommand{
    private LightSection section;
        private boolean status;

    protected LightVoiceCommand(File file) {
            super(file);
        setTrigger(getTrigger());
    }


    public void parse() {

    }

    public Runnable getTrigger() {
        return new Runnable() {
            public void run() {
                section.turn(status);
            }
        };
    }
}
