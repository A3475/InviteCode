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
        Calendar cld = Calendar.getInstance();
        int year = cld.get(Calendar.YEAR);// 当前年数
        int month = cld.get(Calendar.MONTH);// 当前月数
        int day = cld.get(Calendar.DAY_OF_MONTH);// 当前天数
        Player player=event.getPlayer();
        if (Plugin.getConfig().get("PlayerInviteTime."+player.getName())-getTotal(year, month, day)>=1){
            Plugin.getConfig().set("PlayerInviteData."+player.getName(),Plugin.getConfig().getInt("PlayerInviteData."+player.getName())+1);
            Plugin.getConfig().set("PlayerInviteTime."+player.getName(),getTotal(year, month, day));
        }
    }
}
