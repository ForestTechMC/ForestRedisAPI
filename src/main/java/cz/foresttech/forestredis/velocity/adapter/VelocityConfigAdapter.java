package cz.foresttech.forestredis.velocity.adapter;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Implementation of {@link cz.foresttech.forestredis.shared.adapter.IConfigurationAdapter}
 */
public class VelocityConfigAdapter implements cz.foresttech.forestredis.shared.adapter.IConfigurationAdapter {

    private final Path dataDirectory;
    private String fileName;
    private ConfigurationNode config;

    @Inject
    public VelocityConfigAdapter(@DataDirectory Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    @Override
    public void setup(String fileName) {
        this.fileName = fileName;

        Path configFile = dataDirectory.resolve(fileName + ".yml");

        try {
            if (!Files.exists(dataDirectory)) {
                Files.createDirectory(dataDirectory);
            }

            if (!Files.exists(configFile)) {
                try (InputStream in = getClass().getClassLoader().getResourceAsStream(fileName + ".yml")) {
                    if (in != null) {
                        Files.copy(in, configFile);
                    }
                }
            }
            loadConfiguration();
        } catch (IOException e) {
            // Handle the error
        }
    }

    @Override
    public boolean isSetup() {
        return config != null;
    }

    @Override
    public void loadConfiguration() {
        try {
            YAMLConfigurationLoader loader = YAMLConfigurationLoader.builder()
                .setPath(dataDirectory.resolve(fileName + ".yml"))
                .build();
            config = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getString(String path, String def) {
        return this.config.getNode((Object[]) path.split("\\.")).getString(def);
    }

    @Override
    public int getInt(String path, int def) {
        return this.config.getNode((Object[]) path.split("\\.")).getInt(def);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        return this.config.getNode((Object[]) path.split("\\.")).getBoolean(def);
    }

    @Override
    public List<String> getStringList(String path) {
        try {
            return this.config.getNode((Object[]) path.split("\\.")).getList(TypeToken.of(String.class));
        } catch (ObjectMappingException e) {
            throw new RuntimeException(e);
        }
    }

}
