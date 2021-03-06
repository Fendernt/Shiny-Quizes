package shinyquizesplugin.shinyquizesplugin.Leaderboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import shinyquizesplugin.shinyquizesplugin.Mangers.Messengers.ServerCommunicator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static shinyquizesplugin.shinyquizesplugin.ShinyQuizesPlugin.PLUGIN;

public class PlayerWinManager {

    public static Map<UUID, Integer> playerWinData = new HashMap<>();

    public static Integer get(UUID key){
        playerWinData.putIfAbsent(key, 0);
        return playerWinData.get(key);
    }

    public static int getValue(UUID id){
        playerWinData.putIfAbsent(id, 0);
        return playerWinData.get(id);
    }

    public static void addWin(Player player, int amount){
        UUID uuid = player.getUniqueId();
        playerWinData.putIfAbsent(uuid, 0);

        int currentAmount = playerWinData.get(uuid);
        int newAmount = currentAmount + amount;
        playerWinData.put(uuid, newAmount);
    }

    public static void removeWin(Player player, int amount){
        UUID uuid = player.getUniqueId();
        if(playerWinData.get(uuid) != null){
            int currentAmount = playerWinData.get(uuid);
            int newAmount = currentAmount - amount;
            playerWinData.put(uuid, newAmount);
        } else playerWinData.put(uuid, 0);
    }

    public static void setWins(Player player, int amount){
        playerWinData.put(player.getUniqueId(), amount);
    }

    public static int getWins(Player player){
        playerWinData.putIfAbsent(player.getUniqueId(), 0);
        return playerWinData.get(player.getUniqueId());
    }


    public static void saveWinValues(){
        Properties properties = new Properties();

        for (Map.Entry<UUID, Integer> entry : playerWinData.entrySet()) {
            properties.put(entry.getKey().toString(), entry.getValue().toString());
        }

        File tempFile = new File(PLUGIN.getDataFolder().getAbsolutePath()+ "/playerdata/playerWinData.properties");
        try {
            if(checkIfFileExists()) tempFile.createNewFile(); // if file already exists will do nothing
        } catch (IOException e) {
        }

        try {
            properties.store(Files.newOutputStream(Paths.get(PLUGIN.getDataFolder().getAbsolutePath()+ "/playerdata/playerWinData.properties")), null);
        } catch (IOException e) {
            ServerCommunicator.sendConsoleMessage(ChatColor.RED+"Could not save player win data.");
        }
    }

    public static void loadWinValues(){
        if(!checkIfFileExists()) return;
        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(Paths.get(PLUGIN.getDataFolder().getAbsolutePath()+ "/playerdata/playerWinData.properties")));
        } catch (IOException e) {
            ServerCommunicator.sendConsoleMessage(ChatColor.RED+"Could not load player win data.");
        }

        for (String key : properties.stringPropertyNames()) {
            playerWinData.put(UUID.fromString(key), Integer.parseInt(properties.get(key).toString()));
        }
    }

    private static boolean checkIfFileExists(){
        File tempFile = new File(PLUGIN.getDataFolder().getAbsolutePath()+ "/playerdata/playerWinData.properties");
        return tempFile.exists();
    }

}
