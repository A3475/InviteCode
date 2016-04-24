package com.licrafter.inviteCode;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2016/1/26.
 */
public class InviteCode extends JavaPlugin {

    private FileConfiguration config;
    private DataConfiguration dataConfiguration;
    private FileConfiguration dataConfig;

    private DataConfiguration rewardConfiguration;
    private FileConfiguration rewardConfig;

    private PlayerListener listener;
    private PluginManager pm;

    @Override
    public void onEnable() {
        getLogger().info("Enabling InviteCode (版本:" + getDescription().getVersion() + " "
                + "作者:" + getDescription().getAuthors().get(0) + " )");
        getServer().getPluginCommand("invite").setExecutor(new PlayerCommands(this));
        listener = new PlayerListener(this);
        pm = getServer().getPluginManager();
        pm.registerEvents(listener, this);

        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
            reloadConfig();
        }
        config = getConfig();
        dataConfiguration = new DataConfiguration(this, "data.yml");
        dataConfig = dataConfiguration.getDataConfig();

        rewardConfiguration = new DataConfiguration(this, "reward.yml");
        rewardConfig = rewardConfiguration.getDataConfig();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        dataConfiguration.saveDataConfig();
        rewardConfiguration.saveDataConfig();
    }


    public FileConfiguration getDataConfig() {
        return dataConfig;
    }

    public FileConfiguration getRewardConfig() {
        return rewardConfig;
    }

    public void addCode(UUID player, String code) {
        dataConfig.set(code + ".owner", player.toString());
        dataConfiguration.saveDataConfig();
    }

    public boolean areadlyHaseCode(UUID player) {
        Iterator<String> iterator = dataConfig.getKeys(false).iterator();
        while (iterator.hasNext()) {
            String code = iterator.next();
            if (dataConfig.getString(code + ".owner").equals(player.toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean addFriend(String player, String code) {
        if (!dataConfig.getStringList(code + ".invited").contains(player)) {
            List<String> list = dataConfig.getStringList(code + ".invited");
            list.add(player);
            dataConfig.set(code + ".invited", list);
            dataConfiguration.saveDataConfig();
            return true;
        } else {
            return false;
        }
    }

    public void addRewardCount(String code) {
        String player = getServer().getPlayer(UUID.fromString(dataConfig.getString(code + ".owner"))).getName();
        int rewardCount = rewardConfig.getInt(player, 0);
        rewardConfig.set(player, rewardCount + 1);
        rewardConfiguration.saveDataConfig();
    }

    public void clearReward(String player) {
        rewardConfig.set(player, null);
        rewardConfiguration.saveDataConfig();
    }

    public String getCode(UUID player) {
        Iterator<String> iterator = dataConfig.getKeys(false).iterator();
        while (iterator.hasNext()) {
            String code = iterator.next();
            if (dataConfig.getString(code + ".owner").equals(player.toString())) {
                return code;
            }
        }
        return null;
    }

    public List<String> getInvitedList(String code) {
        return dataConfig.getStringList(code + ".invited");
    }

    public int getKeyLenght() {
        return config.getInt("setting.key_length");
    }

    public boolean getIsBroadCast() {
        return config.getBoolean("setting.broadcast");
    }

    public String getBroadCastMsg() {
        return config.getString("setting.message");
    }

    public boolean isRewardBoth() {
        return config.getBoolean("setting.reward_both");
    }

    public boolean checkId() {
        return config.getBoolean("setting.check_ip");
    }

    public Player getInviter(String code) {
        return getServer().getPlayer(UUID.fromString(dataConfig.getString(code + ".owner")));
    }

}
