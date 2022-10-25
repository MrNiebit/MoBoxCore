package org.mossmc.mosscg.MoBoxCore.Game;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.FileUtil;
import org.bukkit.util.StringUtil;
import org.mossmc.mosscg.MoBoxCore.Bungee.BungeeChannel;
import org.mossmc.mosscg.MoBoxCore.Bungee.BungeeTeleport;
import org.mossmc.mosscg.MoBoxCore.Listener.ListenerInventoryClick;
import org.mossmc.mosscg.MoBoxCore.Listener.ListenerPlayerInteract;
import org.mossmc.mosscg.MoBoxCore.Main;

import javax.annotation.processing.Processor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GameEnd {
    public static void startEnd() {
        boolean gameEndServerTeleport = GameBasicInfo.getGame.gameEndServerTeleport();
        if (gameEndServerTeleport) {
            BungeeChannel.initBungeeChannel();
            Bukkit.getPluginManager().registerEvents(new ListenerPlayerInteract(), Main.instance);
            Bukkit.getPluginManager().registerEvents(new ListenerInventoryClick(), Main.instance);
        }
        if (gameEndServerTeleport) {
            Bukkit.broadcastMessage(ChatColor.YELLOW+"游戏已结束！20秒后将自动匹配下一场游戏！");
        } else {
            Bukkit.broadcastMessage(ChatColor.YELLOW+"游戏已结束！服务器将在30秒后重启！");
        }
        new BukkitRunnable(){
            @Override
            public void run() {
                if (gameEndServerTeleport) {
                    Bukkit.broadcastMessage(ChatColor.YELLOW+"游戏已结束！10秒后将自动匹配下一场游戏！");
                } else {
                    Bukkit.broadcastMessage(ChatColor.YELLOW+"游戏已结束！服务器将在20秒后重启！");
                }
            }
        }.runTaskLater(Main.instance,20*10);
        new BukkitRunnable(){
            @Override
            public void run() {
                if (gameEndServerTeleport) {
                    Bukkit.broadcastMessage(ChatColor.GREEN+"正在匹配下一场游戏......");
                } else {
                    Bukkit.broadcastMessage(ChatColor.YELLOW+"游戏已结束！服务器将在10秒后重启！");
                }
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (gameEndServerTeleport) {
                        BungeeTeleport.nextGameTeleport(player);
                    }
                });
            }
        }.runTaskLater(Main.instance,20*20);
        new BukkitRunnable(){
            @Override
            public void run() {
                if (gameEndServerTeleport) {
                    Bukkit.broadcastMessage(ChatColor.GREEN+"正在匹配下一场游戏......");
                } else {
                    Bukkit.broadcastMessage(ChatColor.RED+"游戏已结束！服务器将在5秒后重启！");
                }
            }
        }.runTaskLater(Main.instance,20*25);
        new BukkitRunnable(){
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (gameEndServerTeleport) {
                        player.kickPlayer(ChatColor.GREEN + "游戏已结束！你已被强制返回大厅服务器！");
                    } else {
                        player.kickPlayer(ChatColor.GREEN + "游戏已结束！服务器已重启！");
                    }

                });
                reloadServer();
            }
        }.runTaskLater(Main.instance,20*30);
    }

    private static void reloadServer() {
//        String projectPath = System.getProperty("user.dir");
//        Main.logger.info("base path: " + projectPath);
//        String world = projectPath + "/world";
//        String worldNether = projectPath + "/world_nether";
//        String worldEnd = projectPath + "/world_the_end";
//        deletefile(world);
//        deletefile(worldNether);
//        deletefile(worldEnd);
        boolean randomMap = Main.getConfig.getBoolean("randomMap");
        if (randomMap) {
            Bukkit.shutdown();
            return;
        }
        Bukkit.reload();
    }

    public static void deletefile(String delpath){
        File file = new File(delpath);
        if (!file.exists()) {
            return;
        }
        Main.logger.info("删除路径：" + delpath);
        // 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
        if (!file.isDirectory()) {
            file.delete();
        } else if (file.isDirectory()) {
            String[] filelist = file.list();
            if (filelist == null || filelist.length == 0) {
                file.delete();
                return;
            }
            for (int i = 0; i < filelist.length; i++) {
                File delfile = new File(delpath + "/" + filelist[i]);
                if (!delfile.isDirectory()) {
                    delfile.delete();
                } else if (delfile.isDirectory()) {
                    deletefile(delpath + "/" + filelist[i]);
                }
            }
            file.delete();
        }

    }
}
