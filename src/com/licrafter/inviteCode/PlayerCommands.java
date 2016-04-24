package com.licrafter.inviteCode;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/1/26.
 */
public class PlayerCommands implements CommandExecutor {

    private InviteCode plugin;
    public static String[] letras = {"A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "T", "U", "V", "W", "X", "Y", "Z"};


    public PlayerCommands(InviteCode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //生成邀请码
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "[邀请码]只有玩家才可以执行该插件多命令");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 1 && args[0].equals("code")) {

            if (!plugin.areadlyHaseCode(player.getUniqueId())) {
                String code = formatKey();
                player.sendMessage(ChatColor.AQUA + "[邀请码]你的邀请码为: " + ChatColor.YELLOW + code);
                player.sendMessage(ChatColor.AQUA + "[邀请码]被邀请人输入/invite confirm [邀请码] 确定被邀请");
                player.sendMessage(ChatColor.AQUA + "[邀请码]邀请朋友加入服务器会得到奖励哦!");
                plugin.addCode(player.getUniqueId(), code);
            } else {
                String code = plugin.getCode(player.getUniqueId());
                player.sendMessage(ChatColor.AQUA + "[邀请码]你已经生成了邀请码: " + ChatColor.YELLOW + code);
                player.sendMessage(ChatColor.AQUA + "[邀请码]被邀请人输入 " + ChatColor.YELLOW + "/invite confirm [邀请码]"
                        + ChatColor.AQUA + " 确定被邀请");
            }
            return true;
        }

        if (args.length == 2 && args[0].equals("confirm")) {
            Iterator iterator = plugin.getDataConfig().getKeys(false).iterator();
            String code = args[1];
            while (iterator.hasNext()) {
                if (code.equals(iterator.next())) {
                    //check ip
                    if (plugin.checkId() && checkIp(player, plugin.getInviter(code))) {
                        player.sendMessage(ChatColor.RED + "[邀请码]邀请者和被邀请者的IP地址相同,禁止被邀请!");
                        return true;
                    }
                    if (plugin.addFriend(player.getName(), code)) {
                        player.sendMessage(ChatColor.AQUA + "[邀请码]你已经接受邀请");
                        //是否全局发送消息
                        if (plugin.getIsBroadCast()) {
                            plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&'
                                    , plugin.getBroadCastMsg().replaceAll("%invite%", plugin.getInviter(code).getName())
                                            .replaceAll("%player%", player.getName())));
                        }
                        //是否给予被邀请者奖励
                        if (plugin.isRewardBoth()) {
                            rewardAnother(player.getName());
                            player.sendMessage(ChatColor.AQUA + "[邀请码]你已经收到被邀请的奖励");
                        }
                        //增加这个邀请码的邀请人的奖品个数
                        plugin.addRewardCount(code);
                    } else {
                        player.sendMessage(ChatColor.RED + "[邀请码]你已经接受过邀请了,请不要再次接受");
                    }
                    return true;
                }
            }
            player.sendMessage(ChatColor.RED + "[邀请码]你输入的邀请码错误,请询问你的邀请人正确的邀请码!");
            return true;
        }

        if (args.length == 1 && args[0].equals("me")) {
            String code = plugin.getCode(player.getUniqueId());
            if (code == null) {
                return true;
            }
            List<String> inviteds = plugin.getInvitedList(code);
            player.sendMessage(ChatColor.GREEN + "========== " + player.getName() + " 的邀请记录 ==========");
            player.sendMessage(ChatColor.GREEN + "邀请朋友数量: " + inviteds.size());
            for (int i = 0; i < inviteds.size(); i++) {
                player.sendMessage(ChatColor.GREEN + String.valueOf(i) + ". " + ChatColor.YELLOW + inviteds.get(i));
            }
            return true;
        }
        //邀请码所有人获取奖励
        if (args.length == 1 && args[0].equals("reward")) {
            int count = plugin.getRewardConfig().getInt(player.getName(), 0);
            if (count > 0) {
                reward(player.getName());
                plugin.clearReward(player.getName());
                player.sendMessage(ChatColor.AQUA + "[邀请码]你已经从银行取出所有的邀请奖励,再接再厉哦!");
            } else {
                player.sendMessage(ChatColor.AQUA + "[邀请码]你银行里的邀请奖励是空气~~,继续努力宣传服务器吧!");
            }
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "===============" + plugin.getConfig().getString("setting.title") + "===============");
        player.sendMessage(ChatColor.GREEN + "/invite code     " + ChatColor.GRAY + "- 生成唯一的邀请码");
        player.sendMessage(ChatColor.GREEN + "/invite confirm [邀请码]     " + ChatColor.GRAY + "- 被邀请人确认被邀请");
        player.sendMessage(ChatColor.GREEN + "/invite me     " + ChatColor.GRAY + "- 查看自己邀请的朋友记录");
        player.sendMessage(ChatColor.GREEN + "/invite reward     " + ChatColor.GRAY + "- 取出自己邀请朋友得到的奖励");
        return true;
    }

    public String formatKey() {
        String key = "";
        int t = 0;
        Random n = new Random();
        int tmax = plugin.getKeyLenght();
        if ((tmax < 1) || (tmax > 10)) {
            tmax = 10;
        }
        while (t < tmax) {
            switch (n.nextInt(2)) {
                case 0:
                    key = key + letras[n.nextInt(letras.length)];
                    break;
                case 1:
                    key = key + String.valueOf(n.nextInt(9) + 1);
            }
            t++;
        }
        return key;
    }

    private void rewardAnother(String player) {
        List<String> rewards = plugin.getConfig().getStringList("setting.reward_another.item");
        for (String reward : rewards) {
            String[] array = reward.split(",");
            plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "give " + player + " " + array[0] + " " + array[1]);
        }
        int money = plugin.getConfig().getInt("setting.reward_another.money");
        if (money > 0) {
            plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "eco give " + player + " " + money);
        }
    }

    private void reward(String player) {
        List<String> rewards = plugin.getConfig().getStringList("setting.reward.item");
        for (String reward : rewards) {
            String[] array = reward.split(",");
            plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "give " + player + " " + array[0] + " " + array[1]);
        }
        int money = plugin.getConfig().getInt("setting.reward.money");
        if (money > 0) {
            plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "eco give " + player + " " + money);
        }
    }

    private boolean checkIp(Player gesture, Player inviter) {
        InetSocketAddress gestureIp = gesture.getAddress();
        InetSocketAddress inviterIp = inviter.getAddress();
        if (gestureIp == inviterIp) {
            plugin.getLogger().info("Guest and inviter have the same IP!");
            return true;
        }
        return false;
    }
}
