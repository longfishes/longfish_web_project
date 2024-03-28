package com.longfish.helloworld;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class HelloWorld extends JavaPlugin {

    @Override
    public void onEnable() {
        for (int i = 0; i < 10; i++) {
            System.out.println("######################");
        }
        List<Player> players = (List<Player>) Bukkit.getOnlinePlayers();
        System.out.println(players);
    }

    @Override
    public void onDisable() {
        System.out.println("Good Bye!");
    }
}
