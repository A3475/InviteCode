package com.licrafter.inviteCode;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by lijx on 16/4/24.
 */
public class PlayerListener implements Listener {

    private InviteCode plugin;

    public PlayerListener(InviteCode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLoginListener(PlayerJoinEvent event) {
        boolean show = plugin.getConfig().getBoolean("setting.login_show");
        if (show) {
            plugin.getServer().dispatchCommand(event.getPlayer(), "invite me");
        }
    }

}
