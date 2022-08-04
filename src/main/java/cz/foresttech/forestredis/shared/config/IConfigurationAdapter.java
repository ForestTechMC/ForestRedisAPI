package cz.foresttech.forestredis.shared.config;

import java.util.List;

/**
 * ConfigurationAdapter interface which handles differences between BungeeCord and Spigot in configuration structure.
 */
public interface IConfigurationAdapter {

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Setups the configuration file by provided name.
     *
     * @param fileName Name of the file to setup
     */
    void setup(String fileName);

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Checks if the configuration is setup
     *
     * @return If the configuration is setup
     */
    boolean isSetup();

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Loads the configuration
     */
    void loadConfiguration();

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Returns the String value from the configuration.
     *
     * @param path Path in the configuration.
     * @param def  Default value if the path is not set.
     * @return String value from the configuration. Returns "def" if path is not available.
     */
    String getString(String path, String def);

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Returns the int value from the configuration.
     *
     * @param path Path in the configuration.
     * @param def  Default value if the path is not set.
     * @return int value from the configuration. Returns "def" if path is not available.
     */
    int getInt(String path, int def);

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Returns the boolean value from the configuration.
     *
     * @param path Path in the configuration.
     * @param def  Default value if the path is not set.
     * @return boolean value from the configuration. Returns "def" if path is not available.
     */
    boolean getBoolean(String path, boolean def);

    /*----------------------------------------------------------------------------------------------------------*/

    /**
     * Returns the list of Strings from the configuration.
     *
     * @param path Path in the configuration.
     * @return list of strings from the configuration. Returns empty list if path is not available.
     */
    List<String> getStringList(String path);

}
