package com.eilers.tatanpoker09.tsm.plugins;

import com.eilers.tatanpoker09.tsm.server.Tree;

import java.util.logging.Logger;

/**
 * Created by tatanpoker09 on 23-08-2017.
 */
public abstract class TreePlugin {
    private String name;
    private String author;


    public abstract void onEnable();

    public abstract void onDisable();

    protected void load() {
        Logger log = Tree.getLog();
        log.info("Initizalizing " + name + "...");
        onEnable();
    }

    protected void unload() {
        onDisable();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}