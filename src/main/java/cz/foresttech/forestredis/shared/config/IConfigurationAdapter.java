package cz.foresttech.forestredis.shared.config;

import java.util.List;

public interface IConfigurationAdapter {

    void setup(String fileName);

    boolean isSetup();

    String getString(String path, String def);

    int getInt(String path, int def);

    boolean getBoolean(String path, boolean def);

    List<String> getStringList(String path);

}
