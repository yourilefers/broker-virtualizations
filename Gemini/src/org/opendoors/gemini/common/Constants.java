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
