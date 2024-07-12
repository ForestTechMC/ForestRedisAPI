package cz.foresttech.forestredis.velocity.adapter;

import cz.foresttech.forestredis.shared.adapter.IConfigurationAdapter;
import cz.foresttech.forestredis.velocity.ForestRedisVelocity;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link IConfigurationAdapter} for Velocity version
 */
public class VelocityConfigAdapter implements IConfigurationAdapter {

    private final ForestRedisVelocity plugin;
    private CommentedConfigurationNode configuration;

    private Path configPath;
    private YamlConfigurationLoader loader;

    /**
     * Constructs the instance of adapter.
     *
     * @param plugin {@link ForestRedisVelocity} instance
     */
    public VelocityConfigAdapter(ForestRedisVelocity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setup(String fileName) {
        if (Files.notExists(plugin.getDataDirectory())) {
            try {
                Files.createDirectory(plugin.getDataDirectory());
            } catch (IOException e) {
                plugin.logger().warning("Cannot create plugin directory! This proxy won't process any Redis communication!");
                return;
            }
        }

        configPath = plugin.getDataDirectory().resolve(fileName + ".yml");
        if (Files.notExists(configPath)) {
            try (InputStream stream = plugin.getClass().getClassLoader().getResourceAsStream(fileName + ".yml")) {
                Files.copy(stream, configPath);
            } catch (IOException e) {
                plugin.logger().warning("Cannot create config.yml! This proxy won't process any Redis communication!");
                return;
            }
        }

        loadConfiguration();
    }

    @Override
    public boolean isSetup() {
        return configuration != null;
    }

    @Override
    public void loadConfiguration() {
        loader = YamlConfigurationLoader.builder().path(configPath).build();
        try {
            configuration = loader.load();
        } catch (ConfigurateException e) {
            plugin.logger().warning("Cannot load config.yml! This proxy won't process any Redis communication!");
        }
    }

    @Override
    public String getString(String path, String def) {
        String result = configuration.node(path).getString();
        if (result == null) {
            return def;
        }
        return result;
    }

    @Override
    public int getInt(String path, int def) {
        return this.configuration.node(path).getInt(def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return this.configuration.node(path).getBoolean(def);
    }

    @Override
    public List<String> getStringList(String path) {
        try {
            return this.configuration.node(path).getList(String.class);
        } catch (SerializationException e) {
            return new ArrayList<>();
        }
    }

}
