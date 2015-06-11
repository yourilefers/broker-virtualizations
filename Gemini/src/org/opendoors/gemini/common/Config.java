package org.opendoors.gemini.common;

import java.io.*;
import java.util.HashMap;

/**
 * Contributors:
 * yourilefers
 *
 * @since v1.0
 */
public class Config {

    /** The Config instance */
    private static Config instance;

    /** The config elements */
    private HashMap<String, String> config;

    /**
     * Private Config constructor. Will setup some stuff.
     */
    private Config() {

        // Check the config file for existance
        File configFile = new File(Constants.CONFIG_LOCATION);

        // Check the file
        if(!configFile.exists() || !configFile.canRead()) {
            Logger.error("IO exception: Could not open/read the config file.");
            return;
        }

        try {

            // Setup reader
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            config = new HashMap<String, String>();
            String text;

            try {

                // Read line by line
                while ((text = reader.readLine()) != null) {

                    // Parse the line and add it to the config
                    parseConfigLine(text);

                }

            } catch (Exception e) {
                Logger.error("IO exception: Could not open/read the config file.");
            } finally {
                try {

                    // Close the reader
                    reader.close();

                } catch (IOException e) {
                    Logger.error("IO exception: Could not close the reader stream.");
                }
            }

        } catch(FileNotFoundException e) {
            Logger.error("Could not find the config file.");
        }
    }

    /**
     * Get a (new) instance of Config.
     *
     * @return The Config instance
     */
    public static Config getInstance() {
        if(instance == null) {
            instance = new Config();
        }
        return instance;
    }

    /**
     * Get wehter config has been initialized
     * @return
     */
    public static boolean isInitialized() {
        return instance != null;
    }

    /**
     * Override the toString() method for printing the current config.
     *
     * @return
     */
    @Override
    public String toString() {
        String ret = "Complete config:";
        if(config != null) {
            for (int i = 0; i < config.size(); i++) {
                String key = (String) config.keySet().toArray()[i];
                ret += "\n" + key + " = " + config.get(key);
            }
        }
        ret += "\nEOC";
        return ret;
    }

    /**
     * Parse a line of the config file.
     *
     * @param line
     */
    private void parseConfigLine(String line) {

        // Trim the line
        line = line.trim();

        // Comment line?
        if(line.startsWith("#")) {
            return;
        }

        // Get the '=' sign for key-value
        String[] splitted = line.split("=", 2);

        // Correct?
        if(splitted.length == 2 && !splitted[0].trim().isEmpty() && !splitted[1].trim().isEmpty()) {

            // Save the values
            config.put(splitted[0].trim(), splitted[1].trim());

        } else if(splitted.length == 1 && !splitted[0].trim().isEmpty()) {

            // Save the values
            config.put(splitted[0].trim(), "");

        }
    }

    /**
     * Get a value from config. The default will be null.
     *
     * @param key The key
     * @return
     */
    public String get(String key) {
        return get(key, null);
    }

    /**
     * Get a value from the config with a default value.
     *
     * @param key The key
     * @param def The default value
     * @return
     */
    public String get(String key, String def) {
        return config == null ? null : config.getOrDefault(key, def);
    }

}
