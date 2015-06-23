package org.opendoors.gemini.common;

/**
 * Contributors:
 * yourilefers
 *
 * @since v1.0
 */
public class Constants {

    /**
     * The system version of Gemini (build number).
     */
    public static final int VERSION = 1;

    /**
     * The human readable version number.
     */
    public static final String VERSION_HUMAN = "0.1";

    //
    // Config
    //

    /**
     * The config file location and name
     */
    public static final String CONFIG_LOCATION = "/etc/gemini/config.conf";

    /**
     * The debug level for Gemini.
     */
    public static final String CONFIG_DEBUG = "debug";

    /**
     * The location of the log file.
     */
    public static final String CONFIG_LOG_LOCATION = "log_file";

    /**
     * Orion URL. The URL of Orion.
     */
    public static final String CONFIG_ORION_URL = "orion_url";

    /**
     * The entities Orion may fetch.
     */
    public static final String CONFIG_ORION_ENTITIES = "orion_entities";

    /**
     * The URL of the internal server.
     */
    public static final String CONFIG_SERVER_URL = "server_url";

    /**
     * The port of the internal server.
     */
    public static final String CONFIG_SERVER_PORT = "server_port";

    /**
     * The URL of CKAN.
     */
    public static final String CONFIG_CKAN_URL = "ckan_url";

    /**
     * The organization that the server writes to in CKAN.
     */
    public static final String CONFIG_CKAN_ORGANIZATION = "ckan_organization";

    /**
     * The CKAN API key.
     */
    public static final String CONFIG_CKAN_API_KEY = "ckan_api_key";

    //
    // Config defaults
    //

    /**
     * The default for the debug level.
     */
    public static final String CONFIG_DEBUG_DEFAULT = "info";

    /**
     * The default logfile location.
     */
    public static final String CONFIG_LOG_LOCATION_DEFAULT = "gemini.log";

    /**
     * The default URL of the internal server.
     */
    public static final String CONFIG_SERVER_URL_DEFAULT = "http://localhost";

    /**
     * The default port of the internal server.
     */
    public static final int CONFIG_SERVER_PORT_DEFAULT = 2048;

    //
    // System types
    //

    /**
     * Constant for other OS
     */
    public static final int OS_OTHER = 0;

    /**
     * Constant for Windows
     */
    public static final int OS_WINDOWS = 1;

    /**
     * Constant for Mac OS X
     */
    public static final int OS_MAC_OS_X = 2;

    /**
     * Constant for Linux
     */
    public static final int OS_LINUX = 3;

    //
    // Network properties
    //

    /**
     * The user agent of Gemini
     */
    public static final String NETWORK_USER_AGENT = "Gemini v" + VERSION;

}
