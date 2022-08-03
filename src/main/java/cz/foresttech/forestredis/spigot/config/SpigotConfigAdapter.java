package cz.foresttech.forestredis.spigot.config;

import com.google.common.base.Charsets;
import cz.foresttech.forestredis.bungee.ForestRedisBungee;
import cz.foresttech.forestredis.shared.config.IConfigurationAdapter;
import cz.foresttech.forestredis.spigot.ForestRedisSpigot;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class SpigotConfigAdapter implements IConfigurationAdapter {

    private String fileName;
    private File file;
    private FileConfiguration configuration;

    @Override
    public void setup(String fileName) {
        this.fileName = fileName;

        if (!ForestRedisSpigot.getInstance().getDataFolder().exists()) {
            ForestRedisSpigot.getInstance().getDataFolder().mkdir();
        }

        file = new File(ForestRedisSpigot.getInstance().getDataFolder() + "/" + fileName + ".yml");
        if (!file.exists()) {
            ForestRedisSpigot.getInstance().logger().info("config.yml does not exist! Creating a new one!");
            ForestRedisSpigot.getInstance().saveResource(fileName + ".yml", false);
        }

        configuration = YamlConfiguration.loadConfiguration(file);
        loadConfiguration();
    }

    @Override
    public boolean isSetup() {
        return configuration != null;
    }

    @Override
    public void loadConfiguration() {
        try {
            configuration = YamlConfiguration.loadConfiguration(file);
            InputStream defConfigStream = ForestRedisSpigot.getInstance().getResource(fileName + ".yml");
            if (defConfigStream != null) {
                configuration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
            }
        } catch (Exception ex) {
            ForestRedisBungee.getInstance().logger().warning("Cannot load config.yml! This server won't process any Redis communication!");
            configuration = null;
        }
    }

    @Override
    public String getString(String path, String def) {
        return this.configuration.getString(path, def);
    }

    @Override
    public int getInt(String path, int def) {
        return this.configuration.getInt(path, def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return this.configuration.getBoolean(path, def);
    }

    @Override
    public List<String> getStringList(String path) {
        return this.configuration.getStringList(path);
    }

}
