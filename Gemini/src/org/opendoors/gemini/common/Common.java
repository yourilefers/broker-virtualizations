package org.opendoors.gemini.common;

/**
 * Contributors:
 * yourilefers
 *
 * @since v1.0
 */
public class Common {

    /**
     * Get the current OS type.
     *
     * @return
     */
    public static int getOSType() {

        // Get system name
        String name = System.getProperty("os.name").toLowerCase();

        // Return the system type
        if(name.contains("win")) return Constants.OS_WINDOWS;
        else if(name.contains("mac")) return Constants.OS_MAC_OS_X;
        else return Constants.OS_LINUX;

    }



}
