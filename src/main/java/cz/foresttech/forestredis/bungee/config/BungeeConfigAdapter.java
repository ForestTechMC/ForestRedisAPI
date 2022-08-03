package cz.foresttech.forestredis.bungee.config;

import cz.foresttech.forestredis.bungee.ForestRedisBungee;
import cz.foresttech.forestredis.shared.config.IConfigurationAdapter;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class BungeeConfigAdapter implements IConfigurationAdapter {

    private String fileName;
    private Configuration configuration;

    @Override
    public void setup(String fileName) {
        this.fileName = fileName;

        if (!ForestRedisBungee.getInstance().getDataFolder().exists()) {
            ForestRedisBungee.getInstance().getDataFolder().mkdir();
        }

        File file = new File(ForestRedisBungee.getInstance().getDataFolder(), fileName + ".yml");

        if (!file.exists()) {
            try (InputStream in = ForestRedisBungee.getInstance().getResourceAsStream(fileName + ".yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                ForestRedisBungee.getInstance().logger().warning("Cannot create config.yml! This proxy won't process any Redis communication!");
                return;
            }
            loadConfiguration();
        }
        loadConfiguration();
    }

    @Override
    public boolean isSetup() {
        return configuration != null;
    }

    @Override
    public void loadConfiguration() {
        try {
            configuration = ConfigurationProvider
                    .getProvider(YamlConfiguration.class)
                    .load(new File(ForestRedisBungee.getInstance().getDataFolder(), fileName + ".yml"));
        } catch (IOException e) {
            ForestRedisBungee.getInstance().logger().warning("Cannot load config.yml! This proxy won't process any Redis communication!");
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
