package dev.glowstudent.test;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("rtp").setExecutor(new CustomEventListener());
    }

}
