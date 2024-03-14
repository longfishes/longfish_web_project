package com.longfish.helloworld;

import org.bukkit.plugin.java.JavaPlugin;

public final class HelloWorld extends JavaPlugin {

    @Override
    public void onEnable() {
        for (int i = 0; i < 10; i++) {
            System.out.println("######################");
        }
    }

    @Override
    public void onDisable() {
        System.out.println("Good Bye!");
    }
}
